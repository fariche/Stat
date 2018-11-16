import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class SportsAppCalc {



    public static void main (String[] args) throws IOException, JAXBException, SQLException {
        //Get Data from DB and store in DTO
        SportsImpl sports = new SportsImpl();
        
        //Connect to the API and update get the XML 
        List<CfbDto> cfbDtos = sports.dbToDto(12.0);
        
        //Calc Winners
       sports.calcWinners(cfbDtos,10.0);
        
        //Insert Data into the database
        //sports.pojoToDb(games);
        
        
        System.out.println("Done");
    }
}
