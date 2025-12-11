package org.dotsandboxessoap.game.model;

import org.dotsandboxessoap.game.PlayerColor;

public class Cell {
    private PlayerColor owner = PlayerColor.NONE;

    public Cell() {
    }

    public Cell(Cell other) {
        if (other != null) this.owner = other.owner;
    }

    public PlayerColor getOwner() {
        return owner;
    }

    public void setOwner(PlayerColor owner) {
        this.owner = owner;
    }

    public boolean isCaptured() {
        return owner != PlayerColor.NONE;
    }
}
