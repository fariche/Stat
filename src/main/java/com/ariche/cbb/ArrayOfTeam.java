package com.ariche.cbb;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ArrayOfGame")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrayOfGame {

    @XmlElement(name = "Game")
    private List<Game> game;


    public List<Game> getGame() {
        return game;
    }

    public void setGame(List<Game> Game) {
        this.game = game;
    }
}
