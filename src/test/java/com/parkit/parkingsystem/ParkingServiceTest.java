package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.DateForParkingApp;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            //ticket.setOutTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            System.out.println("time in millis is = " + ticket.getInTime());
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

//quand la méthode parkingSpotDao.updateParking(n'importe q instance de la classe parkingSpot) te demande si cette place a été mise à jour, réponds "VRAI"
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            when(dateForParkingApp.getDateForParkingApp()).thenReturn(new Date(System.currentTimeMillis() - (60*60*1000)));
            System.out.println("current time is = " + (System.currentTimeMillis() - (60*60*1000)));
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }


    @Test
    public void processIncomingVehicleTest(){

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when( parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.processIncomingVehicle();
        //verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
        verify (parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
    }

    /*@Test
    public void processIncomingVehicleTestReadSelection(){

        when(inputReaderUtil.readSelection()).thenReturn(4);
        when( parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.processIncomingVehicle();
        //verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
        verify (parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
    }*/

   /* @Test
    public void getNextParkingNumberIfAvailableWithParkingNumberErrorTest() throws Exception{

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.processIncomingVehicle();
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        assertThrows(Exception.class, () -> parkingService.processIncomingVehicle());

        //when(inputReaderUtil.readSelection()).thenReturn(4);
        //when( parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        //parkingService.getNextParkingNumberIfAvailable();
        //Exception exception = assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());
       // assertEquals("Error fetching parking number from DB. Parking slots might be full", Exception.getMessage());
        //verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        //verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        //verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        //verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
        //verify (parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
    }*/
    @Test
    public void processExitingVehicleTest(){
        //ci-dessous nécessaire?
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
    }
   /* @Test
    public void processExitingVehicleTestException(){
        //ci-dessous nécessaire?
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
        verify(dateForParkingApp, Mockito.times(1)).getDateForParkingApp();
    }*/
}
