import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class SportsAppCalc {



    public static void main (String[] args) throws IOException, JAXBException, SQLException {
        //Get Data from DB and store in DTO
        SportsImpl sports = new SportsImpl();
        
        //Connect to the API and update get the XML 
        List<CfbDto> cfbDtos = sports.dbToDto(13.0);
        
        //Calc Winners
       sports.calcWinners(cfbDtos,20.0);
        
        //Test Calculation
        //sports.testCalc(16.0);
        
        //QC Picks
        
        
        System.out.println("Done");
    }
}
