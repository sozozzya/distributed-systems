package org.dotsandboxessoap.game.model;

import org.dotsandboxessoap.game.PlayerColor;

import java.util.*;

public class GameState {
    private int size;
    private Set<Edge> edges = new HashSet<>();
    private Map<String, PlayerColor> edgeOwners = new HashMap<>();
    private Cell[][] cells;
    private PlayerColor currentTurn = PlayerColor.RED;
    private int scoreRed = 0;
    private int scoreBlue = 0;

    public GameState() {
    }

    public GameState(int size) {
        this.size = size;
        int cellSize = getCellSize();
        this.cells = new Cell[cellSize][cellSize];
        for (int col = 0; col < cellSize; col++)
            for (int row = 0; row < cellSize; row++)
                cells[col][row] = new Cell();
    }

    public int getSize() {
        return size;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Map<String, PlayerColor> getEdgeOwners() {
        return edgeOwners;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getCellSize() {
        return Math.max(0, size - 1);
    }

    public PlayerColor getCurrentTurn() {
        return currentTurn;
    }

    public int getScore(PlayerColor p) {
        if (p == PlayerColor.RED) return scoreRed;
        if (p == PlayerColor.BLUE) return scoreBlue;
        return 0;
    }

    public void setCurrentTurn(PlayerColor p) {
        currentTurn = p;
    }

    public void setScore(PlayerColor p, int value) {
        if (p == PlayerColor.RED) {
            this.scoreRed = value;
        } else if (p == PlayerColor.BLUE) {
            this.scoreBlue = value;
        }
    }

    public void addScore(PlayerColor p, int delta) {
        if (p == PlayerColor.RED) scoreRed += delta;
        if (p == PlayerColor.BLUE) scoreBlue += delta;
    }

    private String edgeKey(Edge e) {
        return e.x + ":" + e.y + ":" + (e.horizontal ? "h" : "v");
    }

    public boolean isEdgePresent(Edge e) {
        return edgeOwners.containsKey(edgeKey(e));
    }

    public void addEdge(Edge e, PlayerColor owner) {
        edges.add(e);
        edgeOwners.put(edgeKey(e), owner);
    }

    public PlayerColor getEdgeOwner(Edge e) {
        PlayerColor p = edgeOwners.get(edgeKey(e));
        return p == null ? PlayerColor.NONE : p;
    }

    public boolean isValidCell(int col, int row) {
        if (cells == null) return false;
        return col >= 0 && col < cells.length && row >= 0 && row < cells.length;
    }

    public boolean isRedTurnLine(int col, int row, boolean horizontal) {
        Edge e = new Edge(col, row, horizontal);
        PlayerColor owner = getEdgeOwner(e);
        return owner == PlayerColor.RED;
    }

    public boolean isHorizontalTaken(int col, int row) {
        Edge e = new Edge(col, row, true);
        return isEdgePresent(e);
    }

    public boolean isVerticalTaken(int col, int row) {
        Edge e = new Edge(col, row, false);
        return isEdgePresent(e);
    }

    public boolean isCellOwned(int col, int row) {
        return cells[col][row].isCaptured();
    }

    public boolean isCellRed(int col, int row) {
        return cells[col][row].getOwner() == PlayerColor.RED;
    }

    public boolean isFinished() {
        int cellSize = getCellSize();
        for (int col = 0; col < cellSize; col++) {
            for (int row = 0; row < cellSize; row++) {
                if (!cells[col][row].isCaptured()) {
                    return false;
                }
            }
        }
        return true;
    }

    public PlayerColor getWinner() {
        int r = getScore(PlayerColor.RED);
        int b = getScore(PlayerColor.BLUE);
        if (r > b) return PlayerColor.RED;
        if (b > r) return PlayerColor.BLUE;
        return PlayerColor.NONE;
    }

    public void copyFrom(GameState other) {
        if (other == null) return;

        this.size = other.size;

        int cellSize = other.getCellSize();
        this.cells = new Cell[cellSize][cellSize];
        for (int col = 0; col < cellSize; col++) {
            for (int row = 0; row < cellSize; row++) {
                this.cells[col][row] = new Cell(other.cells[col][row]);
            }
        }

        this.edges = new HashSet<>();
        for (Edge e : other.edges) {
            this.edges.add(new Edge(e));
        }

        this.edgeOwners = new HashMap<>();
        this.edgeOwners.putAll(other.edgeOwners);

        this.currentTurn = other.currentTurn;
        this.scoreRed = other.scoreRed;
        this.scoreBlue = other.scoreBlue;
    }
}
