package org.dotsandboxessoap.game.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "gameState")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameStateDTO {
    public int size;

    @XmlElementWrapper(name = "cells")
    @XmlElement(name = "cell")
    public List<CellDTO> cells = new ArrayList<>();

    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    public List<EdgeDTO> edges = new ArrayList<>();

    @XmlElementWrapper(name = "edgeOwners")
    @XmlElement(name = "edgeOwner")
    public Map<String, String> edgeOwners = new HashMap<>();

    public String currentTurn;

    public int scoreRed;
    public int scoreBlue;

    public GameStateDTO() {
    }
}
