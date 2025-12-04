package org.dotsandboxestcp.game;

import java.util.*;
import java.util.Set;

public class GameState {
    public int size;
    public Set<Edge> edges = new HashSet<>();
    public Cell[][] cells;
    public PlayerColor currentTurn = PlayerColor.RED;
    public int scoreRed = 0;
    public int scoreBlue = 0;
    public Map<String, PlayerColor> edgeOwners = new HashMap<>();

    public GameState() {
    }

    public GameState(int size) {
        this.size = size;
        int cellSize = getCellSize();
        this.cells = new Cell[cellSize][cellSize];
        for (int y = 0; y < cellSize; y++)
            for (int x = 0; x < cellSize; x++)
                cells[y][x] = new Cell();

        System.out.println("[GAME][GameState] New game created");
    }

    public int getSize() {
        return size;
    }

    public Set<Edge> getEdges() {
        return edges;
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

    public void setCurrentTurn(PlayerColor p) {
        System.out.println("[GAME][GameState] Turn set to " + p);
        currentTurn = p;
    }

    public int getScore(PlayerColor p) {
        if (p == PlayerColor.RED) return scoreRed;
        if (p == PlayerColor.BLUE) return scoreBlue;
        return 0;
    }

    public void addScore(PlayerColor p, int delta) {
        if (delta > 0)
            System.out.println("[GAME][GameState] Score +" + delta + " for " + p);

        if (p == PlayerColor.RED) scoreRed += delta;
        if (p == PlayerColor.BLUE) scoreBlue += delta;
    }

    private String edgeKey(Edge e) {
        return e.x + ":" + e.y + ":" + (e.horizontal ? "h" : "v");
    }

    public boolean isEdgePresent(Edge e) {
        return edgeOwners.containsKey(edgeKey(e)) || edges.contains(e);
    }

    public void addEdge(Edge e, PlayerColor owner) {
        if (isEdgePresent(e)) {
            System.out.println("[GAME][GameState] addEdge skipped: already present (" + e.x + "," + e.y + ", h=" + e.horizontal + ")");
            return;
        }

        System.out.println("[GAME][GameState] addEdge: (" + e.x + "," + e.y +
                ") horizontal=" + e.horizontal + ", owner=" + owner);

        edges.add(e);
        edgeOwners.put(edgeKey(e), owner);
    }

    public PlayerColor getEdgeOwner(Edge e) {
        PlayerColor p = edgeOwners.get(edgeKey(e));
        return p == null ? PlayerColor.NONE : p;
    }

    public boolean isValidCell(int row, int col) {
        if (cells == null) return false;
        return row >= 0 && row < cells.length && col >= 0 && col < cells.length;
    }

    public boolean isRedTurnLine(int r, int c, boolean horizontal) {
        Edge e = new Edge(c, r, horizontal);
        PlayerColor owner = getEdgeOwner(e);
        return owner == PlayerColor.RED;
    }

    public boolean isHorizontalTaken(int r, int c) {
        Edge e = new Edge(c, r, true);
        return isEdgePresent(e);
    }

    public boolean isVerticalTaken(int r, int c) {
        Edge e = new Edge(c, r, false);
        return isEdgePresent(e);
    }

    public boolean isSquareOwned(int r, int c) {
        return cells[r][c].isCaptured();
    }

    public boolean isSquareRed(int r, int c) {
        return cells[r][c].getOwner() == PlayerColor.RED;
    }

    public boolean isFinished() {
        int cellSize = getCellSize();
        for (int y = 0; y < cellSize; y++) {
            for (int x = 0; x < cellSize; x++) {
                if (!cells[y][x].isCaptured()) {
                    return false;
                }
            }
        }
        System.out.println("[GAME][GameState] Game finished!");
        return true;
    }

    public boolean allCellsEmpty() {
        int n = size - 1;
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (cells[r][c].getOwner() != PlayerColor.NONE) return false;
        return true;
    }

    public PlayerColor getWinner() {
        int r = getScore(PlayerColor.RED);
        int b = getScore(PlayerColor.BLUE);
        if (r > b) return PlayerColor.RED;
        if (b > r) return PlayerColor.BLUE;
        return PlayerColor.NONE;
    }
}