
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

public class CfbImpl {

    private static String dbURL = "jdbc:derby://localhost:1527/sample;user=app;password=app";
    private static String tableName = "cfb";
    // jdbc Connection
    private static Connection conn = null;

    public String connect() throws IOException {

        
        //Request
        System.out.println("Connecting to API...");
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
        System.out.println("Connected.");
        return content.toString();
    }

    public ArrayOfGame StringToJaxb(String xml) throws JAXBException {
        System.out.println("Parsing XML via JAXB...");

        JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfGame.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        ArrayOfGame games = (ArrayOfGame) unmarshaller.unmarshal(reader);

        for (Game game : games.getGame()) {
            Double homeScore = 0.0;
            Double awayScore = 0.0;
            if (game.getPeriods().getPeriod() != null) {
                for (Period period : game.getPeriods().getPeriod()) {
                    homeScore += period.getHomeScore();
                    awayScore += period.getAwayScore();
                }
                game.setAwayTeamScore(String.valueOf(awayScore));
                game.setHomeTeamScore(String.valueOf(homeScore));
            }

        }
        System.out.println("Done.");
        return games;
    }

    public void pojoToDb(ArrayOfGame games) throws SQLException {
        createConnection();
        truncateTable();
        try {
            System.out.println("INSERTING DATA..");
            String statement = "";
            for (Game game : games.getGame()) {
                statement = "INSERT INTO " + tableName + " VALUES("
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

                Statement st = conn.createStatement();
                int m = st.executeUpdate(statement);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        System.out.println("DATA Inserted!");
    }

    private static void createConnection() {
        try {
            System.out.println("CONNECTING TO DATABASE");
            String driver = "org.apache.derby.jdbc.ClientDriver";
            Class.forName(driver);
            //Get a connection
            conn = DriverManager.getConnection(dbURL);
            System.out.println("CONNECTION SUCCESSFUL");
        } catch (Exception except) {
            except.printStackTrace();
        }
    }

    public List<CfbDto> dbToDto(Double currWeek) throws SQLException {
        List<CfbDto> cfbDtos = new ArrayList<CfbDto>();
        createConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT * FROM CFB where week = '" + currWeek + "'");
        while (rs.next()) {
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
        System.out.println("SIZE OF LIST: " + cfbDtos.size());
        return cfbDtos;
    }

    public void calcWinners(List<CfbDto> cfbDtos, Double confidenceSpread) throws SQLException {

        int noPointSpreadCount = 0;
        int goodCount = 0;
        int badCount = 0;
        int idenPointSpread = 0;
        for (CfbDto cfbDto : cfbDtos) {
//            System.out.println("");
            //Check if Point Spread Exists
            if (cfbDto.getPointSpread() != 0.0) {

                //Get predicted Spread
                //Double predSpread = cfbDto.getAvgHomePPGFor()-cfbDto.getAvgAwayPPGAgainst();
                Double predHomeScore = (cfbDto.getAvgHomePPGFor() + cfbDto.getAvgAwayPPGAgainst()) / 2;

                //Add 4 points to the home team score as an edge
                predHomeScore += 4.0;

                Double predAwayScore = (cfbDto.getAvgAwayPPGFor() + cfbDto.getAvgHomePPGAgainst()) / 2;
                Double predSpread = predAwayScore - predHomeScore;

                //CHECK IF HOME IS A GOOD PICK
                //If predicted spread is greater than the actual spread, meaning
                // I think the Home Team will win by more points than actual spread
                if (predSpread < cfbDto.getPointSpread()) {
                    //Great! My predicted spread is better than the actual spread, so
                    //I think the home team is going to cover the spread at the book. But by how
                    //much? A couple points is ok, but the larger the prediction, the 
                    //more confident I am in this pick
                    if ((cfbDto.getPointSpread() - predSpread) > confidenceSpread) {
                        System.out.println(cfbDto.getDateTime() + " HOME GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                + (cfbDto.getPointSpread() - predSpread));
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("");
                        goodCount++;
                    } else {
//                        System.out.println("HOME NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (cfbDto.getPointSpread() - predSpread));
//                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                        badCount++;
                    }
                } //CHECK IF AWAY IS A GOOD PICK
                //If predicted spread is less than the actual spread, meaning
                // I think the Home Team will lose by more points than actual spread
                //or better yet the Away team will either win or cover the spread
                else if (predSpread > cfbDto.getPointSpread()) {
                    //Ok! This is good too, because now the AWAY team looks good.
                    //My predicted spread says either the AWAY team witll win or 
                    //cover the spread. But again, by home much? We still need some
                    //confidence
                    if ((predSpread - cfbDto.getPointSpread()) > confidenceSpread) {
                        System.out.println(cfbDto.getDateTime() + " AWAY GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                + (predSpread - cfbDto.getPointSpread()));
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("");
                        goodCount++;
                    } else {
//                        System.out.println("AWAY NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (predSpread - cfbDto.getPointSpread()));
//                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                        badCount++;
                    }
                } else {
//                    System.out.println("POINT SPREAD IDENTICAL. Confidence: " + confidenceSpread
//                            + " points, Actual: " + (cfbDto.getPointSpread() - predSpread));
//                    System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                    System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                    System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                    System.out.println("Predicted Spread: " + predSpread);
//                    System.out.println("Home Score : " + predHomeScore);
//                    System.out.println("Away Score : " + predAwayScore);
                    idenPointSpread++;
                }
                //NO POINT SPREAD in the DAtabase
            } else {
//                System.out.println("NO POINT SPREAD.");
//                System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                System.out.println("Point Spread: " + cfbDto.getPointSpread());
                noPointSpreadCount++;
            }
        }

        System.out.println("TOTAL GOOD: " + goodCount);
        System.out.println("TOTAL BAD: " + badCount);
        System.out.println("TOTAL NO Spread: " + noPointSpreadCount);
        System.out.println("IDENTICAL SPREADS: " + idenPointSpread);
    }

    public void testCalc(Double confidenceSpread) throws SQLException {
        //Get all the games that are completed
        List<CfbDto> cfbDtos = new ArrayList<CfbDto>();
        createConnection();
        Statement stmt = conn.createStatement();
        int correctPred = 0;
        int wrongPred = 0;
        ResultSet rs;
//        rs = stmt.executeQuery("SELECT * FROM CFB where status = 'Final' OR status = 'F/OT'");
        rs = stmt.executeQuery("SELECT * FROM CFB where (status = 'Final' OR status = 'F/OT') AND ("
                + "week='11.0' OR "
                + "week='10.0' OR "
                + "week='9.0' )");
//                + "week='8.0' OR "
//                + "week='7.0' OR "
//                + "week='6.0')");
        while (rs.next()) {
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

            //Get Predicted Score
            //Double predSpread = cfbDto.getAvgHomePPGFor()-cfbDto.getAvgAwayPPGAgainst();
            Double predHomeScore = (cfbDto.getAvgHomePPGFor() + cfbDto.getAvgAwayPPGAgainst()) / 2;

            //Add 4 points to the home team score as an edge
            predHomeScore += 4.0;

            Double predAwayScore = (cfbDto.getAvgAwayPPGFor() + cfbDto.getAvgHomePPGAgainst()) / 2;
            Double predSpread = predAwayScore - predHomeScore;

            //Actual Spread
            Double actualSpread = cfbDto.getAwayTeamScore() - cfbDto.getHomeTeamScore();

            //Check if Predicted Score is close to Actual. Also, if Actual Score
            //Would have hit under the predicted algorithm
//            System.out.println("WEEK: " + cfbDto.getWeek() + " " + cfbDto.getHomeTeamName() + " vs " + cfbDto.getAwayTeamName());
//            System.out.println("PREDICTED HOME SCORE: " + predHomeScore);
//            System.out.println("ACTUAL HOME SCORE: " + cfbDto.getHomeTeamScore());
//            System.out.println("PREDICTED AWAY SCORE: " + predAwayScore);
//            System.out.println("ACTUAL AWAY SCORE: " + cfbDto.getAwayTeamScore());
//            System.out.println("");
            if (predSpread < cfbDto.getPointSpread()) {
                //Great! My predicted spread is better than the actual spread, so
                //I think the home team is going to cover the spread at the book. But by how
                //much? A couple points is ok, but the larger the prediction, the 
                //more confident I am in this pick
                if ((cfbDto.getPointSpread() - predSpread) > confidenceSpread) {
                    if ((actualSpread - 3) < cfbDto.getPointSpread()) {
                        System.out.println("CORRECT PREDICTION HOME GOOD GAME. Confidence: " + confidenceSpread
                                + " points! Gaming: " + (cfbDto.getPointSpread() - predSpread)
                                + " ACTUAL: " + actualSpread);
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("ACTUAL Home Score : " + cfbDto.getHomeTeamScore());
                        System.out.println("ACTUAL Away Score : " + cfbDto.getAwayTeamScore());
                        System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                        correctPred++;
                        System.out.println("");
                    } else {
                        System.out.println("NOT CORRECT PREDICTION HOME GOOD GAME. Confidence: " + confidenceSpread
                                + " points! Gaming: " + (cfbDto.getPointSpread() - predSpread)
                                + " ACTUAL: " + actualSpread);
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("ACTUAL Home Score : " + cfbDto.getHomeTeamScore());
                        System.out.println("ACTUAL Away Score : " + cfbDto.getAwayTeamScore());
                        System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                        wrongPred++;
                        System.out.println("");
                    }
                } else {
//                        System.out.println("HOME NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (cfbDto.getPointSpread() - predSpread));
//                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
//                        badCount++;
                }
            } //CHECK IF AWAY IS A GOOD PICK
            //If predicted spread is less than the actual spread, meaning
            // I think the Home Team will lose by more points than actual spread
            //or better yet the Away team will either win or cover the spread
            else if (predSpread > cfbDto.getPointSpread()) {
                //Ok! This is good too, because now the AWAY team looks good.
                //My predicted spread says either the AWAY team witll win or 
                //cover the spread. But again, by home much? We still need some
                //confidence
                if ((predSpread - cfbDto.getPointSpread()) > confidenceSpread) {
                    if ((actualSpread + 3) > cfbDto.getPointSpread()) {
                        System.out.println("CORRECT PREDICTION AWAY GOOD GAME. Confidence: " + confidenceSpread
                                + " points! Gaming: " + (predSpread - cfbDto.getPointSpread())
                                + " ACTUAL: " + actualSpread);
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("ACTUAL Home Score : " + cfbDto.getHomeTeamScore());
                        System.out.println("ACTUAL Away Score : " + cfbDto.getAwayTeamScore());
                        System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                        correctPred++;
                        System.out.println("");
                    } else {
                        System.out.println("NOT CORRECT PREDICTION AWAY GOOD GAME. Confidence: " + confidenceSpread
                                + " points! Gaming: " + (predSpread - cfbDto.getPointSpread())
                                + " ACTUAL: " + actualSpread);
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("ACTUAL Home Score : " + cfbDto.getHomeTeamScore());
                        System.out.println("ACTUAL Away Score : " + cfbDto.getAwayTeamScore());
                        System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                        wrongPred++;
                        System.out.println("");
                    }
                }
            }

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
        System.out.println("TOTAL CORRECT: " + correctPred);
        System.out.println("TOTAL WRONG: " + wrongPred);
        System.out.println("WINNING %" + ((correctPred / (wrongPred + correctPred)) * 100.00));
        System.out.println("COUNT OF GAMES: " + cfbDtos.size());

    }

    public void qcPicks(List<CfbDto> cfbDtos, Double confidenceSpread) throws SQLException {

        int noPointSpreadCount = 0;
        int goodCount = 0;
        int badCount = 0;
        int idenPointSpread = 0;
        for (CfbDto cfbDto : cfbDtos) {
//            System.out.println("");
            //Check if Point Spread Exists
            if (cfbDto.getPointSpread() != 0.0) {

                //Get predicted Spread
                //Double predSpread = cfbDto.getAvgHomePPGFor()-cfbDto.getAvgAwayPPGAgainst();
                Double predHomeScore = (cfbDto.getAvgHomePPGFor() + cfbDto.getAvgAwayPPGAgainst()) / 2;

                //Add 4 points to the home team score as an edge
                predHomeScore += 4.0;

                Double predAwayScore = (cfbDto.getAvgAwayPPGFor() + cfbDto.getAvgHomePPGAgainst()) / 2;
                Double predSpread = predAwayScore - predHomeScore;

                //CHECK IF HOME IS A GOOD PICK
                //If predicted spread is greater than the actual spread, meaning
                // I think the Home Team will win by more points than actual spread
                if (predSpread < cfbDto.getPointSpread()) {
                    //Great! My predicted spread is better than the actual spread, so
                    //I think the home team is going to cover the spread at the book. But by how
                    //much? A couple points is ok, but the larger the prediction, the 
                    //more confident I am in this pick
                    if ((cfbDto.getPointSpread() - predSpread) > confidenceSpread) {
                        System.out.println(cfbDto.getDateTime() + " HOME GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                + (cfbDto.getPointSpread() - predSpread));
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("");
                        goodCount++;
                    } else {
//                        System.out.println("HOME NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (cfbDto.getPointSpread() - predSpread));
//                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                        badCount++;
                    }
                } //CHECK IF AWAY IS A GOOD PICK
                //If predicted spread is less than the actual spread, meaning
                // I think the Home Team will lose by more points than actual spread
                //or better yet the Away team will either win or cover the spread
                else if (predSpread > cfbDto.getPointSpread()) {
                    //Ok! This is good too, because now the AWAY team looks good.
                    //My predicted spread says either the AWAY team witll win or 
                    //cover the spread. But again, by home much? We still need some
                    //confidence
                    if ((predSpread - cfbDto.getPointSpread()) > confidenceSpread) {
                        System.out.println(cfbDto.getDateTime() + " AWAY GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                + (predSpread - cfbDto.getPointSpread()));
                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
                        System.out.println("Predicted Spread: " + predSpread);
                        System.out.println("Home Score : " + predHomeScore);
                        System.out.println("Away Score : " + predAwayScore);
                        System.out.println("");
                        goodCount++;
                    } else {
//                        System.out.println("AWAY NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (predSpread - cfbDto.getPointSpread()));
//                        System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                        badCount++;
                    }
                } else {
//                    System.out.println("POINT SPREAD IDENTICAL. Confidence: " + confidenceSpread
//                            + " points, Actual: " + (cfbDto.getPointSpread() - predSpread));
//                    System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                    System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                    System.out.println("Point Spread: " + cfbDto.getPointSpread());
//                    System.out.println("Predicted Spread: " + predSpread);
//                    System.out.println("Home Score : " + predHomeScore);
//                    System.out.println("Away Score : " + predAwayScore);
                    idenPointSpread++;
                }
                //NO POINT SPREAD in the DAtabase
            } else {
//                System.out.println("NO POINT SPREAD.");
//                System.out.println("HOME TEAM: :" + cfbDto.getHomeTeamName());
//                System.out.println("AWAY TEAM: :" + cfbDto.getAwayTeamName());
//                System.out.println("Point Spread: " + cfbDto.getPointSpread());
                noPointSpreadCount++;
            }
        }

        System.out.println("TOTAL GOOD: " + goodCount);
        System.out.println("TOTAL BAD: " + badCount);
        System.out.println("TOTAL NO Spread: " + noPointSpreadCount);
        System.out.println("IDENTICAL SPREADS: " + idenPointSpread);
    }

    private Double getAvgPPGFor(String teamName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs, rs1;
        Double totalPointsFor = 0.0;
        int weekCount = 0;

        //Get Home Total Points Scored
        rs = stmt.executeQuery("SELECT home_score FROM CFB where home_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs.next()) {
            totalPointsFor += Double.valueOf(rs.getString(1));
            weekCount++;
        }

        //Get Away Total Points Scored
        rs1 = stmt.executeQuery("SELECT away_score FROM CFB where away_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs1.next()) {
            totalPointsFor += Double.valueOf(rs1.getString(1));
            weekCount++;
        }
        Double avgPointsFor = totalPointsFor / weekCount;
        return avgPointsFor;
    }

    private Double getAvgPPGAgainst(String teamName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs, rs1;
        Double totalPointsAgainst = 0.0;
        int weekCount = 0;

        //Get Home Total Points Scored
        rs = stmt.executeQuery("SELECT home_score FROM CFB where away_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs.next()) {
            totalPointsAgainst += Double.valueOf(rs.getString(1));
            weekCount++;
        }

        //Get Away Total Points Scored
        rs1 = stmt.executeQuery("SELECT away_score FROM CFB where home_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs1.next()) {
            totalPointsAgainst += Double.valueOf(rs1.getString(1));
            weekCount++;
        }
        Double avgPointsAgainst = totalPointsAgainst / weekCount;
        return avgPointsAgainst;
    }

    private void truncateTable() throws SQLException {
        System.out.println("TRUNCATING TABLE");
        Statement stmt = conn.createStatement();
        ResultSet rs;

        //Get Home Total Points Scored
        stmt.executeUpdate("DELETE FROM " + tableName + " WHERE 1=1");
        System.out.println("TABLE TRUNCATED");
    }
}
