package org.dotsandboxestcp.ui;

import org.dotsandboxestcp.client.ClientConnection;
import org.dotsandboxestcp.client.ClientState;
import org.dotsandboxestcp.client.MessageHandler;
import org.dotsandboxestcp.game.Edge;
import org.dotsandboxestcp.game.GameState;
import org.dotsandboxestcp.game.PlayerColor;
import org.dotsandboxestcp.protocol.Message;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameController {
    @FXML
    private GridPane boardPane;
    @FXML
    private Label lblTurn;
    @FXML
    private Label lblScoreRed;
    @FXML
    private Label lblScoreBlue;
    @FXML
    private Button btnRestart;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField tfServerHost;
    @FXML
    private TextField tfServerPort;
    @FXML
    private Label lblMyColor;
    private Stage stage;
    private ClientConnection connection;
    private final ClientState clientState = new ClientState();
    private MessageHandler msgHandler;
    private GameState gameState;
    private GameView gameView;

    public void setStage(Stage s) {
        this.stage = s;
    }

    @FXML
    public void initialize() {
        tfServerHost.setText("localhost");
        tfServerPort.setText("9000");
        btnRestart.setDisable(true);
        msgHandler = new MessageHandler(clientState, this::onStateUpdate, this::onSystemMessage, this::onGameOver, this::onAssignColor);
        System.out.println("[UI] Initialized GameController");
    }

    @FXML
    private void onConnectClicked() {
        String host = tfServerHost.getText().trim();
        if (host.isEmpty()) {
            onSystemMessage("Host cannot be empty");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(tfServerPort.getText().trim());
        } catch (NumberFormatException ex) {
            onSystemMessage("Invalid port number");
            return;
        }
        connection = new ClientConnection(host, port, this::onRawMessage);
        try {
            connection.connect();
            clientState.setConnected(true);
            btnConnect.setDisable(true);
            btnRestart.setDisable(false);
            onSystemMessage("Connected to " + host + ":" + port);
            System.out.println("[CLIENT] Connection established to " + host + ":" + port);
            gameState = new GameState(6);
            gameView = new GameView(boardPane, gameState, connection, this::getMyColor);
            gameView.updateUI();
        } catch (IOException e) {
            onSystemMessage("Failed to connect: " + e.getMessage());
            System.out.println("[ERROR] Connection failed: " + e.getMessage());
        }
    }

    private void onRawMessage(Message msg) {
        msgHandler.handle(msg);
    }

    public PlayerColor getMyColor() {
        return clientState.getMyColor();
    }

    private void onAssignColor(PlayerColor color) {
        Platform.runLater(() -> {
            clientState.setMyColor(color);
            lblMyColor.setText(color.name());
            System.out.println("[CLIENT] Assigned color: " + color);
        });
    }

    private void onSystemMessage(String s) {
        System.out.println("[SYSTEM] " + s);
    }

    private void onGameOver(GameState gs) {
        Platform.runLater(() -> {
            gameView.updateUI();
            PlayerColor winnerColor = gs.getWinner();
            String winnerText;
            if (winnerColor == PlayerColor.RED) winnerText = "Победил красный)";
            else if (winnerColor == PlayerColor.BLUE) winnerText = "Победил синий)";
            else winnerText = "Ничья)";

            Alert a = new Alert(Alert.AlertType.INFORMATION, winnerText);
            a.setHeaderText("Игра окончена!");
            a.showAndWait();
            System.out.println("[GAME] Game over: " + winnerText);
        });
    }

    private void onStateUpdate(GameState serverState) {
        Platform.runLater(() -> {

            boolean isStartGame =
                    serverState.getEdges().isEmpty()
                            && serverState.allCellsEmpty();

            if (isStartGame) {
                gameState = new GameState(serverState.getSize());
                gameView = new GameView(boardPane, gameState, connection, this::getMyColor);
                gameView.updateUI();

                lblTurn.setText(gameState.getCurrentTurn().name());
                lblScoreRed.setText("0");
                lblScoreBlue.setText("0");

                System.out.println("[GAME] FULL UI REBUILD (start game)");
                return;
            }

            syncStateFromServer(serverState);

            lblTurn.setText(gameState.getCurrentTurn().name());
            lblScoreRed.setText(String.valueOf(gameState.getScore(PlayerColor.RED)));
            lblScoreBlue.setText(String.valueOf(gameState.getScore(PlayerColor.BLUE)));
            System.out.println("[GAME] State updated: Turn=" + gameState.getCurrentTurn()
                    + ", RED=" + gameState.getScore(PlayerColor.RED)
                    + ", BLUE=" + gameState.getScore(PlayerColor.BLUE));
        });
    }

    private void syncStateFromServer(GameState serverState) {
        System.out.println("[GAME] Syncing state from server");

        gameState.setCurrentTurn(serverState.getCurrentTurn());
        System.out.println("[GAME] Current turn set to " + serverState.getCurrentTurn());

        int oldRed = gameState.getScore(PlayerColor.RED);
        int oldBlue = gameState.getScore(PlayerColor.BLUE);
        int serverRed = serverState.getScore(PlayerColor.RED);
        int serverBlue = serverState.getScore(PlayerColor.BLUE);
        if (serverRed != oldRed) gameState.addScore(PlayerColor.RED, serverRed - oldRed);
        if (serverBlue != oldBlue) gameState.addScore(PlayerColor.BLUE, serverBlue - oldBlue);

        for (Edge e : serverState.getEdges()) {
            PlayerColor owner = serverState.getEdgeOwner(e);
            if (!gameState.isEdgePresent(e)) {
                gameState.addEdge(e, owner);
                System.out.println("[GAME] Edge added from server: " + e.x + "," + e.y + ", h=" + e.horizontal + ", owner=" + owner);
            } else {
                PlayerColor localOwner = gameState.getEdgeOwner(e);
                if (localOwner == null && owner != null) {
                    gameState.addEdge(e, owner);
                    System.out.println("[GAME] Edge owner fixed from server: " + e.x + "," + e.y + " -> " + owner);
                }
            }
        }

        Set<Edge> toRemove = new HashSet<>();
        for (Edge e : gameState.getEdges()) {
            if (!serverState.isEdgePresent(e)) {
                toRemove.add(e);
            }
        }
        for (Edge e : toRemove) {
            gameState.getEdges().remove(e);
            gameState.edgeOwners.remove(e.x + ":" + e.y + ":" + (e.horizontal ? "h" : "v"));
            System.out.println("[GAME] Local edge removed (server says NONE): " + e);
        }

        int n = serverState.getSize() - 1;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (serverState.isSquareOwned(r, c) && !gameState.isSquareOwned(r, c)) {
                    PlayerColor owner = serverState.isSquareRed(r, c) ? PlayerColor.RED : PlayerColor.BLUE;
                    gameState.getCells()[r][c].setOwner(owner);
                    System.out.println("[GAME] Square captured from server at r=" + r + ", c=" + c + " by " + owner);
                } else if (!serverState.isSquareOwned(r, c) && gameState.isSquareOwned(r, c)) {
                    gameState.getCells()[r][c].setOwner(PlayerColor.NONE);
                    System.out.println("[GAME] Local square reset at r=" + r + ", c=" + c + " (server says NONE)");
                }
            }
        }

        gameView.updateUI();
    }

    @FXML
    private void onRestartClicked() {
        if (connection != null) {
            System.out.println("[GAME] Restart requested by client");
            connection.sendRestart();
        }
    }
}
