package com.parkit.parkingsystem.integration.dao;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotDaoTest {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBasePrepareService dataBasePrepareService;


    @BeforeAll

    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {

        dataBasePrepareService.clearDataBaseEntries();

    }
    @Test
    public void getNextAvailableSlotBikeTest() {
        //Arrange

        ParkingType parkingType = ParkingType.BIKE;

        //Act
        int number = parkingSpotDAO.getNextAvailableSlot(parkingType);
        //Assert

        assertEquals(4, number);


    }

    @Test
    public void getNextAvailableSlotCarTest() {
        //Arrange

        ParkingType parkingType = ParkingType.CAR;

        //Act
        int number = parkingSpotDAO.getNextAvailableSlot(parkingType);
        //Assert
        assertEquals(1, number);
        // (ou ParkingType.valieOf(CAR))

    }
    @Test
    public void updateParkingSpotWhenIncomingTest() {
        //ARRANGE
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,true );

        parkingSpot.setAvailable(false);


        //ACT
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        //ASSERT


        assertEquals(true, result);


    }
    @Test
    public void updateParkingSpotWhenOutcomingTest(){

            //ARRANGE
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,true );

            parkingSpot.setAvailable(true);


            //ACT
            boolean result = parkingSpotDAO.updateParking(parkingSpot);
            //ASSERT


            assertEquals(true, result);


        }

    }
