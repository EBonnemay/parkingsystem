package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
//ôté par Emma
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, int number_of_tickets) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            System.out.println("zut");
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime();
        DateTime inHourJo = new DateTime(inHour);


        Date outHour = ticket.getOutTime();
        DateTime outHourJo = new DateTime(outHour);


        //TODO: Some tests are failing here. LESQUELS? Need to check if this logic is correct

        Duration durationInMillisec = new Duration(inHourJo, outHourJo);
        long duration = durationInMillisec.getStandardMinutes();
        System.out.println("duration = " + duration);
        if (duration < 30) {
            System.out.println("Vous êtes resté moins de 30 minutes, c'est gratuit");
            ticket.setPrice(0);

        } else {
            double discount = number_of_tickets > 1 ? 5 : 0;
            if (discount == 5) {
                System.out.println("5% appliqué sur le prix");
            }

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR / 60) * (100 - discount) / 100);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR / 60) * (100 - discount) / 100);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }

        }
    }
}