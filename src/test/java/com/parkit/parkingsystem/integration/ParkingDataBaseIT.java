package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateForParkingApp;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static DateForParkingApp dateForParkingApp;

    @BeforeAll

    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    public static void tearDown() {

    }

    @Test

    public void testParkingACar()  {
        //GIVEN - ARRANGE
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        dataBasePrepareService.clearDataBaseEntries();
        Date date = new Date();

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(date);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        //WHEN - ACT
        parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

        Ticket ticket;
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());//DNAS LA DB TICKET je vais chch mon ticket
        //THEN - ASSERT
        assertNotNull(ticket);

        ParkingSpot parkSpot = ticket.getParkingSpot();
       assertTrue(parkingSpotDAO.updateParking(parkSpot));
        boolean avail = parkSpot.isAvailable();

        assertFalse(avail);

    }

    @Test

    //THROWS EXCEPTION CI-DESSOUS?
    public void testParkingABike()  {
        //GIVEN - ARRANGE
        when(inputReaderUtil.readSelection()).thenReturn(2);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        dataBasePrepareService.clearDataBaseEntries();
        Date date = new Date();

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(date);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        //WHEN - ACT
        parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

        Ticket ticket;
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());//DNAS LA DB TICKET je vais chch mon ticket
        //THEN - ASSERT
        assertNotNull(ticket);

        ParkingSpot parkSpot = ticket.getParkingSpot();
        assertTrue(parkingSpotDAO.updateParking(parkSpot));
        boolean avail = parkSpot.isAvailable();
        assertFalse(avail);

    }


    @Test
    //ajout de throws dans une méthode de test...
    public void testParkingLotExitCAR() {


        testParkingACar();
        System.out.println(ticketDAO.getTicket("ABCDEF").getOutTime());
        System.out.println(ticketDAO.getTicket("ABCDEF").getPrice());
        long timeIn = ticketDAO.getTicket("ABCDEF").getInTime().getTime();
        long timeOut = timeIn + (60 * 60 * 1000);
        System.out.println("voici timeout "+ timeOut);

       Date dateOut = new Date(timeOut);

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateOut);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp); //parce que je vais avoir besoin d'un

        parkingService.processExitingVehicle();

        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();

        //TODO: check that the fare generated and out time are populated (remplis) correctly in the database

        assertTrue(ticketDAO.getTicket(encodedRegistration).getPrice() > 0);
        assertNotNull(ticketDAO.getTicket(encodedRegistration).getOutTime());
    }
    @Test

    public void testParkingLotExitBIKE() {
        //GIVEN - ARRANGE

        testParkingABike();

        long timeIn = ticketDAO.getTicket("GHIJKL").getInTime().getTime();
        long timeOut = timeIn + (60 * 60 * 1000);
        System.out.println("voici timeout "+ timeOut);

        Date dateOut = new Date(timeOut);

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateOut);


        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp); //parce que je vais avoir besoin d'un

        //WHEN - ACT
        parkingService.processExitingVehicle();

        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();

        //TODO: check that the fare generated and out time are populated (remplis) correctly in the database
        //THEN - ASSERT
        assertTrue(ticketDAO.getTicket(encodedRegistration).getPrice() > 0);
        assertNotNull(ticketDAO.getTicket(encodedRegistration).getOutTime());
    }

    @Test
    public void differentTicketsForMultipleParkingsSameCarTest(){
        //GIVEN - ARRANGE
        testParkingLotExitCAR();
        int id = ticketDAO.getTicket("ABCDEF").getId();
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


        Date date = new Date();

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(date);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        //WHEN - ACT
        parkingService.processIncomingVehicle();


        int id2 = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber()).getId();
        //THEN - ASSERT
        assertNotEquals(id2, id);
        assertEquals(2, id2);
    }

}