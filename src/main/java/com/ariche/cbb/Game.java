package com.ariche.cbb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Game {

    /**
     * @return the rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * @return the conference
     */
    public String getConference() {
        return conference;
    }

    /**
     * @param conference the conference to set
     */
    public void setConference(String conference) {
        this.conference = conference;
    }

    @XmlElement(name="Status")
    private String status;

    @XmlElement(name="DateTime")
    private String dateTime;

    @XmlElement(name="AwayTeam")
    private String awayTeamName;

    @XmlElement(name="HomeTeam")
    private String homeTeamName;

    @XmlElement(name="HomeTeamScore")
    private String homeTeamScore;

    @XmlElement(name="AwayTeamScore")
    private String awayTeamScore;

    @XmlElement(name="PointSpread")
    private String pointSpread;

    @XmlElement(name="OverUnder")
    private String overUnder;

    @XmlElement(name="AwayTeamMoneyLine")
    private String awayTeamMoneyLine;

    @XmlElement(name="HomeTeamMoneyLine")
    private String homeTeamMoneyLine;

    @XmlElement(name="Periods")
    private Periods periods;
    
    private String rank;
    
    private String conference;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public Double getHomeTeamScore() {
        return (homeTeamScore.equalsIgnoreCase("")) ? 0 :Double.valueOf(homeTeamScore);
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public Double getAwayTeamScore() {
        return (awayTeamScore.equalsIgnoreCase("")) ? 0 :Double.valueOf(awayTeamScore);
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public Double getPointSpread() {
        return (pointSpread.equalsIgnoreCase("")) ? 0 : Double.valueOf(pointSpread);
    }

    public void setPointSpread(String pointSpread) {
        this.pointSpread = pointSpread;
    }

    public Double getOverUnder() {
        return (overUnder.equalsIgnoreCase("")) ? 0 :Double.valueOf(overUnder);
    }

    public void setOverUnder(String overUnder) {
        this.overUnder = overUnder;
    }

    public Double getAwayTeamMoneyLine() {
        return (awayTeamMoneyLine.equalsIgnoreCase("")) ? 0 :Double.valueOf(awayTeamMoneyLine);
    }

    public void setAwayTeamMoneyLine(String awayTeamMoneyLine) {
        this.awayTeamMoneyLine = awayTeamMoneyLine;
    }

    public Double getHomeTeamMoneyLine() {
        return (homeTeamMoneyLine.equalsIgnoreCase("")) ? 0 :Double.valueOf(homeTeamMoneyLine);
    }

    public void setHomeTeamMoneyLine(String homeTeamMoneyLine) {
        this.homeTeamMoneyLine = homeTeamMoneyLine;
    }

    public Periods getPeriods() {
        return periods;
    }

    public void setPeriods(Periods periods) {
        this.periods = periods;
    }
}
