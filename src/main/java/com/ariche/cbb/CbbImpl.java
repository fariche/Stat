package com.ariche.cbb;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CbbImpl {

    private static String dbURL = "jdbc:derby://localhost:1527/sample;user=app;password=app";
    private static String tableName = "cbb";
    // jdbc Connection
    private static Connection conn = null;

    public String connectStatApi(String day) throws IOException {

        //Request Stat Info
        System.out.println("Connecting to Stats API...");
        URL url = new URL("https://api.fantasydata.net/v3/cbb/scores/XML/GamesByDate/" + day);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", "9e381d7b71bf4cffbf2cf966ca44a8cb");

        //Response Stat Info
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String content = in.readLine().replace("/><", "/>\n<");
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
        in.close();
        System.out.println("Connected to Stat API.");
        return content.toString();
    }

    public String connectTeamApi() throws IOException {

        //Request Stat Info
        System.out.println("Connecting to Team API...");
        URL url = new URL("https://api.fantasydata.net/v3/cbb/scores/XML/teams");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", "9e381d7b71bf4cffbf2cf966ca44a8cb");

        //Response Stat Info
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String content = in.readLine().replace("/><", "/>\n<");
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
        in.close();
        System.out.println("Connected to Team API.");
        return content.toString();
    }

    public ArrayOfGame stringToJaxbStats(String xml) throws JAXBException {
        System.out.println("Parsing STAT XML via JAXB...");

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

    public ArrayOfTeam stringToJaxbTeam(String xml) throws JAXBException {
        System.out.println("Parsing TEAM XML via JAXB...");

        JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfTeam.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        ArrayOfTeam teams = (ArrayOfTeam) unmarshaller.unmarshal(reader);

//        for (Team team : teams.getTeams()) {
//
//            System.out.println("Team Name: "+team.getTeamName());
//            System.out.println("Conference: "+team.getConference());
//            System.out.println("Rank: "+team.getRank());
//            System.out.println("");
//        }
        System.out.println("Done.");
        return teams;
    }

    public void pojoToDb(ArrayOfGame games) throws SQLException {
        createConnection();
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
                        + "'" + game.getHomeTeamMoneyLine() + "',"
                        + "'" + game.getAwayTeamMoneyLine() + "',"
                        + "'',"
                        + "'',"
                        + "'',"
                        + "''"
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

    public void updateRecords(ArrayOfTeam teams) throws SQLException {
        createConnection();
        try {
            System.out.println("UPDATING RANK/CONFERENCE DATA..");
            String statement = "";
            for (Team team : teams.getTeams()) {
                statement = "UPDATE " + tableName + " SET"
                        + " home_rank='" + team.getRank() + "',"
                        + " home_conference='" + team.getConference() + "' WHERE "
                        + " home_name='" + team.getTeamName() + "'";

                Statement st = conn.createStatement();
                int m = st.executeUpdate(statement);
            }
            for (Team team : teams.getTeams()) {
                statement = "UPDATE " + tableName + " SET"
                        + " away_rank='" + team.getRank() + "',"
                        + " away_conference='" + team.getConference() + "' WHERE "
                        + " away_name='" + team.getTeamName() + "'";

                Statement st = conn.createStatement();
                int m = st.executeUpdate(statement);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        System.out.println("DATA Updated!");
    }

    public void createConnection() {
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

    public List<CbbDto> dbToDto(String today) throws SQLException {
        List<CbbDto> cbbDtos = new ArrayList<CbbDto>();
        createConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT * FROM CBB where date_time like '%" + today + "%'");
        while (rs.next()) {
            CbbDto cbbDto = new CbbDto();
            cbbDto.setAwayTeamScore(rs.getString(1));
            cbbDto.setHomeTeamScore(rs.getString(2));
            cbbDto.setOverUnder(rs.getString(3));
            cbbDto.setPointSpread(rs.getString(4));
            cbbDto.setAwayTeamName(rs.getString(5));
            cbbDto.setHomeTeamName(rs.getString(6));
            cbbDto.setDateTime(rs.getString(7));
            cbbDto.setStatus(rs.getString(8));
            cbbDto.setHomeTeamMoneyLine(rs.getString(9));
            cbbDto.setAwayTeamMoneyLine(rs.getString(10));
            cbbDto.setHomeRank(rs.getString(11));
            cbbDto.setAwayRank(rs.getString(12));
            cbbDto.setHomeConference(rs.getString(13));
            cbbDto.setAwayConference(rs.getString(14));

            //Set Average Values
            cbbDto.setAvgHomePPGFor(String.valueOf(getAvgPPGFor(cbbDto.getHomeTeamName())));
            cbbDto.setAvgAwayPPGFor(String.valueOf(getAvgPPGFor(cbbDto.getAwayTeamName())));
            cbbDto.setAvgHomePPGAgainst(String.valueOf(getAvgPPGAgainst(cbbDto.getHomeTeamName())));
            cbbDto.setAvgAwayPPGAgainst(String.valueOf(getAvgPPGAgainst(cbbDto.getAwayTeamName())));

            //Print some
//                System.out.println("");
//                System.out.println(cbbDto.getHomeTeamName()+": HOME RANK "+cbbDto.getHomeRank());
//                System.out.println(cbbDto.getAwayTeamName()+": AWAY RANK "+cbbDto.getAwayRank());
//                System.out.println(cbbDto.getHomeTeamName()+": Home Conference "+cbbDto.getHomeConference());
//                System.out.println(cbbDto.getAwayTeamName()+": Away Conference "+cbbDto.getAwayConference());
//                
//                System.out.println("");
            //Add DTO to list
            cbbDtos.add(cbbDto);

        }
        conn.close();
        System.out.println("SIZE OF LIST: " + cbbDtos.size());
        return cbbDtos;
    }

    public void calcWinners(List<CbbDto> cbbDtos, Double confidenceSpread, int daysBack) throws SQLException {

        int noPointSpreadCount = 0;
        int goodCount = 0;
        int badCount = 0;
        int idenPointSpread = 0;
        Double actualSpread = 0.0;
        int correctPred = 0;
        int wrongPred = 0;
        int totalCorrectPred = 0;
        int totalWrongPred = 0;
        int totalGames = 0;
        List<String> winners = new ArrayList<String>();
        List<String> losers = new ArrayList<String>();
        List<CbbDto> cbbDtosTest = new ArrayList<CbbDto>();

        //If not testing will run once, otherwise will run daysBack times
        while (daysBack >= 0) {

            //Testing Only clear the list and gather from the proper week
            if (daysBack != 0) {
                LocalDate dayToTest = LocalDate.now().minusDays(daysBack);
                cbbDtosTest.clear();
                cbbDtosTest = dbToDto(dayToTest.toString());
                System.out.println("##########################################");
                System.out.println("GAMES FOR: " + dayToTest.toString());
                System.out.println("##########################################");

            } else {
                cbbDtosTest.clear();
                cbbDtosTest = cbbDtos;
            }

            for (CbbDto cbbDto : cbbDtosTest) {
//            System.out.println("");
                //Check if Point Spread Exists
                if (cbbDto.getPointSpread() != 0.0) {

                    //Get predicted Spread
                    //Double predSpread = cbbDto.getAvgHomePPGFor()-cbbDto.getAvgAwayPPGAgainst();
                    Double predHomeScore = (cbbDto.getAvgHomePPGFor() + cbbDto.getAvgAwayPPGAgainst()) / 2;

                    //Add 8 points to the home team score as an edge
                    predHomeScore += 8.0;

                    Double predAwayScore = (cbbDto.getAvgAwayPPGFor() + cbbDto.getAvgHomePPGAgainst()) / 2;
                    Double predSpread = predAwayScore - predHomeScore;

                    //CHECK IF HOME IS A GOOD PICK
                    //If predicted spread is greater than the actual spread, meaning
                    // I think the Home Team will win by more points than actual spread
                    if (predSpread < cbbDto.getPointSpread()) {
                        //Great! My predicted spread is better than the actual spread, so
                        //I think the home team is going to cover the spread at the book. But by how
                        //much? A couple points is ok, but the larger the prediction, the 
                        //more confident I am in this pick
                        if ((cbbDto.getPointSpread() - predSpread) > confidenceSpread) {

                            if (daysBack == 0) {
                                int score = 0;
                                for (int i = 0; i < winners.size(); i++) {
                                    if ((winners.get(i).equalsIgnoreCase(cbbDto.getHomeConference()))
                                            && (losers.get(i).equalsIgnoreCase(cbbDto.getAwayConference()))) {
                                        System.out.println(cbbDto.getDateTime() + " HOME GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                                + (cbbDto.getPointSpread() - predSpread));
                                        System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                        System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                        System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                        System.out.println("Predicted Spread: " + predSpread);
                                        System.out.println("Home Score : " + predHomeScore);
                                        System.out.println("Away Score : " + predAwayScore);
                                        System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                        System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                        System.out.println("HIT #" + (++score));
                                        System.out.println("");
                                        goodCount++;
                                    }
                                }
                            } else {
                                actualSpread = cbbDto.getAwayTeamScore() - cbbDto.getHomeTeamScore();
                                if ((actualSpread) < cbbDto.getPointSpread()) {
                                    System.out.println("CORRECT PREDICTION HOME GOOD GAME. Confidence: " + confidenceSpread
                                            + " points! Gaming: " + (cbbDto.getPointSpread() - predSpread)
                                            + " ACTUAL: " + actualSpread);
                                    System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                    System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                    System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                    System.out.println("Predicted Spread: " + predSpread);
                                    System.out.println("Home Score : " + predHomeScore);
                                    System.out.println("Away Score : " + predAwayScore);
                                    System.out.println("ACTUAL Home Score : " + cbbDto.getHomeTeamScore());
                                    System.out.println("ACTUAL Away Score : " + cbbDto.getAwayTeamScore());
                                    System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                    System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                    System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                                    winners.add(cbbDto.getHomeConference());
                                    losers.add(cbbDto.getAwayConference());
                                    correctPred++;
                                    System.out.println("");
                                } else {
                                    System.out.println("NOT CORRECT PREDICTION HOME GOOD GAME. Confidence: " + confidenceSpread
                                            + " points! Gaming: " + (cbbDto.getPointSpread() - predSpread)
                                            + " ACTUAL: " + actualSpread);
                                    System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                    System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                    System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                    System.out.println("Predicted Spread: " + predSpread);
                                    System.out.println("Home Score : " + predHomeScore);
                                    System.out.println("Away Score : " + predAwayScore);
                                    System.out.println("ACTUAL Home Score : " + cbbDto.getHomeTeamScore());
                                    System.out.println("ACTUAL Away Score : " + cbbDto.getAwayTeamScore());
                                    System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                    System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                    System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                                    wrongPred++;
                                    System.out.println("");
                                }
                            }

                        } else {
//                        System.out.println("HOME NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (cbbDto.getPointSpread() - predSpread));
//                        System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cbbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                            badCount++;
                        }
                    } //CHECK IF AWAY IS A GOOD PICK
                    //If predicted spread is less than the actual spread, meaning
                    // I think the Home Team will lose by more points than actual spread
                    //or better yet the Away team will either win or cover the spread
                    else if (predSpread > cbbDto.getPointSpread()) {
                        //Ok! This is good too, because now the AWAY team looks good.
                        //My predicted spread says either the AWAY team witll win or 
                        //cover the spread. But again, by home much? We still need some
                        //confidence
                        if ((predSpread - cbbDto.getPointSpread()) > confidenceSpread) {
                            if (daysBack == 0) {

                                //check to see if the winning/losing conference are in the list of good ones
                                int score = 0;
                                for (int i = 0; i < winners.size(); i++) {
                                    if ((winners.get(i).equalsIgnoreCase(cbbDto.getAwayConference()))
                                            && (losers.get(i).equalsIgnoreCase(cbbDto.getHomeConference()))) {
                                        System.out.println(cbbDto.getDateTime() + " AWAY GOOD GAME. Confidence: " + confidenceSpread + " points! Actual: "
                                                + (predSpread - cbbDto.getPointSpread()));
                                        System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                        System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                        System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                        System.out.println("Predicted Spread: " + predSpread);
                                        System.out.println("Home Score : " + predHomeScore);
                                        System.out.println("Away Score : " + predAwayScore);
                                        System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                        System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                        System.out.println("HIT #" + (++score));
                                        System.out.println("");
                                        goodCount++;
                                    }
                                }
                            } else {
                                actualSpread = cbbDto.getAwayTeamScore() - cbbDto.getHomeTeamScore();
                                if ((actualSpread) > cbbDto.getPointSpread()) {
                                    System.out.println("CORRECT PREDICTION AWAY GOOD GAME. Confidence: " + confidenceSpread
                                            + " points! Gaming: " + (predSpread - cbbDto.getPointSpread())
                                            + " ACTUAL: " + actualSpread);
                                    System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                    System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                    System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                    System.out.println("Predicted Spread: " + predSpread);
                                    System.out.println("Home Score : " + predHomeScore);
                                    System.out.println("Away Score : " + predAwayScore);
                                    System.out.println("ACTUAL Home Score : " + cbbDto.getHomeTeamScore());
                                    System.out.println("ACTUAL Away Score : " + cbbDto.getAwayTeamScore());
                                    System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                    System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                    System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                                    winners.add(cbbDto.getAwayConference());
                                    losers.add(cbbDto.getHomeConference());

                                    correctPred++;
                                    System.out.println("");
                                } else {
                                    System.out.println("NOT CORRECT PREDICTION AWAY GOOD GAME. Confidence: " + confidenceSpread
                                            + " points! Gaming: " + (predSpread - cbbDto.getPointSpread())
                                            + " ACTUAL: " + actualSpread);
                                    System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
                                    System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
                                    System.out.println("Point Spread: " + cbbDto.getPointSpread());
                                    System.out.println("Predicted Spread: " + predSpread);
                                    System.out.println("Home Score : " + predHomeScore);
                                    System.out.println("Away Score : " + predAwayScore);
                                    System.out.println("ACTUAL Home Score : " + cbbDto.getHomeTeamScore());
                                    System.out.println("ACTUAL Away Score : " + cbbDto.getAwayTeamScore());
                                    System.out.println("HOME Conference: " + cbbDto.getHomeConference());
                                    System.out.println("AWAY Conference: " + cbbDto.getAwayConference());
                                    System.out.println("COMPARABLE: " + (actualSpread - predSpread));
                                    wrongPred++;
                                    System.out.println("");
                                }
                            }

                        } else {
//                        System.out.println("AWAY NOT WORTH IT Confidence: " + confidenceSpread
//                                + " points, Actual: " + (predSpread - cbbDto.getPointSpread()));
//                        System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
//                        System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
//                        System.out.println("Point Spread: " + cbbDto.getPointSpread());
//                        System.out.println("Predicted Spread: " + predSpread);
//                        System.out.println("Home Score : " + predHomeScore);
//                        System.out.println("Away Score : " + predAwayScore);
                            badCount++;
                        }
                    } else {
//                    System.out.println("POINT SPREAD IDENTICAL. Confidence: " + confidenceSpread
//                            + " points, Actual: " + (cbbDto.getPointSpread() - predSpread));
//                    System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
//                    System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
//                    System.out.println("Point Spread: " + cbbDto.getPointSpread());
//                    System.out.println("Predicted Spread: " + predSpread);
//                    System.out.println("Home Score : " + predHomeScore);
//                    System.out.println("Away Score : " + predAwayScore);
                        idenPointSpread++;
                    }
                    //NO POINT SPREAD in the DAtabase
                } else {
//                System.out.println("NO POINT SPREAD.");
//                System.out.println("HOME TEAM: :" + cbbDto.getHomeTeamName());
//                System.out.println("AWAY TEAM: :" + cbbDto.getAwayTeamName());
//                System.out.println("Point Spread: " + cbbDto.getPointSpread());
                    noPointSpreadCount++;
                }
            }

            try {
                if (daysBack == 0) {
                    System.out.println("TOTAL GOOD: " + goodCount);
                    System.out.println("TOTAL BAD: " + badCount);
                    System.out.println("TOTAL NO Spread: " + noPointSpreadCount);
                    System.out.println("IDENTICAL SPREADS: " + idenPointSpread);

                    //clear variables
                    goodCount = 0;
                    badCount = 0;
                    noPointSpreadCount = 0;
                    idenPointSpread = 0;
                } else {
                    System.out.println("TOTAL CORRECT: " + correctPred);
                    System.out.println("TOTAL WRONG: " + wrongPred);
                    System.out.println("WINNING %" + ((correctPred / (wrongPred + correctPred)) * 100.00));
                    System.out.println("COUNT OF GAMES: " + cbbDtosTest.size());

                    totalCorrectPred += correctPred;
                    totalWrongPred += wrongPred;
                    totalGames += cbbDtosTest.size();

                    //Clear variables
                    correctPred = 0;
                    wrongPred = 0;
                    //clear variables
                    goodCount = 0;
                    badCount = 0;
                    noPointSpreadCount = 0;
                    idenPointSpread = 0;

                }

            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }
            daysBack--;
            System.out.println("**************************************************************************************************");
            System.out.println("**************************************************************************************************");
        }
        if (daysBack != 0) {
            System.out.println("TOTAL CORRECT: " + totalCorrectPred);
            System.out.println("TOTAL WRONG: " + totalWrongPred);
            System.out.println("WINNING %" + ((totalCorrectPred / (totalWrongPred + totalCorrectPred)) * 100.00));
            System.out.println("TOTAL COUNT OF GAMES: " + totalGames);

            for (int i = 0; i < winners.size(); i++) {
                System.out.println((i + 1) + ". WINNER: " + winners.get(i) + " LOSER: " + losers.get(i));
            }
        }

    }

    private Double getAvgPPGFor(String teamName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs, rs1;
        Double totalPointsFor = 0.0;
        int gameCount = 0;

        //Get Home Total Points Scored
        rs = stmt.executeQuery("SELECT home_score FROM CBB where home_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs.next()) {
            totalPointsFor += Double.valueOf(rs.getString(1));
            gameCount++;
        }

        //Get Away Total Points Scored
        rs1 = stmt.executeQuery("SELECT away_score FROM CBB where away_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs1.next()) {
            totalPointsFor += Double.valueOf(rs1.getString(1));
            gameCount++;
        }
        Double avgPointsFor = totalPointsFor / gameCount;
        return avgPointsFor;
    }

    private Double getAvgPPGAgainst(String teamName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs, rs1;
        Double totalPointsAgainst = 0.0;
        int gameCount = 0;

        //Get Home Total Points Scored
        rs = stmt.executeQuery("SELECT home_score FROM CBB where away_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs.next()) {
            totalPointsAgainst += Double.valueOf(rs.getString(1));
            gameCount++;
        }

        //Get Away Total Points Scored
        rs1 = stmt.executeQuery("SELECT away_score FROM CBB where home_name = '" + teamName + "' AND (status = 'Final' OR status = 'F/OT')");
        while (rs1.next()) {
            totalPointsAgainst += Double.valueOf(rs1.getString(1));
            gameCount++;
        }
        Double avgPointsAgainst = totalPointsAgainst / gameCount;
        return avgPointsAgainst;
    }

    public void truncateTable() throws SQLException {
        System.out.println("TRUNCATING TABLE");
        Statement stmt = conn.createStatement();
        ResultSet rs;

        //Get Home Total Points Scored
        stmt.executeUpdate("DELETE FROM " + tableName + " WHERE 1=1");
        System.out.println("TABLE TRUNCATED");
    }
}
