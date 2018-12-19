package com.ariche.cbb;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CbbAppCalc {

    private static final LocalDate DAY_TO_TEST = LocalDate.now().plusDays(0);
    private static final int DAYS_BACK = 15; //Games started Nov 8 and more data the better
    private static final double CONFIDENCE = 13.0;

    public static void main(String[] args) throws IOException, JAXBException, SQLException {
        //Get Data from DB and store in DTO
        CbbImpl sports = new CbbImpl();

        //Connect to the API and update get the XML 
        List<CbbDto> cbbDtos = sports.dbToDto(DAY_TO_TEST.toString());

        //Calc Winners
        System.out.println("Calculating Winners for: "+ DAY_TO_TEST);
        sports.calcWinners(cbbDtos, CONFIDENCE, DAYS_BACK);

        //Test Calculation
        //sports.testCalc(16.0);
        //QC Picks
        System.out.println("Done");

    }
}
