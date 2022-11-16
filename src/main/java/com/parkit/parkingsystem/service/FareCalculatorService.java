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
        Date inHour = ticket.getInTime();
        DateTime inHourJo = new DateTime(inHour);

        //retrait Emma
        //int inHour = ticket.getInTime().getHours();
        //ajout print Emma
        System.out.println("inHourJoda = " + inHourJo);
        //ajout Emma
        Date outHour = ticket.getOutTime();
        DateTime outHourJo = new DateTime(outHour);
        //retrait Emma
        //int outHour = ticket.getOutTime().getHours();
        //ajout print Emma
        System.out.println("outHourJava =" + outHour);
        System.out.println("outHourJoda = " + outHourJo);
        //TODO: Some tests are failing here. Need to check if this logic is correct
        //ôté par Emma
        //int  duration = outHour - inHour;
        Duration durationInMillisec = new Duration (inHourJo, outHourJo);
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