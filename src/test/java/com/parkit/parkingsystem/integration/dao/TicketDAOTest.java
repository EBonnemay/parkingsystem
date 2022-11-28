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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {
        private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
        private static ParkingSpotDAO parkingSpotDAO;
        private static TicketDAO ticketDAO;
        private static DataBasePrepareService dataBasePrepareService;

        private static Ticket ticket;
        private static String testRegistration = "12345";

        private static Date date;
//ici rajouter parkingspot et ticket et reader util???
    //@Mock
    //private static Ticket ticket;

    //@Mock
    //private static ParkingSpot parkingSpot;


        @BeforeAll

        public static void setUp() {
            parkingSpotDAO = new ParkingSpotDAO();
            //les 3 suivants dans forEach?
            parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
            ticketDAO = new TicketDAO();
            ticketDAO.dataBaseConfig = dataBaseTestConfig;
            dataBasePrepareService = new DataBasePrepareService();
        }

        @BeforeEach
        private void setUpPerTest() throws Exception {

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

        public void saveTicketTest() {



            ticketDAO.saveTicket(ticket);///PROBLEME


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
            assertEquals ( true, ticketDAO.updateTicket(ticket));
           // double priceInDB = ticketDAO.getTicket(testRegistration).getPrice();
           // System.out.println("price expected is "+ priceExpected);
           // System.out.println("price in db is "+ priceInDB);
           // assertEquals(priceExpected, priceInDB);
            //assertEquals(date, ticketDAO.getTicket(testRegistration).getOutTime());

        }

}
       /* @Test
        public void updateTicketTest(){

        }*/

