import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ArrayOfGame")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ArrayOfGame {

    public ArrayOfGame(){};

    private List<Game> game;


    @XmlElement(name = "Game")
    public List<Game> getGame() {
        return game;
    }

    public void setGame(List<Game> Game) {
        this.game = game;
    }
}
