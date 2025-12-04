package org.dotsandboxestcp.server;

import org.dotsandboxestcp.game.GameState;
import org.dotsandboxestcp.game.PlayerColor;
import org.dotsandboxestcp.protocol.Message;
import org.dotsandboxestcp.protocol.MessageType;
import org.dotsandboxestcp.protocol.MessageSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerState {
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>(2));
    private GameState gameState = new GameState(6);

    public synchronized void addClient(ClientHandler client) {
        if (clients.size() >= 2) {
            System.out.println("[SERVER] Rejecting client: server full");
            return;
        }

        clients.add(client);
        System.out.println("[SERVER] Client added. Total: " + clients.size());

        if (clients.size() == 1) {
            System.out.println("[SERVER] Assigned RED to first player");
            client.assignColor(PlayerColor.RED);
        } else if (clients.size() == 2) {
            System.out.println("[SERVER] Assigned BLUE to second player");
            clients.get(1).assignColor(PlayerColor.BLUE);
        }

        if (isReadyToStart()) {
            System.out.println("[GAME] Both players connected, starting game");
            broadcastStartGame();
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("[SERVER] Client removed. Total: " + clients.size());
    }

    public synchronized boolean isReadyToStart() {
        return clients.size() == 2;
    }

    public void broadcast(Message msg) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                try {
                    c.sendMessage(msg);
                } catch (Exception e) {
                    System.out.println("[ERROR] Failed to send message: " + c.getPlayerId());
                }
            }
        }
    }

    public synchronized void broadcastStartGame() {
        for (ClientHandler client : new ArrayList<>(clients)) {
            try {
                Message msg = new Message(MessageType.START_GAME)
                        .add("state", MessageSerializer.toJson(gameState));
                System.out.println("[TCP] Sending START_GAME to " + client.getPlayerId() +
                        ": " + MessageSerializer.toJson(msg));
                client.sendMessage(msg);
            } catch (Exception e) {
                System.out.println("[TCP][ERROR] Failed to send START_GAME to " +
                        client.getPlayerId() + ": " + e.getMessage());
            }
        }
    }

    public synchronized GameState getGameState() {
        return gameState;
    }

    public synchronized void resetGame() {
        System.out.println("[GAME] Resetting game state...");
        this.gameState = new GameState(gameState.getSize());
    }
}
