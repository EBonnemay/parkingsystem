package com.parkit.parkingsystem.model;


import java.util.Date;
import lombok.Data;

@Data
public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;
}
