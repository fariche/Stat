package com.ariche.cbb;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CbbAppDbUpdate {

    public static void main(String[] args) throws IOException, JAXBException, SQLException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MMM-dd");
        
        //First Day of the season
        Date startDate = formatter2.parse("2018-NOV-08");
        
//      Date endDate = formatter2.parse("2018-NOV-24");
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(2);
      //  String end = endDate.format(DateTimeFormatter.ofPattern("yyyy-MMM-dd"));
        


        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
       // LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        //Set endDate to today plus 2 days
    //    endDate = endDate

        //Create Sports Object
        CbbImpl sports = new CbbImpl();
        
        //Crearte DB Connection
        sports.createConnection();
        
        //Truncate table
        sports.truncateTable();

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            //Format the current date in yyyy-MMM-dd
            String curDay = date.getYear() + "-" + date.getMonth().toString().substring(0, 3) + "-";
            curDay += (date.getDayOfMonth() <= 9) ? "0" + date.getDayOfMonth() : date.getDayOfMonth();
            
            System.out.println("Current Date formatted: "+curDay.toString());
            
            //Connect to the API and update get the XML 
            String xml = sports.connect(curDay);

            //Move XML into Pojo via JaxB
             ArrayOfGame games = sports.StringToJaxb(xml);
             
            //Insert Data into the database
            sports.pojoToDb(games);

        }

        System.out.println("Done");
    }
}
