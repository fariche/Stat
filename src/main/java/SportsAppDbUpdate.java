import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
public class SportsAppDbUpdate {



    public static void main (String[] args) throws IOException, JAXBException, SQLException {
        //Create Sports Object
        SportsImpl sports = new SportsImpl();
        
        //Connect to the API and update get the XML 
        String xml = sports.connect();
        
        //Move XML into Pojo via JaxB
        ArrayOfGame games = sports.StringToJaxb(xml);
        
        //Insert Data into the database
        sports.pojoToDb(games);
        
        
        System.out.println("Done");
    }
}
