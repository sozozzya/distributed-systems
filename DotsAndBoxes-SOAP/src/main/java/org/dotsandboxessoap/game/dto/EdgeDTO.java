package org.dotsandboxessoap.game.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "edge")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdgeDTO {
    public int x;
    public int y;
    public boolean horizontal;

    public EdgeDTO() {
    }

    public EdgeDTO(int x, int y, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }
}
