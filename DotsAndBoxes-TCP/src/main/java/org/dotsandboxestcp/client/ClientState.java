package org.dotsandboxestcp.client;

import org.dotsandboxestcp.game.GameState;
import org.dotsandboxestcp.game.PlayerColor;

public class ClientState {
    private GameState gameState;
    private PlayerColor myColor = PlayerColor.NONE;
    private boolean connected = false;

    public void setGameState(GameState s) {
        System.out.println("[Client] INFO GameState updated");
        this.gameState = s;
    }

    public PlayerColor getMyColor() {
        return myColor;
    }

    public void setMyColor(PlayerColor myColor) {
        System.out.println("[Client] INFO Assigned color: " + myColor);
        this.myColor = myColor;
    }

    public void setConnected(boolean connected) {
        System.out.println("[Client] INFO Connection status changed: " + connected);
        this.connected = connected;
    }
}
