package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.Fare;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;

public class CalculateFareForCarTest {
    public double calculFareCarMethod(Date dateIn, Date dateOut){

        DateTime inDateTime = new DateTime(dateIn);
        DateTime outDateTime = new DateTime(dateOut);

        Duration durationInMillisec = new Duration (inDateTime, outDateTime);
        long duration = durationInMillisec.getStandardMinutes();
        System.out.println("duration = "+ duration);

        double farePerHour = 1.5;
        return duration * farePerHour/60;

    }
}
