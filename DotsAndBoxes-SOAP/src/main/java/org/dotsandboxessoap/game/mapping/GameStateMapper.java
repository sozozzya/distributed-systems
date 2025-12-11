package org.dotsandboxessoap.game.mapping;

import org.dotsandboxessoap.game.PlayerColor;
import org.dotsandboxessoap.game.dto.CellDTO;
import org.dotsandboxessoap.game.dto.EdgeDTO;
import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.model.Cell;
import org.dotsandboxessoap.game.model.Edge;
import org.dotsandboxessoap.game.model.GameState;

import java.util.Map;

public class GameStateMapper {
    public static GameStateDTO toDTO(GameState gs) {
        GameStateDTO dto = new GameStateDTO();

        dto.size = gs.getSize();

        for (Edge e : gs.getEdges()) {
            dto.edges.add(EdgeMapper.toDTO(e));
        }

        for (Map.Entry<String, PlayerColor> entry : gs.getEdgeOwners().entrySet()) {
            dto.edgeOwners.put(entry.getKey(), entry.getValue().name());
        }

        Cell[][] cells = gs.getCells();
        for (int col = 0; col < gs.getCellSize(); col++) {
            for (int row = 0; row < gs.getCellSize(); row++) {
                dto.cells.add(CellMapper.toDTO(col, row, cells[col][row]));
            }
        }

        dto.currentTurn = gs.getCurrentTurn().name();
        dto.scoreRed = gs.getScore(PlayerColor.RED);
        dto.scoreBlue = gs.getScore(PlayerColor.BLUE);

        return dto;
    }

    public static GameState fromDTO(GameStateDTO dto) {
        GameState gs = new GameState(dto.size);

        gs.getEdges().clear();
        for (EdgeDTO e : dto.edges) {
            gs.getEdges().add(EdgeMapper.fromDTO(e));
        }

        gs.getEdgeOwners().clear();
        for (Map.Entry<String, String> entry : dto.edgeOwners.entrySet()) {
            gs.getEdgeOwners().put(entry.getKey(), PlayerColor.valueOf(entry.getValue()));
        }

        for (CellDTO c : dto.cells) {
            CellMapper.fromDTO(c, gs.getCells());
        }

        gs.setCurrentTurn(PlayerColor.valueOf(dto.currentTurn));
        gs.setScore(PlayerColor.RED, dto.scoreRed);
        gs.setScore(PlayerColor.BLUE, dto.scoreBlue);

        return gs;
    }
}
