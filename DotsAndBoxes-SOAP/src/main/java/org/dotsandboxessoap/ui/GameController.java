package org.dotsandboxessoap.ui;

import org.dotsandboxessoap.client.ClientSoap;
import org.dotsandboxessoap.client.ClientState;
import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.mapping.GameStateMapper;
import org.dotsandboxessoap.game.model.GameState;
import org.dotsandboxessoap.game.PlayerColor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    private ClientState clientState;
    private ClientSoap soap;
    private GameState localState;
    private GameView gameView;
    private boolean gameOverShown = false;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pollerFuture;
    private static final long POLL_INTERVAL_MS = 500L;

    public void setStage(Stage s) {
        this.stage = s;
        if (stage != null) {
            stage.setOnCloseRequest(evt -> {
                stopPolling();
                scheduler.shutdownNow();
            });
        }
    }

    @FXML
    public void initialize() {
        tfServerHost.setText("localhost");
        tfServerPort.setText("9000");

        btnRestart.setDisable(true);

        System.out.println("[UI] Ready for SOAP connect");
    }

    @FXML
    private void onConnectClicked() {
        String host = tfServerHost.getText().trim();
        String portText = tfServerPort.getText().trim();

        if (host.isEmpty() || portText.isEmpty()) {
            onSystemMessage("Host and port must be provided");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            onSystemMessage("Invalid port number");
            return;
        }

        String wsdlUrl = "http://" + host + ":" + port + "/game?wsdl";
        onSystemMessage("Connecting to " + wsdlUrl + " ...");
        try {
            ClientSoap cs = new ClientSoap(wsdlUrl);
            cs.register();

            ClientState st = new ClientState();
            st.setSoap(cs);

            String assigned = cs.getAssignedColor();
            if (assigned != null && !assigned.isBlank()) {
                try {
                    st.setMyColor(PlayerColor.valueOf(assigned));
                } catch (Exception ignored) {
                    st.setMyColor(PlayerColor.NONE);
                }
            } else {
                st.setMyColor(PlayerColor.NONE);
            }
            st.setConnected(true);

            this.clientState = st;
            this.soap = cs;

            initAfterConnect();

            btnConnect.setDisable(true);
            btnRestart.setDisable(false);

            onSystemMessage("Connected to " + wsdlUrl);
            System.out.println("[CLIENT] Connection established to " + wsdlUrl);
        } catch (Exception e) {
            onSystemMessage("Failed to connect: " + e.getMessage());
            System.out.println("[ERROR] Connection failed: " + e.getMessage());
        }
    }

    private void initAfterConnect() {
        if (soap == null || clientState == null) {
            onSystemMessage("Internal error: no SOAP client");
            return;
        }

        Platform.runLater(() -> {
            PlayerColor c = clientState.getMyColor();
            lblMyColor.setText(c == null ? "NONE" : c.name());
        });

        try {
            GameStateDTO dto = soap.getState();
            if (dto == null) {
                onSystemMessage("Failed to fetch initial state from server");
                return;
            }

            localState = GameStateMapper.fromDTO(dto);
            clientState.setGameState(dto);

            gameView = new GameView(boardPane, localState, soap, this::getMyColor);

            Platform.runLater(() -> {
                gameView.updateUI();
                lblTurn.setText(localState.getCurrentTurn().name());
                lblScoreRed.setText(String.valueOf(localState.getScore(PlayerColor.RED)));
                lblScoreBlue.setText(String.valueOf(localState.getScore(PlayerColor.BLUE)));
                btnRestart.setDisable(false);
            });

            startPolling();

        } catch (Exception ex) {
            onSystemMessage("Error during initialization: " + ex.getMessage());
        }
    }

    private void startPolling() {
        stopPolling();

        Runnable pollTask = () -> {
            try {
                GameStateDTO dto = soap.getState();
                if (dto != null) {
                    GameState serverState = GameStateMapper.fromDTO(dto);
                    Platform.runLater(() -> {
                        localState.copyFrom(serverState);
                        lblTurn.setText(localState.getCurrentTurn().name());
                        lblScoreRed.setText(String.valueOf(localState.getScore(PlayerColor.RED)));
                        lblScoreBlue.setText(String.valueOf(localState.getScore(PlayerColor.BLUE)));
                        if (gameView != null) gameView.updateUI();

                        if (localState.isFinished() && !gameOverShown) {
                            gameOverShown = true;
                            onGameOver(localState);
                        }
                    });
                }
            } catch (Exception ex) {
                System.err.println("[UI] Poll error: " + ex.getMessage());
            }
        };

        pollerFuture = scheduler.scheduleAtFixedRate(pollTask, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
        System.out.println("[UI] Polling started (" + POLL_INTERVAL_MS + "ms)");
    }

    private void stopPolling() {
        if (pollerFuture != null && !pollerFuture.isCancelled()) {
            pollerFuture.cancel(true);
            pollerFuture = null;
            System.out.println("[UI] Polling stopped");
        }
    }

    public PlayerColor getMyColor() {
        return clientState == null ? PlayerColor.NONE : clientState.getMyColor();
    }

    private void onSystemMessage(String s) {
        System.out.println("[SYSTEM] " + s);
    }

    private void onGameOver(GameState gs) {
        Platform.runLater(() -> {
            if (gameView != null) gameView.updateUI();
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

    @FXML
    private void onRestartClicked() {
        if (soap == null) {
            onSystemMessage("Not connected to server");
            return;
        }

        stopPolling();
        try {
            GameStateDTO dto = soap.restart();
            if (dto != null) {
                GameState newState = GameStateMapper.fromDTO(dto);
                Platform.runLater(() -> {
                    localState.copyFrom(newState);
                    lblTurn.setText(localState.getCurrentTurn().name());
                    lblScoreRed.setText(String.valueOf(localState.getScore(PlayerColor.RED)));
                    lblScoreBlue.setText(String.valueOf(localState.getScore(PlayerColor.BLUE)));
                    gameOverShown = false;
                    if (gameView != null) gameView.updateUI();
                });
            }
        } catch (Exception e) {
            System.out.println("[UI] Restart failed: " + e.getMessage());
            onSystemMessage("Restart failed: " + e.getMessage());
        } finally {
            startPolling();
        }
    }
}
