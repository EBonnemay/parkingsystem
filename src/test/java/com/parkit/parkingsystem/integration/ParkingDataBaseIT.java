package com.parkit.parkingsystem.integration;

import com.google.protobuf.GeneratedMessage;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.CalculateFareForCarTest;
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
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    // la classe ParkingDataBaseIt a attribut, qui existe avant même toute instanciation de ParkinDataBaseIT
    //ici j'instancie dataBaseTestConfig > instanciation d'un objet dataBaseTestCOnfig (boite à outils)
    private static ParkingSpotDAO parkingSpotDAO;  //déclaration d'un parkingSpotDAO : alloue un espace pr une DB parking
    private static TicketDAO ticketDAO; //déclaration d'un ticketDAT :alloue un espace pr une DB ticket
    private static DataBasePrepareService dataBasePrepareService; //déclaration d'un objet DataBaseprepareService : alloue 1 espace pr 1 DBPS

   // private static Ticket ticket; // alloue 1 espace pr 1 copie de Ticket

    @InjectMocks
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;//alloue 1 espace pr une copie de IRUtil
    //???no access modifier

    @Mock
    private static DateForParkingApp dateForParkingApp;

    @BeforeAll //avant de lancer les tests de la classe,  exécuter ceci :
    private static void setUp() throws Exception{
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
    private static void tearDown(){ //Après tous les tests exécute "tear down"(??)

    }

    @Test
    public void testParkingACar() throws Exception {

        //Ticket ticket = new Ticket();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        //instancier un objet parkingService à partir de trois variables :
        // - un objet inputReaderUtil
        // - un parkingSport DAO (déjà instancié)
        // - un ticketDAO (dejà instanciée)
        //le constructeur appelé ci-dessus place ces trois paramètres en attributs de la classe parkingService
        parkingService.processIncomingVehicle();
        //CETTE METHODE :
                //trouve une place si possible (retourne un parkingSpot object) : n°, type, available
                //parkingSpod.getId : retourne un int > 0 (n°place)
                //demande input n<) du véhicule > retourne string
                //set la place de parking à parkingSpot.available(false)
                // DANS LA CLASSE DB met à jour availability
                //instancie date entree inTime au moment présent
                //crée un ticket
                //set attributes of TICKET object (id, n°parking, n° véhicule, prix, heure entree, heure sortie)
                //save ticket in DB
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        // dans table ticket : parking number, numéro du véhicule, heure d'arrivée sont dans la DB
        //String vehicleNumber = ticket.getVehicleRegNumber();
        //System.out.println(vehicleNumber); >> pourquoi cette ligne donne-t-elle "null"??
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
        assertEquals(avail, false);//méthode 1
        //mais ci dessus je ne regarde pas dans la base de données? je ne sais pas si save in DB a été fait...SI SI
        //ou non non je ne suis pas allée dans base parking.
        //peut-on considérer que oui, parce que je suis allée chercher ticket dans la DB??? mais pas dans parking
        //faire plutôt comme ci-dessous :
        //méthode 2 :
        System.out.println("parkingSpotDAO.updateParking(parkSpot) is" + parkingSpotDAO.updateParking(parkSpot));
        assertEquals(parkingSpotDAO.updateParking(parkSpot), true);
        System.out.println("Time In in DB = "+ ticket.getInTime());

    }

    @Test
    //ajout de throws dans une méthode de test...
    public void testParkingLotExit() throws Exception {
        testParkingACar();

        // getting the system date Wed Nov 16 17:21:38 CET 2022
/*c'est ce bloc qui fuot le bordel > réinstanciation de dateForParking alors que mock est déjà passé dessus.
        dateForParkingApp = new DateForParkingApp(2022, 11, 16, 22, 30, 00); >> ATTENTION ICI NEW
        Date dateTimeTest = dateForParkingApp.getDateForParkingApp();
        // getting the object of the Timestamp class
        Timestamp outTimeTest = new Timestamp(dateTimeTest.getTime());

        // printing the timestamp of the current date
        System.out.println("here is timestamp object ts = "+ outTimeTest);

        System.out.println("here is date objext 'outTimeTest' = "+ outTimeTest);
*/

        Date dateTimeTest = new Date(2022-1900, 11-1, 17, 22, 30, 00);
        Timestamp outTimeTest = new Timestamp(dateTimeTest.getTime());

        //doReturn(dateTimeTest).when(dateForParkingApp).getDateForParkingApp();
        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateTimeTest);

        // when(dateForParkingApp.getDateForParkingApp() ).thenReturn(dateTimeTest);
       // System.out.println("mockito order given");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp); //parce que je vais avoir besoin d'un
        //nouvel outil de communication avec la DB sur base de mes variables de classe déf ci dessus (inputRU, PSDAO, TDAO)
        parkingService.processExitingVehicle();
        //Ticket ticket = new Ticket();
        // quand je crée un objet Ticket ici >> reassigned local variable(?)
        //quand je n'en crée pas, ma DB est vide et l'outTest est null FAUX
        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();
        Ticket ticket = ticketDAO.getTicket(encodedRegistration); // On rehydate l'objet ticket avec la valeur de la DB
        // notons que getTicket crée un ticket
        //TODO: check that the fare generated and out time are populated (remplis) correctly in the database
        Date getOutTimeTicketDAO = ticket.getOutTime();
       String outTimeTestString = outTimeTest.toString();
       String getOutTimeTicketDAOString = getOutTimeTicketDAO.toString();

        System.out.println("outTimeTest in DB = "+ ticket.getOutTime());
        //assertThat(ticket.getOutTime()).as("out date for %s vehicle number", inputReaderUtil.readVehicleRegistrationNumber()).isEqualTo(outTimeTest);
        System.out.println("my outTime in String is = "+outTimeTestString.substring(0,19));
        System.out.println("my DB time Out in String is = "+ getOutTimeTicketDAOString.substring(0,19));
        assertEquals(outTimeTestString.substring(0,19), getOutTimeTicketDAOString.substring(0,19));


    }
    @Test
    public void testCorrectFareInDB () throws Exception {
        Date dateTimeTestIn = new Date(2022-1900, 11-1, 18, 6, 30, 0);
        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateTimeTestIn);
        //Timestamp outTimeTestIn = new Timestamp(dateTimeTestIn.getTime());
        testParkingACar();
        Date dateTimeTestOut = new Date(2022-1900, 11-1, 18, 8, 30, 0);
        //Timestamp outTimeTestOut = new Timestamp(dateTimeTestOut.getTime());
        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateTimeTestOut);
        CalculateFareForCarTest calculateFareForCarTest = new CalculateFareForCarTest();
        Double expectedFare = calculateFareForCarTest.calculFareCarMethod(dateTimeTestIn, dateTimeTestOut);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
        parkingService.processExitingVehicle();
        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();
        //Ticket ticket = ticketDAO.getTicket(encodedRegistration); // On rehydate l'objet ticket avec la valeur de la DB
        //double actualFareFoundDB = ticket.getPrice();

        //System.out.println("my outTime in String is = "+outTimeTestString.substring(0,19));
        //System.out.println("my DB time Out in String is = "+ getOutTimeTicketDAOString.substring(0,19));

        assertEquals(Optional.of(expectedFare), Optional.of(ticketDAO.getTicket(encodedRegistration).getPrice()));

    }


}
