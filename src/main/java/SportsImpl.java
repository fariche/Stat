
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SportsImpl {

    private static String dbURL = "jdbc:derby://localhost:1527/sample;user=app;password=app";
    private static String tableName = "cfb";
    // jdbc Connection
    private static Connection conn = null;

    public String connect() throws IOException {

        //Request
        URL url = new URL("https://api.fantasydata.net/v3/cfb/scores/xml/Games/2018");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", "1a61db7569d84b53ac29bf25f29a7bbe");

        //Response
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String content = in.readLine().replace("/><", "/>\n<");
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
        in.close();

        return content.toString();
    }

    public ArrayOfGame StringToJaxb(String xml) throws JAXBException {
//        System.out.println("\nNEW LINE");
//        System.out.println(xml);
        System.out.println(xml);

        JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfGame.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        ArrayOfGame games = (ArrayOfGame) unmarshaller.unmarshal(reader);

        for (Game game : games.getGame()) {
//            System.out.println("WEEK**********: " + game.getWeek());
//            System.out.println("Away Team: " + game.getAwayTeamName());
//            System.out.println("Home Team: " + game.getHomeTeamName());
//            System.out.println("DateTime : " + game.getDateTime());
//            System.out.println("Point Spread: " + game.getPointSpread());
//            System.out.println("Home Team Score: " + game.getHomeTeamScore());

            Double homeScore = 0.0;
            Double awayScore = 0.0;
            if (game.getPeriods().getPeriod() != null) {
                for (Period period : game.getPeriods().getPeriod()) {
//                    System.out.println("Period Home: " + period.getHomeScore());
                    homeScore += period.getHomeScore();
//                    System.out.println("Period Away: " + period.getAwayScore());
                    awayScore += period.getAwayScore();
                }
                game.setAwayTeamScore(String.valueOf(awayScore));
                game.setHomeTeamScore(String.valueOf(homeScore));
            }

        }

        return games;
    }

    public void pojoToDb(ArrayOfGame games) throws SQLException {
        createConnection();
        try {
            
            System.out.println("333333333333");
            String statement = "";
            for (Game game : games.getGame()) {
                System.out.println("44444444444");
                statement = "INSERT INTO "+tableName+" VALUES("
                        + "'" + game.getAwayTeamScore() + "',"
                        + "'" + game.getHomeTeamScore() + "',"
                        + "'" + game.getOverUnder() + "',"
                        + "'" + game.getPointSpread() + "',"
                        + "'" + game.getAwayTeamName() + "',"
                        + "'" + game.getHomeTeamName() + "',"
                        + "'" + game.getDateTime() + "',"
                        + "'" + game.getStatus() + "',"
                        + "'" + game.getWeek() + "',"
                        + "'" + game.getHomeTeamMoneyLine() + "',"
                        + "'" + game.getAwayTeamMoneyLine() + "'"
                        + ")";

                System.out.println("ST: " + statement);
                Statement st = conn.createStatement();
                System.out.println("555555555");
                int m = st.executeUpdate(statement);
                System.out.println("666666666666");
                System.out.println("Updated " + m + " rows");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
    
    private static void createConnection()
    {
        try
        {
            System.out.println("99999999999999");
            String driver = "org.apache.derby.jdbc.ClientDriver";
	Class.forName(driver);
            System.out.println("888888");
            //Get a connection
            conn = DriverManager.getConnection(dbURL); 
            System.out.println("CONNECTION SUCCESSFUL");
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
    
    public List<CfbDto> dbToDto() throws SQLException{
        List<CfbDto> cfbDtos = new ArrayList<CfbDto>();
                createConnection();
        Statement stmt = conn.createStatement();
            ResultSet rs; 
            rs = stmt.executeQuery("SELECT * FROM CFB where status = 'Final' OR status = 'F/OT'");
            while ( rs.next() ) {
                CfbDto cfbDto = new CfbDto();
                cfbDto.setAwayTeamScore(rs.getString(1));
                cfbDto.setHomeTeamScore(rs.getString(2));
                cfbDto.setOverUnder(rs.getString(3));
                cfbDto.setPointSpread(rs.getString(4));
                cfbDto.setAwayTeamName(rs.getString(5));
                cfbDto.setHomeTeamName(rs.getString(6));
                cfbDto.setDateTime(rs.getString(7));
                cfbDto.setStatus(rs.getString(8));
                cfbDto.setWeek(rs.getString(9));
                cfbDto.setHomeTeamMoneyLine(rs.getString(10));
                cfbDto.setAwayTeamMoneyLine(rs.getString(11));
                
                //Set Average Values
                cfbDto.setAvgHomePPGFor(String.valueOf(getAvgPPGFor(cfbDto.getHomeTeamName())));
                cfbDto.setAvgAwayPPGFor(String.valueOf(getAvgPPGFor(cfbDto.getAwayTeamName())));
                cfbDto.setAvgHomePPGAgainst(String.valueOf(getAvgPPGAgainst(cfbDto.getHomeTeamName())));
                cfbDto.setAvgAwayPPGAgainst(String.valueOf(getAvgPPGAgainst(cfbDto.getAwayTeamName())));
                
                //Print some
//                System.out.println("");
//                System.out.println(cfbDto.getHomeTeamName()+": HOME FOR"+cfbDto.getAvgHomePPGFor());
//                System.out.println(cfbDto.getAwayTeamName()+": AWAY FOR"+cfbDto.getAvgAwayPPGFor());
//                System.out.println(cfbDto.getHomeTeamName()+": HOME AGAINST"+cfbDto.getAvgHomePPGAgainst());
//                System.out.println(cfbDto.getAwayTeamName()+": AWAY AGAINST"+cfbDto.getAvgAwayPPGAgainst());
//                System.out.println("");
                
                //Add DTO to list
                cfbDtos.add(cfbDto);
                
            }
            conn.close();
    return cfbDtos;
    }
    
    public void calcWinners(List<CfbDto> cfbDtos, int currWeek){
        
    }
    
    private Double getAvgPPGFor(String teamName) throws SQLException{
        Statement stmt = conn.createStatement();
            ResultSet rs,rs1; 
            Double totalPointsFor = 0.0;
            int weekCount = 0;
             
            //Get Home Total Points Scored
            rs = stmt.executeQuery("SELECT home_score FROM CFB where home_name = '"+teamName+"' AND (status = 'Final' OR status = 'F/OT')");
            while ( rs.next() ) {
                totalPointsFor+=Double.valueOf(rs.getString(1));
                weekCount++;
            }   
            
            //Get Away Total Points Scored
            rs1 = stmt.executeQuery("SELECT away_score FROM CFB where away_name = '"+teamName+"' AND (status = 'Final' OR status = 'F/OT')");
            while ( rs1.next() ) {
                totalPointsFor+=Double.valueOf(rs1.getString(1));
                weekCount++;
            }
            Double avgPointsFor = totalPointsFor/weekCount;
                return avgPointsFor;
    }
    
    private Double getAvgPPGAgainst(String teamName) throws SQLException{
        Statement stmt = conn.createStatement();
            ResultSet rs,rs1; 
            Double totalPointsAgainst = 0.0;
            int weekCount = 0;
            
            //Get Home Total Points Scored
            rs = stmt.executeQuery("SELECT home_score FROM CFB where away_name = '"+teamName+"' AND (status = 'Final' OR status = 'F/OT')");
            while ( rs.next() ) {
                totalPointsAgainst+=Double.valueOf(rs.getString(1));
                weekCount++;
            }   
            
            //Get Away Total Points Scored
            rs1 = stmt.executeQuery("SELECT away_score FROM CFB where home_name = '"+teamName+"' AND (status = 'Final' OR status = 'F/OT')");
            while ( rs1.next() ) {
                totalPointsAgainst+=Double.valueOf(rs1.getString(1));
                weekCount++;
            }
            Double avgPointsAgainst = totalPointsAgainst/weekCount;
                return avgPointsAgainst;
    }
}
