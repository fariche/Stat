package com.ariche.cbb;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ArrayOfTeam")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrayOfTeam {

    /**
     * @return the team
     */
    public List<Team> getTeams() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeams(List<Team> team) {
        this.team = team;
    }

    @XmlElement(name = "Team")
    private List<Team> team;

   
}
