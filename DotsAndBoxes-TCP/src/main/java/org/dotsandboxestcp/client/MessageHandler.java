package org.dotsandboxestcp.client;

import org.dotsandboxestcp.game.*;
import org.dotsandboxestcp.protocol.*;

import java.util.function.Consumer;

public class MessageHandler {
    private final ClientState state;
    private final Consumer<GameState> onStateUpdate;
    private final Consumer<String> onSystemMessage;
    private final Consumer<GameState> onGameOver;
    private final Consumer<PlayerColor> onAssignColor;

    public MessageHandler(
            ClientState state,
            Consumer<GameState> onStateUpdate,
            Consumer<String> onSystemMessage,
            Consumer<GameState> onGameOver,
            Consumer<PlayerColor> onAssignColor
    ) {
        this.state = state;
        this.onStateUpdate = onStateUpdate;
        this.onSystemMessage = onSystemMessage;
        this.onGameOver = onGameOver;
        this.onAssignColor = onAssignColor;
    }

    public void handle(Message msg) {
        if (msg == null) {
            System.out.println("[Client] ERROR Received null message");
            return;
        }

        String type = msg.getType();
        System.out.println("[Client] INFO Handling message type=" + type);

        switch (type) {

            case MessageType.ASSIGN_COLOR:
                String c = msg.getAsString("color");
                if (c == null) {
                    System.out.println("[Client] ERROR ASSIGN_COLOR without color");
                    return;
                }
                PlayerColor pc;
                try {
                    pc = PlayerColor.valueOf(c);
                } catch (Exception e) {
                    System.out.println("[Client] ERROR Invalid color: " + c);
                    return;
                }
                System.out.println("[Client] INFO Assigned color received: " + pc);
                state.setMyColor(pc);
                onAssignColor.accept(pc);
                break;

            case MessageType.START_GAME:
                System.out.println("[Client] INFO Start game message received");
                GameState gs = MessageSerializer.fromJson(msg.getAsString("state"), GameState.class);
                state.setGameState(gs);
                onStateUpdate.accept(gs);
                onSystemMessage.accept("Game started");
                break;

            case MessageType.UPDATE_STATE:
                System.out.println("[Client] INFO GameState updated from server");
                GameState gs2 = MessageSerializer.fromJson(msg.getAsString("state"), GameState.class);
                state.setGameState(gs2);
                onStateUpdate.accept(gs2);
                break;

            case MessageType.GAME_OVER:
                System.out.println("[Client] INFO GAME OVER received");
                GameState gs3 = MessageSerializer.fromJson(msg.getAsString("state"), GameState.class);
                state.setGameState(gs3);
                onGameOver.accept(gs3);
                break;

            default:
                System.out.println("[Client] WARN Unknown message type: " + type);
                break;
        }
    }
}
