package org.dotsandboxestcp.game;

public class Move {
    public int x;
    public int y;
    public boolean horizontal;

    public Move() {
    }

    public Move(int x, int y, boolean horizontal) {
        System.out.println("[GAME][Move] Created move (" + x + "," + y + ") horizontal=" + horizontal);
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }
}
