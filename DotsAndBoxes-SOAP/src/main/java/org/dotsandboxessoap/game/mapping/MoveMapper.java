package org.dotsandboxessoap.game.mapping;

import org.dotsandboxessoap.game.dto.MoveDTO;
import org.dotsandboxessoap.game.model.Move;

public class MoveMapper {
    public static MoveDTO toDTO(Move move) {
        return new MoveDTO(move.x, move.y, move.horizontal);
    }

    public static Move fromDTO(MoveDTO dto) {
        return new Move(dto.x, dto.y, dto.horizontal);
    }
}
