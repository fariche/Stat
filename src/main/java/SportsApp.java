import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
public class SportsApp {



    public static void main (String[] args) throws IOException, JAXBException, SQLException {
        SportsImpl sports = new SportsImpl();
        String xml = sports.connect();
        ArrayOfGame games = sports.StringToJaxb(xml);
        sports.pojoToDb(games);
        System.out.println("Done");
    }
}
