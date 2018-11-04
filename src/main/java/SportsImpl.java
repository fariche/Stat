
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SportsImpl {


    public String connect() throws IOException {

        //Request
        URL url = new URL("https://api.fantasydata.net/v3/cfb/scores/xml/Games/2018");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", "1a61db7569d84b53ac29bf25f29a7bbe");

        //Response
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String content = in.readLine().replace("/><","/>\n<");
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
        in.close();


        return content.toString();
    }

    public void StringToJaxb(String xml) throws JAXBException {
//        System.out.println("\nNEW LINE");
//        System.out.println(xml);
        System.out.println(xml);

        JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfGame.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        ArrayOfGame games = (ArrayOfGame) unmarshaller.unmarshal(reader);

        for (Game game:games.getGame()) {
            System.out.println("WEEK**********: "+game.getWeek());
            System.out.println("Away Team: "+game.getAwayTeamName());
            System.out.println("Home Team: "+game.getHomeTeamName());
            System.out.println("DateTime : "+game.getDateTime());
            System.out.println("Point Spread: "+game.getPointSpread());
            System.out.println("Home Team Score: "+game.getHomeTeamScore());

            if(game.getPeriods().getPeriod() != null) {
                for (Period period : game.getPeriods().getPeriod()) {
                    System.out.println("Period Home: " + period.getHomeScore());
                    System.out.println("Period Away: " + period.getAwayScore());
                }
            }

        }
    }
}
