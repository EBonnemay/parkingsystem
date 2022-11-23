package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }
    @Tag("calculateFareWithDurationsAtLeast30mnWithoutDiscounts")
    @DisplayName("Vérifier le calcul du ticket sans réduction ou gratuité, durée >= 30mn - tests sur les durées et dateTimes entrée/sortie")

    @Test
    public void calculateFareCarWithFirstTimeClientStaying1HourLength() {

        Date inTime = new Date(); //dateTime actuel
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//dateTime inTime = dateOutTime -1h
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 1);

        double expected = Fare.CAR_RATE_PER_HOUR;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();


        assertEquals(expectedDouble, ticket.getPrice() );
    }
    @Test
    public void calculateFareCarForFirstTimeCLientWith45mnParkingTime() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 1);

        double expected = 0.75* Fare.CAR_RATE_PER_HOUR;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals((double)expectedDouble, ticket.getPrice());
    }
    @Test
    public void calculateFareBikeWithFirstTimeClientStaying30mn() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 1);

        double expected = 0.5* Fare.BIKE_RATE_PER_HOUR;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithFirstTimeClientWith1DayParkingTime() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 1);

        double expected = 24* Fare.CAR_RATE_PER_HOUR;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());
    }

   @DisplayName("Vérifier que l'input d'une date de sortie de parking postérieure à la date d'entrée produit une exception")
    @Test

    public void calculateFareBikeWithFutureInTime() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, 1));
    }

    @Tag("LessThan30mnFreeParking")
    @DisplayName("Appliquer la gratuité <30mn pour tous les types de véhiclules")

    @Test
    public void calculateFareBikeWithFirstTimeClientStayingLessThan30mn(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,1);



        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithFirstTimeClientStayingLessThan30mn(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,1);



        assertEquals(0, ticket.getPrice());
    }
    @Test
    @DisplayName("Soit une durée de stationnement de 30 et 4 sec minutes à cheval sur deux jours, lorsque l'utilisateur sort du parking, le système n'applique pas la gratuité")
    public void calculateFareBikeWithFirstTimeClientStaying30mnAnd4SecOn2Days(){

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2022, Calendar.NOVEMBER, 30, 23, 58, 4);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2022, Calendar.DECEMBER, 1, 0, 28, 8);
        long dateInLong = cal1.getTimeInMillis();
        long dateOutLong = cal2.getTimeInMillis();

        Date dateIn = cal1.getTime();

        Date dateOut = cal2.getTime();

        double lengthInSec = (dateOutLong-dateInLong)/1000;


        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(dateIn);
        ticket.setOutTime(dateOut);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,1);

        double expected = Fare.BIKE_RATE_PER_HOUR/3600*lengthInSec;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());///0.5 expected,

    }


    @Test
    @DisplayName("Soit une durée de stationnement de 30 et 4 sec minutes à cheval sur deux jours, lorsque l'utilisateur sort du parking, le système n'applique pas la gratuité")
    public void calculateFareCarWithLongParking(){

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2022, Calendar.NOVEMBER, 30, 23, 58, 4);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, Calendar.MARCH, 1, 0, 28, 8);
        long dateInLong = cal1.getTimeInMillis();
        long dateOutLong = cal2.getTimeInMillis();

        Date dateIn = cal1.getTime();

        Date dateOut = cal2.getTime();

        double lengthInSec = (dateOutLong-dateInLong)/1000;


        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(dateIn);
        ticket.setOutTime(dateOut);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,1);

        double expected = Fare.CAR_RATE_PER_HOUR/3600*lengthInSec;
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());///0.5 expected,

    }
    @Tag("calculateFareWithMissingInformationsReturnsExceptions")
    @DisplayName("Soit une requête calculateFare dans laquelle ne figure pas de type de véhicule, lorsque cette méthode est appelée, alors elle renvoie une exception NullPointerException")
    @Test
    public void calculateFareWithUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, 1));
    }

    @Test
    @DisplayName("Soit une requête calculateFare dans laquelle le nombre de tickets correspondant à une immatriculation est inférieur à 1, lorsque cette méthode est appelée, alors elle renvoie une exception IllegalArgumentException")
    public void calculateFareWithKnownTypeCarAndUnknownClientProfile() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, -1));
    }



    @Tag("5%DiscountForRecurringClientsLength30mn0rMore")
    @DisplayName("Soit une requête calculateFare dans laquelle le client (voiture ou vélo) est récurrent et reste 30 minutes ou plus, lorsque cette méthode est appelée, alors elle renvoie un prix de ticket rabaissé de 5%")

    @Test
    public void calculateFareCarWithRecurringClientStaying14hAnd3sec() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2022, Calendar.NOVEMBER, 20, 8, 30, 4);
        long dateInLong = cal1.getTimeInMillis();
        Date dateIn = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2022, Calendar.NOVEMBER, 20, 22, 30, 7);
        long dateOutLong = cal2.getTimeInMillis();
        Date dateOut = cal2.getTime();


        double lengthInSec = (dateOutLong-dateInLong)/1000;

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(dateIn);
        ticket.setOutTime(dateOut);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 2);

        double expected = Fare.CAR_RATE_PER_HOUR/3600*lengthInSec - ((Fare.CAR_RATE_PER_HOUR/3600*lengthInSec)*Fare.DISCOUNT/100);

        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());
    }
    @Test
    public void calculateFareBikeWithRecurringClientStaying1h() {
        Date inTime = new Date(); //dateTime actuel
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//dateTime inTime = dateOutTime -1h
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 2);

        double expected = Fare.BIKE_RATE_PER_HOUR - (Fare.BIKE_RATE_PER_HOUR * Fare.DISCOUNT / 100);
        BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        double expectedDouble = bd.doubleValue();

        assertEquals(expectedDouble, ticket.getPrice());

    }


    @Tag("CombinationOf2discountConditions")
    @DisplayName("Soit une requête calculateFare dans laquelle le client vélo ou voiture est récurrent ET reste moins de 30 minutes, lorsque cette méthode est appelée, alors elle 0")


    @Test
    public void calculateFareCarWithRecurringClientStayingLessThan30mn (){
        Calendar cal = Calendar.getInstance();
        cal.set(2022, Calendar.NOVEMBER, 30, 8, 30, 0);
        Date dateTimeInInTest = cal.getTime();
        cal.set(2022, Calendar.NOVEMBER, 30, 8, 59, 0);
        Date dateTimeOutInTest = cal.getTime();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(dateTimeInInTest);
        ticket.setOutTime(dateTimeOutInTest);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, 2);

        assertEquals(0, ticket.getPrice());

    }
    @Test
    public void calculateFareBikeWithRecurringClientStayingLessThan30mn(){

        Calendar cal = Calendar.getInstance();
        cal.set(2022 , Calendar.NOVEMBER, 30, 8, 30, 0);
        Date dateTimeInInTest = cal.getTime();
        cal.set(2022 , Calendar.NOVEMBER, 30, 8, 59, 0);
        Date dateTimeOutInTest = cal.getTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(dateTimeInInTest);
        ticket.setOutTime(dateTimeOutInTest);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,2);

        assertEquals(0,ticket.getPrice());
    }
}