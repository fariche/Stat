package com.ariche.cbb;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CbbAppCalc {

    private static final LocalDate TODAY = LocalDate.now();

    public static void main(String[] args) throws IOException, JAXBException, SQLException {
        //Get Data from DB and store in DTO
        CbbImpl sports = new CbbImpl();

        //Connect to the API and update get the XML 
        List<CbbDto> cbbDtos = sports.dbToDto(TODAY.toString());

        //Calc Winners
        sports.calcWinners(cbbDtos, 13.0);

        //Test Calculation
        //sports.testCalc(16.0);
        //QC Picks
        System.out.println("Done");
    }
}
