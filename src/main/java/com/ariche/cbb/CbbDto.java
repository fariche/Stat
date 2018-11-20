package com.ariche.cbb;


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
public class CbbDto {

    /**
     * @return the avgAwayPPGAgainst
     */
    public Double getAvgAwayPPGAgainst() {
        return Double.valueOf(avgAwayPPGAgainst);
    }

    /**
     * @param avgAwayPPGAgainst the avgAwayPPGAgainst to set
     */
    public void setAvgAwayPPGAgainst(String avgAwayPPGAgainst) {
        this.avgAwayPPGAgainst = avgAwayPPGAgainst;
    }

    /**
     * @return the avgHomePPGAgainst
     */
    public Double getAvgHomePPGAgainst() {
        return Double.valueOf(avgHomePPGAgainst);
    }

    /**
     * @param avgHomePPGAgainst the avgHomePPGAgainst to set
     */
    public void setAvgHomePPGAgainst(String avgHomePPGAgainst) {
        this.avgHomePPGAgainst = avgHomePPGAgainst;
    }

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
    private String avgAwayPPGFor;
    private String avgHomePPGFor;
    private String avgAwayPPGAgainst;
    private String avgHomePPGAgainst;

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
    public Double getHomeTeamScore() {
        return Double.valueOf(homeTeamScore);
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
    public Double getAwayTeamScore() {
        return Double.valueOf(awayTeamScore);
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
    public Double getPointSpread() {
        return Double.valueOf(pointSpread);
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
    public Double getAvgAwayPPGFor() {
        return Double.valueOf(avgAwayPPGFor);
    }

    /**
     * @param avgAwayPPG the avgAwayPPG to set
     */
    public void setAvgAwayPPGFor(String avgAwayPPGFor) {
        this.avgAwayPPGFor = avgAwayPPGFor;
    }

    /**
     * @return the avgHomePPG
     */
    public Double getAvgHomePPGFor() {
        return Double.valueOf(avgHomePPGFor);
    }

    /**
     * @param avgHomePPG the avgHomePPG to set
     */
    public void setAvgHomePPGFor(String avgHomePPGFor) {
        this.avgHomePPGFor = avgHomePPGFor;
    }

}
