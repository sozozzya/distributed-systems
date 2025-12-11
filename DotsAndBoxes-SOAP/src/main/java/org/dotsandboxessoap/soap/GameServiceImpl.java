package org.dotsandboxessoap.soap;

import org.dotsandboxessoap.game.*;
import jakarta.jws.WebService;
import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.dto.MoveDTO;
import org.dotsandboxessoap.game.model.GameState;
import org.dotsandboxessoap.game.mapping.GameStateMapper;
import org.dotsandboxessoap.game.mapping.MoveMapper;
import org.dotsandboxessoap.game.model.Move;

import java.util.*;

@WebService(
        serviceName = "GameService",
        portName = "GameServicePort",
        targetNamespace = "http://soap/",
        endpointInterface = "org.dotsandboxessoap.soap.GameService"
)
public class GameServiceImpl implements GameService {
    private final Map<String, PlayerColor> clients = Collections.synchronizedMap(new LinkedHashMap<>());
    private final GameState gameState;
    private final int maxClients = 2;

    public GameServiceImpl() {
        this.gameState = new GameState(6);
        System.out.println("[SOAP-SERVER] GameServiceImpl initialized");
    }

    @Override
    public synchronized String registerClient() {
        if (clients.size() >= maxClients) {
            System.out.println("[SOAP-SERVER] Rejecting registration: server full");
            return "ERROR:FULL";
        }
        String clientId = UUID.randomUUID().toString();
        PlayerColor assigned = clients.isEmpty() ? PlayerColor.RED : PlayerColor.BLUE;
        clients.put(clientId, assigned);
        System.out.println("[SOAP-SERVER] Registered client " + clientId + " as " + assigned);

        if (clients.size() == maxClients) {
            System.out.println("[SOAP-SERVER] Two clients connected, starting new game");
            resetGameInternal();
        }

        return clientId + ":" + assigned.name();
    }

    @Override
    public synchronized GameStateDTO getState(String clientId) {
        return GameStateMapper.toDTO(gameState);
    }

    @Override
    public synchronized GameStateDTO makeMove(String clientId, MoveDTO moveDTO) {
        PlayerColor p = clients.get(clientId);
        if (p == null) {
            System.out.println("[SOAP-SERVER] makeMove from unknown client " + clientId);
            return GameStateMapper.toDTO(gameState);
        }
        if (gameState.getCurrentTurn() != p) {
            System.out.println("[SOAP-SERVER] Wrong turn by " + clientId);
            return GameStateMapper.toDTO(gameState);
        }
        Move move = MoveMapper.fromDTO(moveDTO);
        System.out.println("[SOAP-SERVER] makeMove by " + clientId + " (" + p + "): " + move);
        GameLogic.applyMove(gameState, move, p);

        return GameStateMapper.toDTO(gameState);
    }

    @Override
    public synchronized GameStateDTO restart(String clientId) {
        PlayerColor p = clients.get(clientId);
        if (p == null) {
            System.out.println("[SOAP-SERVER] restart from unknown client " + clientId);
            return GameStateMapper.toDTO(gameState);
        }
        System.out.println("[SOAP-SERVER] restart requested by " + clientId + " (" + p + ")");
        resetGameInternal();
        return GameStateMapper.toDTO(gameState);
    }

    private void resetGameInternal() {
        int size = gameState.getSize();
        GameState fresh = new GameState(size);

        gameState.copyFrom(fresh);

        System.out.println("[SOAP-SERVER] Game reset to size " + size);
    }
}
