import javax.xml.bind.annotation.XmlElement;

public class Game {

    private String season;


    public String getSeason() {
        return season;
    }

    @XmlElement(name="Season")
    public void setSeason(String season) {
        this.season = season;
    }
}
