import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class CfbAppCalc {



    public static void main (String[] args) throws IOException, JAXBException, SQLException {
        //Get Data from DB and store in DTO
        CfbImpl sports = new CfbImpl();
        
        //Connect to the API and update get the XML 
        List<CfbDto> cfbDtos = sports.dbToDto(13.0);
        
        //Calc Winners
       sports.calcWinners(cfbDtos,18.0);
        
        //Test Calculation (Confidence Spread, Current Week, Weeks to test starting at
        // Current week - 1 and going down)
        //sports.testCalc(18.0,13,5);
        
        
        System.out.println("Done");
    }
}
