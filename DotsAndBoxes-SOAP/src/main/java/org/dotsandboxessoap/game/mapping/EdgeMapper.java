package org.dotsandboxessoap.game.mapping;

import org.dotsandboxessoap.game.dto.EdgeDTO;
import org.dotsandboxessoap.game.model.Edge;

public class EdgeMapper {
    public static EdgeDTO toDTO(Edge edge) {
        return new EdgeDTO(edge.x, edge.y, edge.horizontal);
    }

    public static Edge fromDTO(EdgeDTO dto) {
        return new Edge(dto.x, dto.y, dto.horizontal);
    }
}
