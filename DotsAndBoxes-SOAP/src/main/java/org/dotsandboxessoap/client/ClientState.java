package org.dotsandboxessoap.client;

import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.PlayerColor;

public class ClientState {
    private GameStateDTO gameState;
    private PlayerColor myColor = PlayerColor.NONE;
    private boolean connected = false;

    private ClientSoap soap;

    public void setSoap(ClientSoap soap) {
        this.soap = soap;
    }

    public void setGameState(GameStateDTO s) {
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
