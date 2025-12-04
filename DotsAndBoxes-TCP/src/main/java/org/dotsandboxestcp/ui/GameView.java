package org.dotsandboxestcp.ui;

import javafx.scene.Cursor;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.dotsandboxestcp.game.Edge;
import org.dotsandboxestcp.game.GameState;
import org.dotsandboxestcp.game.Move;
import org.dotsandboxestcp.client.ClientConnection;
import org.dotsandboxestcp.game.PlayerColor;

import java.util.*;
import java.util.function.Supplier;

public class GameView {
    private final GridPane grid;
    private final GameState gameState;
    private final ClientConnection connection;
    private static final int CELL = 40;
    private static final int DOT_SIZE = 8;
    private static final int LINE_THICKNESS = 4;
    private final Map<String, Line> lineMap = new HashMap<>();
    private final Supplier<PlayerColor> myColorProvider;

    public GameView(GridPane grid, GameState gameState, ClientConnection connection, Supplier<PlayerColor> myColorProvider) {
        this.grid = grid;
        this.gameState = gameState;
        this.connection = connection;
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
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Circle dot = UIUtils.createDot(DOT_SIZE);
                grid.add(dot, c * 2, r * 2);
                if (c < size - 1) {
                    Line hLine = UIUtils.createLine(CELL, LINE_THICKNESS);
                    String key = "h:" + r + ":" + c;
                    lineMap.put(key, hLine);
                    if (gameState.isHorizontalTaken(r, c)) {
                        hLine.setStroke(gameState.isRedTurnLine(r, c, true) ? Color.RED : Color.BLUE);
                        hLine.setCursor(Cursor.DEFAULT);
                    } else {
                        int rr = r, cc = c;
                        hLine.setOnMouseClicked(e -> handleClick(rr, cc, true));
                    }
                    grid.add(hLine, c * 2 + 1, r * 2);
                }
                if (r < size - 1) {
                    Line vLine = UIUtils.createVerticalLine(CELL, LINE_THICKNESS);
                    String key = "v:" + r + ":" + c;
                    lineMap.put(key, vLine);
                    if (gameState.isVerticalTaken(r, c)) {
                        vLine.setStroke(gameState.isRedTurnLine(r, c, false) ? Color.RED : Color.BLUE);
                        vLine.setCursor(Cursor.DEFAULT);
                    } else {
                        int rr = r, cc = c;
                        vLine.setOnMouseClicked(e -> handleClick(rr, cc, false));
                    }
                    grid.add(vLine, c * 2, r * 2 + 1);
                }
            }
        }
        drawCompletedSquares();
    }

    private void handleClick(int r, int c, boolean isHorizontal) {
        PlayerColor myColor = myColorProvider.get();
        System.out.println("[UI] Click event: r=" + r + ", c=" + c + ", horizontal=" + isHorizontal + " | myColor=" + myColor + " | turn=" + gameState.getCurrentTurn());
        if (myColor != gameState.getCurrentTurn()) {
            System.out.println("[UI] Move rejected — not your turn");
            return;
        }
        Edge e = new Edge(c, r, isHorizontal);
        if (gameState.isEdgePresent(e)) {
            System.out.println("[UI] Move rejected — edge already taken");
            return;
        }
        gameState.addEdge(e, myColor);
        String key = (isHorizontal ? "h:" : "v:") + r + ":" + c;
        Line line = lineMap.get(key);
        if (line != null) {
            line.setStroke(myColor == PlayerColor.RED ? Color.RED : Color.BLUE);
            line.setCursor(Cursor.DEFAULT);
        }
        drawCompletedSquares();
        connection.sendMove(new Move(c, r, isHorizontal));
        System.out.println("[CLIENT] Move sent to server");
    }

    private void drawCompletedSquares() {
        int size = gameState.getSize() - 1;
        grid.getChildren().removeIf(node -> node instanceof Rectangle);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (gameState.isSquareOwned(r, c)) {
                    Rectangle rect = new Rectangle(CELL, CELL);
                    rect.setFill(gameState.isSquareRed(r, c) ? Color.rgb(255, 80, 80, 0.5) : Color.rgb(80, 80, 255, 0.5));
                    grid.add(rect, c * 2 + 1, r * 2 + 1);
                    System.out.println("[GAME] Square filled at (" + r + "," + c + ")");
                }
            }
        }
    }

    public void updateUI() {
        System.out.println("[UI] Updating UI");
        for (Map.Entry<String, Line> entry : lineMap.entrySet()) {
            String[] parts = entry.getKey().split(":");
            boolean horizontal = parts[0].equals("h");
            int r = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);
            Edge e = new Edge(c, r, horizontal);
            Line line = entry.getValue();
            if (gameState.isEdgePresent(e)) {
                PlayerColor owner = gameState.getEdgeOwner(e);
                line.setStroke(owner == PlayerColor.RED ? Color.RED : Color.BLUE);
                line.setCursor(Cursor.DEFAULT);
            }
        }
        drawCompletedSquares();
    }
}
