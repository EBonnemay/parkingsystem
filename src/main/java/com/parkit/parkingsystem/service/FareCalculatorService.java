package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
//ôté par Emma
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        //ajout Emma
        Date inHourDate = ticket.getInTime();
        DateTime inHour = new DateTime(inHourDate);

        //retrait Emma
        //int inHour = ticket.getInTime().getHours();
        //ajout print Emma
        System.out.println("inHour = " + inHour);
        //ajout Emma
        Date outHourDate = ticket.getOutTime();
        DateTime outHour = new DateTime(outHourDate);
        //retrait Emma
        //int outHour = ticket.getOutTime().getHours();
        //ajout print Emma
        System.out.println("outHour = " + outHour);
        //TODO: Some tests are failing here. Need to check if this logic is correct
        //ôté par Emma
        //int  duration = outHour - inHour;
        Duration durationInMillisec = new Duration (inHour, outHour);
        long duration = durationInMillisec.getStandardMinutes();
        System.out.println("duration = "+ duration);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR/60);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR/60);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}