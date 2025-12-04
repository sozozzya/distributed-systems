package org.dotsandboxestcp.game;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    public record Result(boolean valid, int squaresCaptured) {
    }

    public static Result applyMove(GameState state, Move move, PlayerColor player) {
        if (move == null) {
            System.out.println("[GAME][GameLogic] Null move rejected");
            return new Result(false, 0);
        }

        System.out.println("[GAME][GameLogic] Applying move: (" + move.x + "," + move.y +
                ") horizontal=" + move.horizontal + " player=" + player);

        if (player != state.getCurrentTurn()) {
            System.out.println("[GAME][GameLogic] Invalid move — not player's turn: " + player);
            return new Result(false, 0);
        }

        Edge e = new Edge(move.x, move.y, move.horizontal);

        if (state.isEdgePresent(e)) {
            System.out.println("[GAME][GameLogic] Invalid move — edge already exists");
            return new Result(false, 0);
        }

        state.addEdge(e, player);
        System.out.println("[GAME][GameLogic] Edge added to state");

        int captured = captureSquares(state, e, player);
        System.out.println("[GAME][GameLogic] Squares captured: " + captured);

        state.addScore(player, captured);

        if (state.isFinished()) {
            System.out.println("[GAME][GameState] Game finished!");
            state.setCurrentTurn(PlayerColor.NONE);
        } else {
            if (captured == 0) {
                PlayerColor next = player.opposite();
                state.setCurrentTurn(next);
                System.out.println("[GAME][GameLogic] No squares captured. Turn switched to " + state.getCurrentTurn());
            } else {
                System.out.println("[GAME][GameLogic] Player " + player + " gets another move (captured > 0)");
            }
        }

        return new Result(true, captured);
    }

    private static int captureSquares(GameState state, Edge e, PlayerColor player) {
        int captured = 0;
        int cellSize = state.getCellSize();
        List<int[]> affectedCells = new ArrayList<>();
        if (e.horizontal) {
            int cx = e.x;
            int cyAbove = e.y - 1;
            int cyBelow = e.y;
            if (cyAbove >= 0 && cyAbove < cellSize && cx >= 0 && cx < cellSize)
                affectedCells.add(new int[]{cx, cyAbove});
            if (cyBelow >= 0 && cyBelow < cellSize && cx >= 0 && cx < cellSize)
                affectedCells.add(new int[]{cx, cyBelow});
        } else {
            int cy = e.y;
            int cxLeft = e.x - 1;
            int cxRight = e.x;
            if (cxLeft >= 0 && cxLeft < cellSize && cy >= 0 && cy < cellSize) affectedCells.add(new int[]{cxLeft, cy});
            if (cxRight >= 0 && cxRight < cellSize && cy >= 0 && cy < cellSize)
                affectedCells.add(new int[]{cxRight, cy});
        }
        for (int[] c : affectedCells) {
            int cx = c[0], cy = c[1];
            if (isCellComplete(state, cx, cy) && !state.getCells()[cy][cx].isCaptured()) {
                System.out.println("[GAME][GameLogic] Cell (" + cx + "," + cy + ") completed by " + player);
                state.getCells()[cy][cx].setOwner(player);
                captured++;
            }
        }
        return captured;
    }

    private static boolean isCellComplete(GameState state, int cx, int cy) {
        if (!state.isValidCell(cy, cx)) {
            return false;
        }
        Edge top = new Edge(cx, cy, true);
        Edge bottom = new Edge(cx, cy + 1, true);
        Edge left = new Edge(cx, cy, false);
        Edge right = new Edge(cx + 1, cy, false);
        boolean complete = state.isEdgePresent(top) && state.isEdgePresent(bottom) && state.isEdgePresent(left) && state.isEdgePresent(right);
        if (complete) System.out.println("[GAME][GameLogic] Cell complete (" + cx + "," + cy + ")");
        return complete;
    }
}
