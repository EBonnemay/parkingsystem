package com.parkit.parkingsystem.integration.dao;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;

import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {
        private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
        private static DataBasePrepareService dataBasePrepareService;

        private static Ticket ticket;
        private static final String testRegistration = "12345";


        @BeforeAll

        public static void setUp() {
            ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
            //les 3 suivants dans forEach?
            parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
            ticketDAO = new TicketDAO();
            ticketDAO.dataBaseConfig = dataBaseTestConfig;
            dataBasePrepareService = new DataBasePrepareService();
        }

        @BeforeEach
        public void setUpPerTest() {

            dataBasePrepareService.clearDataBaseEntries();
            ticket = new Ticket();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            //String testRegistration = "12345";
            ticket.setId(1);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(testRegistration);
            ticket.setPrice(0);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() - (180 * 60 * 1000));
            ticket.setInTime(date);
            ticket.setOutTime(null);

//délcarer un ticketDAO.databaseconfig et instancier un ticketDAO?
        }

        @Test
        public void getNumberOfTicketTest(){
            ticketDAO.saveTicket(ticket);
            double priceExpected = 4.5;
            Date date2 = new Date();
            ticket.setOutTime(date2);
            ticket.setPrice(priceExpected);

            ticketDAO.updateTicket(ticket);
            //assertEquals(1,ticketDAO.getNumberOfTickets(testRegistration));
            ticket = new Ticket();
            ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, false);
            //String testRegistration = "12345";
            ticket.setId(2);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(testRegistration);
            ticket.setPrice(0);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() - (180 * 60 * 1000));
            ticket.setInTime(date);
            ticket.setOutTime(null);
            ticketDAO.saveTicket(ticket);
            assertEquals(2,ticketDAO.getNumberOfTickets(testRegistration));

    }

        @Test

        public void saveTicketTest() {



            ticketDAO.saveTicket(ticket);


            //ASSERT
            assertEquals(ticket.getId(),ticketDAO.getTicket(testRegistration).getId());
        }

        @Test
        public void getTicketTest(){
            //ARRANGE

            ticketDAO.saveTicket(ticket);
            //ACT, //ASSERT
            assertEquals(ticket.getId(),ticketDAO.getTicket(testRegistration).getId());
        }
        @Test
        public void updateTicketTest(){
            ticketDAO.saveTicket(ticket);
            double priceExpected = 4.5;
            Date date2 = new Date();
            ticket.setOutTime(date2);
            ticket.setPrice(priceExpected);

            ticketDAO.updateTicket(ticket);
            assertTrue(ticketDAO.updateTicket(ticket));

        }


}
