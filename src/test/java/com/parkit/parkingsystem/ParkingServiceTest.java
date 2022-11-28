package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateForParkingApp;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;// crée un double de la classe InputReaderUtil
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @Mock
    private static DateForParkingApp dateForParkingApp;

    @Test
    public void processIncomingVehicleTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            when(dateForParkingApp.getDateForParkingApp()).thenReturn(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
    }

    @Test
    public void processExitingVehicleTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            when(dateForParkingApp.getDateForParkingApp()).thenReturn(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
    }
    @Test
    public void getNextCarParkingNumberIfNotAvailableTest(){
        try {
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
            // ARRANGE
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

            // ACT

            // ASSERT
            assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());

    }
    @Test
    public void getNextBikeParkingNumberIfNotAvailableTest(){
        try {

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("GHIJKL");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
        // ARRANGE
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        // ACT

        // ASSERT
        assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());

    }
}
