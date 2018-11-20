package com.ariche.cbb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Period {

    @XmlElement(name="AwayScore")
    private String awayScore;

    @XmlElement(name="HomeScore")
    private String homeScore;

    public Double getAwayScore() {
        return (awayScore.equalsIgnoreCase("")) ? 0 : Double.valueOf(awayScore);
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public Double getHomeScore() {
        return (homeScore.equalsIgnoreCase("")) ? 0 :Double.valueOf(homeScore);
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }
}
