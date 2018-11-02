import javax.xml.bind.JAXBException;
import java.io.IOException;
public class SportsApp {



    public static void main (String[] args) throws IOException, JAXBException {
        SportsImpl sports = new SportsImpl();
        String xml = sports.connect();
        sports.StringToJaxb(xml);
        System.out.println("Done");
    }
}
