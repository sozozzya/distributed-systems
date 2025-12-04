package org.dotsandboxestcp.game;

public class Cell {
    public PlayerColor owner = PlayerColor.NONE;

    public Cell() {
    }

    public PlayerColor getOwner() {
        return owner;
    }

    public void setOwner(PlayerColor owner) {
        System.out.println("[GAME][Cell] Cell captured by " + owner);
        this.owner = owner;
    }

    public boolean isCaptured() {
        return owner != PlayerColor.NONE;
    }
}
