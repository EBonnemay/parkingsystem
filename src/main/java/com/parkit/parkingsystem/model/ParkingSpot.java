package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;
import lombok.Data;

@Data
public class ParkingSpot {

    private final int number;
    private final ParkingType parkingType;
    private boolean isAvailable;

    //constructeur passe les paramètres en attributs
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
        this.number = number;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return number;
    }
}