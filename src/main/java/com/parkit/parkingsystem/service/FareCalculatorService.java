package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, int number_of_tickets) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime();
        long inHourMillis = inHour.getTime();
        Date outHour = ticket.getOutTime();
        long outHourMillis = outHour.getTime();

        long durationJava = outHourMillis - inHourMillis;

        //TODO: Some tests are failing here. Need to check if this logic is correct

        if (durationJava < 1800000) {
            System.out.println("You stayed less than 30 minutes, parking is free");
            ticket.setPrice(0);

        } else {
            if (number_of_tickets < 1){
                throw new IllegalArgumentException("Number of tickets for 1 vehicle can not be inferior to 1:");
            }
            double discount = number_of_tickets > 1 ? 5 : 0;
            if (discount == 5) {
                System.out.println("5% on price");
            }

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice((durationJava * Fare.CAR_RATE_PER_HOUR / 3600000) * (100 - discount) / 100);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((durationJava * Fare.BIKE_RATE_PER_HOUR / 3600000) * (100 - discount) / 100);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }

            BigDecimal bd = BigDecimal.valueOf(ticket.getPrice()).setScale(2, RoundingMode.HALF_UP);
            double result = bd.doubleValue();
            ticket.setPrice(result);

        }
    }
}