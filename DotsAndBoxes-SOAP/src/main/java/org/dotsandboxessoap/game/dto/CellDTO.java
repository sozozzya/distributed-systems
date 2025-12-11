package org.dotsandboxessoap.game.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cell")
@XmlAccessorType(XmlAccessType.FIELD)
public class CellDTO {
    public int col;
    public int row;
    public String owner;

    public CellDTO() {
    }

    public CellDTO(int col, int row, String owner) {
        this.col = col;
        this.row = row;
        this.owner = owner;
    }
}
