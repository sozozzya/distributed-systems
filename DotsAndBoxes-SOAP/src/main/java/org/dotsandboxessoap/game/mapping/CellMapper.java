package org.dotsandboxessoap.game.mapping;

import org.dotsandboxessoap.game.model.Cell;
import org.dotsandboxessoap.game.PlayerColor;
import org.dotsandboxessoap.game.dto.CellDTO;

public class CellMapper {
    public static CellDTO toDTO(int col, int row, Cell cell) {
        return new CellDTO(col, row, cell.getOwner().name());
    }

    public static void fromDTO(CellDTO dto, Cell[][] cells) {
        cells[dto.col][dto.row].setOwner(PlayerColor.valueOf(dto.owner));
    }
}
