package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
//ajout
import com.parkit.parkingsystem.service.DateForParkingApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;
    //ajout
    private DateForParkingApp dateForParkingApp ;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO, DateForParkingApp dateForParkingApp){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
        this.dateForParkingApp = dateForParkingApp;
    }

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = getVehichleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark its availability as false

                Date inTime = dateForParkingApp.getDateForParkingApp();
                //Date inTime = new Date();//when no parameter, cur date...
                //System.out.println(inTime1);
               // DateFormat defaultFormat = new SimpleDateFormat("EEE,MMM dd HH:mm:ss z yyyy");
                //DateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                //String inTimeString = defaultFormat.format(inTime1);
                //Date inTime = DBFormat.parse(inTimeString);



                System.out.println("inTime has just been set at " + inTime);
                Ticket ticket = new Ticket();
                //if (ticket.getRecurrent())
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);

                if(ticketDAO.getTicket(vehicleRegNumber)==null){
                    //considère le client comme NON RECURRENT
                }


                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);
            }
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            logger.error("Error parsing user input for type of vehicle", ie);
        }catch(Exception e){
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        try{
            String vehicleRegNumber = getVehichleRegNumber();
           // LIGNE CI-DESSOUS PROBLEME : IL RECUPERE LE PREMIER NUMERO DE TICKET CORR A IMMATRICULATION
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber); //récupère objet ticket corr à immatriculation dans la TABLE TICKET
            int number_of_tickets = ticketDAO.getNumberOfTickets(vehicleRegNumber);
            //ajout ICI il y avait un new dateForParkingApp, nouvel objet différent du mock ATTENTION ATTENTION
            Date outTime = dateForParkingApp.getDateForParkingApp();

            //enlevé
            //Date outTime = new Date();  // DATE ACTUELLE

            ticket.setOutTime(outTime); //complète les informations du ticket:: null POinter exception

            fareCalculatorService.calculateFare(ticket, number_of_tickets); // calcule le prix à partir des données du ticket
            ticket.setRecurrent(true);
            if(ticketDAO.updateTicket(ticket)) { //place les données objet ticket DANS TABLE TICKET et renvoie oui ou non
                ParkingSpot parkingSpot = ticket.getParkingSpot(); //récupère objet parkingSpot dans objet ticket
                parkingSpot.setAvailable(true); //modifie available de ce parkingSpot à True
                parkingSpotDAO.updateParking(parkingSpot); //PLACE LES INFOS DE L4OBJET parking spot DANS LA BASE DE DONNEE PARKING
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            }else{
                System.out.println("Unable to update ticket information. Error occurred");
            }
        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }
    }
}
