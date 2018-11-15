
import javax.xml.bind.annotation.XmlElement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author whip
 */
public class CfbDto {

    private String week;
    private String status;
    private String dateTime;
    private String awayTeamName;
    private String homeTeamName;
    private String homeTeamScore;
    private String awayTeamScore;
    private String pointSpread;
    private String overUnder;
    private String awayTeamMoneyLine;
    private String homeTeamMoneyLine;
    private Periods periods;
    private String avgAwayPPG;
    private String avgHomePPG;

    /**
     * @return the week
     */
    public String getWeek() {
        return week;
    }

    /**
     * @param week the week to set
     */
    public void setWeek(String week) {
        this.week = week;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the awayTeamName
     */
    public String getAwayTeamName() {
        return awayTeamName;
    }

    /**
     * @param awayTeamName the awayTeamName to set
     */
    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    /**
     * @return the homeTeamName
     */
    public String getHomeTeamName() {
        return homeTeamName;
    }

    /**
     * @param homeTeamName the homeTeamName to set
     */
    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    /**
     * @return the homeTeamScore
     */
    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    /**
     * @param homeTeamScore the homeTeamScore to set
     */
    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    /**
     * @return the awayTeamScore
     */
    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    /**
     * @param awayTeamScore the awayTeamScore to set
     */
    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    /**
     * @return the pointSpread
     */
    public String getPointSpread() {
        return pointSpread;
    }

    /**
     * @param pointSpread the pointSpread to set
     */
    public void setPointSpread(String pointSpread) {
        this.pointSpread = pointSpread;
    }

    /**
     * @return the overUnder
     */
    public String getOverUnder() {
        return overUnder;
    }

    /**
     * @param overUnder the overUnder to set
     */
    public void setOverUnder(String overUnder) {
        this.overUnder = overUnder;
    }

    /**
     * @return the awayTeamMoneyLine
     */
    public String getAwayTeamMoneyLine() {
        return awayTeamMoneyLine;
    }

    /**
     * @param awayTeamMoneyLine the awayTeamMoneyLine to set
     */
    public void setAwayTeamMoneyLine(String awayTeamMoneyLine) {
        this.awayTeamMoneyLine = awayTeamMoneyLine;
    }

    /**
     * @return the homeTeamMoneyLine
     */
    public String getHomeTeamMoneyLine() {
        return homeTeamMoneyLine;
    }

    /**
     * @param homeTeamMoneyLine the homeTeamMoneyLine to set
     */
    public void setHomeTeamMoneyLine(String homeTeamMoneyLine) {
        this.homeTeamMoneyLine = homeTeamMoneyLine;
    }

    /**
     * @return the periods
     */
    public Periods getPeriods() {
        return periods;
    }

    /**
     * @param periods the periods to set
     */
    public void setPeriods(Periods periods) {
        this.periods = periods;
    }

    /**
     * @return the avgAwayPPG
     */
    public String getAvgAwayPPG() {
        return avgAwayPPG;
    }

    /**
     * @param avgAwayPPG the avgAwayPPG to set
     */
    public void setAvgAwayPPG(String avgAwayPPG) {
        this.avgAwayPPG = avgAwayPPG;
    }

    /**
     * @return the avgHomePPG
     */
    public String getAvgHomePPG() {
        return avgHomePPG;
    }

    /**
     * @param avgHomePPG the avgHomePPG to set
     */
    public void setAvgHomePPG(String avgHomePPG) {
        this.avgHomePPG = avgHomePPG;
    }

}
