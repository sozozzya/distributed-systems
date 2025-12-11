package org.dotsandboxessoap.game.model;

public class Move {
    public int x;
    public int y;
    public boolean horizontal;

    public Move() {
    }

    public Move(int x, int y, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }
}
