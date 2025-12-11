package org.dotsandboxessoap.game;

public enum PlayerColor {
    RED, BLUE, NONE;

    public PlayerColor opposite() {
        if (this == RED) return BLUE;
        if (this == BLUE) return RED;
        return NONE;
    }
}
