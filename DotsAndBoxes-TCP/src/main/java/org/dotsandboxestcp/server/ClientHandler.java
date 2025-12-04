package org.dotsandboxestcp.server;

import org.dotsandboxestcp.game.GameLogic;
import org.dotsandboxestcp.game.GameState;
import org.dotsandboxestcp.game.Move;
import org.dotsandboxestcp.game.PlayerColor;
import org.dotsandboxestcp.protocol.Message;
import org.dotsandboxestcp.protocol.MessageType;
import org.dotsandboxestcp.protocol.MessageSerializer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ServerState serverState;
    private BufferedReader in;
    private PrintWriter out;
    private String playerId;
    private PlayerColor assignedColor;

    public ClientHandler(Socket socket, ServerState serverState) {
        this.socket = socket;
        this.serverState = serverState;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            playerId = socket.getRemoteSocketAddress().toString();
            System.out.println("[CLIENT] Connected: " + playerId);
            serverState.addClient(this);
            synchronized (this) {
                while (assignedColor == null) {
                    wait();
                }
            }
            System.out.println("[CLIENT] Assigned color to " + playerId + ": " + assignedColor);
            sendMessage(new Message(MessageType.ASSIGN_COLOR).add("color", assignedColor.name()));
            String line;
            while ((line = in.readLine()) != null) {
                Message msg = MessageSerializer.fromJson(line);
                handleMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Client disconnected unexpectedly: " + playerId + " - " + e.getMessage());
        } finally {
            System.out.println("[CLIENT] Removed client: " + playerId);
            serverState.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleMessage(Message msg) {
        if (msg == null) return;
        switch (msg.getType()) {
            case MessageType.MOVE:
                handleMove(msg);
                break;
            case MessageType.RESTART:
                handleRestart();
                break;
            default:
                System.out.println("[SERVER] Unknown message type from " + playerId + ": " + msg.getType());
                break;
        }
    }

    private void handleMove(Message msg) {
        Move move = MessageSerializer.fromJson(msg.getAsString("move"), Move.class);
        if (assignedColor == null) {
            System.out.println("[ERROR] Move received from unassigned client: " + playerId);
            return;
        }

        synchronized (serverState) {
            GameState gs = serverState.getGameState();
            GameLogic.Result res = GameLogic.applyMove(gs, move, assignedColor);

            System.out.println("[MOVE] From " + assignedColor + ": " + move);
            System.out.println("[GAME] Move result: " + res + ", nextTurn=" + gs.getCurrentTurn());

            serverState.broadcast(new Message(MessageType.UPDATE_STATE)
                    .add("state", MessageSerializer.toJson(gs))
                    .add("lastMove", MessageSerializer.toJson(move))
                    .add("scoreRed", gs.getScore(PlayerColor.RED))
                    .add("scoreBlue", gs.getScore(PlayerColor.BLUE)));

            if (gs.isFinished()) {
                serverState.broadcast(new Message(MessageType.GAME_OVER)
                        .add("state", MessageSerializer.toJson(gs)));
            }
        }
    }

    private void handleRestart() {
        System.out.println("[GAME] Restart requested by " + assignedColor);

        synchronized (serverState) {
            serverState.resetGame();
            System.out.println("[GAME] Game reset complete");
            serverState.broadcastStartGame();
        }
    }

    public void sendMessage(Message msg) {
        out.println(MessageSerializer.toJson(msg));
    }

    public void assignColor(PlayerColor color) {
        this.assignedColor = color;
        synchronized (this) {
            notifyAll();
        }
    }

    public String getPlayerId() {
        return playerId;
    }
}
