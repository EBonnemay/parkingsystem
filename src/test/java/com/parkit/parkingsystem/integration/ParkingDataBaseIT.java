package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.DateForParkingApp;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    // la classe ParkingDataBaseIt a attribut, qui existe avant même toute instanciation de ParkinDataBaseIT
    //ici j'instancie dataBaseTestConfig > instanciation d'un objet dataBaseTestCOnfig (boite à outils)
    private static ParkingSpotDAO parkingSpotDAO;  //déclaration d'un parkingSpotDAO : alloue un espace pr une DB parking
    private static TicketDAO ticketDAO; //déclaration d'un ticketDAT :alloue un espace pr une DB ticket
    private static DataBasePrepareService dataBasePrepareService; //déclaration d'un objet DataBaseprepareService : alloue 1 espace pr 1 DBPS

    // private static Ticket ticket; // alloue 1 espace pr 1 copie de Ticket

    //@InjectMocks
    //private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;//alloue 1 espace pr une copie de IRUtil
    //???no access modifier

    @Mock
    private static DateForParkingApp dateForParkingApp;//le mock désactive la classe. on est censé faire un when pour ttes les méthodes invoquées qui appartiennent à cette classe

    @BeforeAll //avant de lancer les tests de la classe,  exécuter ceci une fois pour toutes :
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO(); //instancier une nouvelle table parking
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig; //parkingspotDAT a un objet DBCOnfig en attribut.
        ticketDAO = new TicketDAO(); //instancier une nouvelle table tickets.
        ticketDAO.dataBaseConfig = dataBaseTestConfig;//connexion "db test config" passe dans ticket. et parking. dbconfig
        dataBasePrepareService = new DataBasePrepareService(); //instancie un objet prepareService (boîte à outils de nettoyage)
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1); //avant chaque test  : retourne 1 quand méthode readSelection sera appelée
        //avant chaque test : retourne FEDCBA quand readRegistration sera appelé
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("FEDCBA");
        //Date testDate = new Date(2022, 11, 14, 19, 44, 03);
        //when(ticket.getOutTime()).thenReturn(testDate);
        dataBasePrepareService.clearDataBaseEntries();

    }

    @AfterAll
    private static void tearDown() { //Après tous les tests exécute "tear down"(??)

    }

    @Test
    public void testParkingACar() throws Exception {

        Date date = new Date();

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(date);
        //Ticket ticket = new Ticket();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        //instancier un objet parkingService à partir de trois variables :
        // - un objet inputReaderUtil
        // - un parkingSport DAO (déjà instancié)
        // - un ticketDAO (dejà instanciée)
        //le constructeur appelé ci-dessus place ces trois paramètres en attributs de la classe parkingService
        parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        // dans table ticket : parking number, numéro du véhicule, heure d'arrivée sont dans la DB
        //String vehicleNumber = ticket.getVehicleRegNumber();

        //Ticket ticketInDB = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        //TERMINE


        //VERIFICATION
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());//DNAS LA DB TICKET je vais chch mon ticket
        // lié au bon n° de véhicule
        assertNotNull(ticket); //Je vérifie que le ticket correspondant à l'input n° véhicule était bien dans la DB.
        // TICKET NOT NULL OK

        ParkingSpot parkSpot = ticket.getParkingSpot();//
        // je récupère l'objet "place de parking" de cette instance du ticket

        boolean avail = parkSpot.isAvailable();//je vérifie si méthode available de mon parkspot est false
        System.out.println("avail is" + avail);
        assertFalse(avail);//méthode 1
        //mais ci dessus je ne regarde pas dans la base de données? je ne sais pas si save in DB a été fait...SI SI
        //ou non non je ne suis pas allée dans base parking.
        //peut-on considérer que oui, parce que je suis allée chercher ticket dans la DB??? mais pas dans parking
        //faire plutôt comme ci-dessous :
        //méthode 2 :
        System.out.println("parkingSpotDAO.updateParking(parkSpot) is" + parkingSpotDAO.updateParking(parkSpot));
        assertTrue(parkingSpotDAO.updateParking(parkSpot));
        //UN TEST OU DEUX TESTS?
        System.out.println("Time In in DB = " + ticket.getInTime());

    }

    @Test
    //ajout de throws dans une méthode de test...
    public void testParkingLotExit() throws Exception {
        testParkingACar();
        System.out.println(ticketDAO.getTicket("FEDCBA").getOutTime());
        System.out.println(ticketDAO.getTicket("FEDCBA").getPrice());
        Calendar cal = Calendar.getInstance();
        //Date dateTimeOutInTestExit = new Date(2022 - 1900, 12 - 1, 19, 22, 30, 00);
        //System.out.println("dateTimeOutInTestExit is "+ dateTimeOutInTestExit);
        cal.set(2022, 12 - 1, 19, 22, 30, 00);
        Date dateTimeOutInTestExitFromCal = cal.getTime();
        System.out.println("dateTimeOutInTestExitFromCal is "+ dateTimeOutInTestExitFromCal);
        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateTimeOutInTestExitFromCal);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp); //parce que je vais avoir besoin d'un
        //nouvel outil de communication avec la DB sur base de mes variables de classe déf ci dessus (inputRU, PSDAO, TDAO)

        parkingService.processExitingVehicle();

        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();
        Ticket ticket = ticketDAO.getTicket(encodedRegistration); // On rehydate l'objet ticket avec la valeur de la DB

        //TODO: check that the fare generated and out time are populated (remplis) correctly in the database
        Date getOutTimeTicketDAO = ticket.getOutTime();
        Double price = ticket.getPrice();

        //assertThat(ticket.getOutTime().isNotNull());
        System.out.println(price);
        //assertThat(price);
        assertThat(getOutTimeTicketDAO);
    }


}