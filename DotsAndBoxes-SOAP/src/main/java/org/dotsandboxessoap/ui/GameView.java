package org.dotsandboxessoap.ui;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.dotsandboxessoap.client.ClientSoap;
import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.dto.MoveDTO;
import org.dotsandboxessoap.game.mapping.GameStateMapper;
import org.dotsandboxessoap.game.mapping.MoveMapper;
import org.dotsandboxessoap.game.model.Edge;
import org.dotsandboxessoap.game.model.GameState;
import org.dotsandboxessoap.game.model.Move;
import org.dotsandboxessoap.game.PlayerColor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class GameView {
    private final GridPane grid;
    private final GameState gameState;
    private final ClientSoap soap;
    private static final int CELL = 40;
    private static final int DOT_SIZE = 8;
    private static final int LINE_THICKNESS = 4;
    private final Map<String, Line> lineMap = new HashMap<>();
    private final Supplier<PlayerColor> myColorProvider;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public GameView(GridPane grid, GameState gameState, ClientSoap soap, Supplier<PlayerColor> myColorProvider) {
        this.grid = grid;
        this.gameState = gameState;
        this.soap = soap;
        this.myColorProvider = myColorProvider;

        System.out.println("[UI] Initializing GameView");
        configureGrid();
        buildGrid();
    }

    private void configureGrid() {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        int size = gameState.getSize() * 2 - 1;
        for (int i = 0; i < size; i++) {
            UIUtils.addColumn(grid, CELL);
            UIUtils.addRow(grid, CELL);
        }
    }

    private void buildGrid() {
        grid.getChildren().clear();
        lineMap.clear();
        int size = gameState.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Circle dot = UIUtils.createDot(DOT_SIZE);
                grid.add(dot, x * 2, y * 2);
                if (x < size - 1) {
                    Line hLine = UIUtils.createLine(CELL, LINE_THICKNESS);
                    String key = "h:" + x + ":" + y;
                    lineMap.put(key, hLine);
                    if (gameState.isHorizontalTaken(x, y)) {
                        hLine.setStroke(gameState.isRedTurnLine(x, y, true) ? Color.RED : Color.BLUE);
                        hLine.setCursor(Cursor.DEFAULT);
                    } else {
                        int xx = x, yy = y;
                        hLine.setOnMouseClicked(e -> handleClick(xx, yy, true));
                    }
                    grid.add(hLine, x * 2 + 1, y * 2);
                }
                if (y < size - 1) {
                    Line vLine = UIUtils.createVerticalLine(CELL, LINE_THICKNESS);
                    String key = "v:" + x + ":" + y;
                    lineMap.put(key, vLine);
                    if (gameState.isVerticalTaken(x, y)) {
                        vLine.setStroke(gameState.isRedTurnLine(x, y, false) ? Color.RED : Color.BLUE);
                        vLine.setCursor(Cursor.DEFAULT);
                    } else {
                        int xx = x, yy = y;
                        vLine.setOnMouseClicked(e -> handleClick(xx, yy, false));
                    }
                    grid.add(vLine, x * 2, y * 2 + 1);
                }
            }
        }
        drawCompletedCells();
    }

    private void handleClick(int x, int y, boolean isHorizontal) {
        PlayerColor myColor = myColorProvider.get();
        System.out.println("[UI] Click event: x=" + x + ", y=" + y + ", horizontal=" + isHorizontal + " | myColor=" + myColor + " | turn=" + gameState.getCurrentTurn());
        if (myColor != gameState.getCurrentTurn()) {
            System.out.println("[UI] Not your turn");
            return;
        }
        Move move = new Move(x, y, isHorizontal);
        MoveDTO moveDTO = MoveMapper.toDTO(move);

        String optimisticKey = (isHorizontal ? "h:" : "v:") + x + ":" + y;
        Line optimisticLine = lineMap.get(optimisticKey);
        if (optimisticLine != null) {
            optimisticLine.setStroke(Color.GRAY);
            optimisticLine.setCursor(Cursor.DEFAULT);
        }

        exec.submit(() -> {
            try {
                GameStateDTO newDto = soap.makeMove(moveDTO);
                if (newDto == null) {
                    System.out.println("[SOAP] makeMove returned null");
                    Platform.runLater(() -> {
                        if (optimisticLine != null) optimisticLine.setStroke(Color.LIGHTGRAY);
                        if (optimisticLine != null) optimisticLine.setCursor(Cursor.HAND);
                    });
                    return;
                }
                GameState newState = GameStateMapper.fromDTO(newDto);
                Platform.runLater(() -> {
                    try {
                        gameState.copyFrom(newState);
                        updateUI();
                    } catch (Exception ex) {
                        System.out.println("[SOAP] Failed to copy game state: " + ex.getMessage());
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    System.out.println("[SOAP] Failed to send move: " + ex.getMessage());
                    if (optimisticLine != null) {
                        optimisticLine.setStroke(Color.LIGHTGRAY);
                        optimisticLine.setCursor(Cursor.HAND);
                    }
                });
            }
        });
    }

    private void drawCompletedCells() {
        int size = gameState.getSize() - 1;
        grid.getChildren().removeIf(node -> node instanceof Rectangle);
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                if (gameState.isCellOwned(col, row)) {
                    Rectangle rect = new Rectangle(CELL, CELL);
                    rect.setFill(gameState.isCellRed(col, row)
                            ? Color.rgb(255, 80, 80, 0.5)
                            : Color.rgb(80, 80, 255, 0.5));
                    grid.add(rect, col * 2 + 1, row * 2 + 1);
                }
            }
        }
    }

    public void updateUI() {
        for (Map.Entry<String, Line> entry : lineMap.entrySet()) {
            String[] parts = entry.getKey().split(":");
            boolean horizontal = parts[0].equals("h");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            Edge e = new Edge(x, y, horizontal);
            Line line = entry.getValue();

            boolean present = gameState.isEdgePresent(e);
            PlayerColor owner = gameState.getEdgeOwner(e);

            if (present) {
                line.setStroke(owner == PlayerColor.RED ? Color.RED : Color.BLUE);
                line.setCursor(Cursor.DEFAULT);
            } else {
                line.setStroke(Color.LIGHTGRAY);
                line.setCursor(Cursor.HAND);
            }
        }
        drawCompletedCells();
    }
}
