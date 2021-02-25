import tables.*;

import java.awt.image.AreaAveragingScaleFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BookFlight {
    private final User u;
    private final Connection con;
    private final String s;

    public BookFlight(User u, Connection con, String s) {
        this.u = u;
        this.con = con;
        this.s = s;
    }

    public void booking(){
        System.out.println("\n\n"+ s +" Booking " + s);
        printAvailableAirports();
        String origin = airportSelection("origin");
        String destination = destinationAirport(origin);

        System.out.println("\nFlight from " + origin + " to " + destination + ":");
        ArrayList<Flight> listOfFlight1 = getDirectFlights(origin, destination);
        int choice1 = chooseFlightFromList(listOfFlight1);
        Flight originFlight = listOfFlight1.get(choice1 - 1);

        System.out.println("\nFlight from " + destination + " to " + origin + ":");
        ArrayList<Flight> listOfFlight2 = getDirectFlights(destination, origin);
        int choice2 = chooseFlightFromList(listOfFlight1);
        Flight destinationFlight = listOfFlight2.get(choice2 - 1);

        TicketBoardingPass wayIn = new TicketBoardingPass(originFlight.getID());
        TicketBoardingPass wayOut = new TicketBoardingPass(destinationFlight.getID());
        createTickets(wayIn, wayOut);

        int price = originFlight.getFlightCost() + destinationFlight.getFlightCost();
        System.out.println("\nTotal price for " + origin + "->" + destination+ ", " +
                destination + "->" + origin+ " = " + price + "$.");

        Scanner userInput = new Scanner(System.in);
        System.out.print("\t1. Proceed to payment\n\t2. Save reservation\n\t3. Abort\n\t=> ");
        int choice = 0;
        while (true) {
            try {
                // tables.User input to choose element of the menu
                choice = userInput.nextInt();
                if (choice > 0 && choice < 4){
                    break;
                } else {
                    System.out.print("Please input an integer between 1 and 3 => ");
                }

                // Catching if a user input something else than an integer
            } catch (InputMismatchException e) {
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. => ");
            }
        }

        Menu menu = new Menu(con,s);
        switch (choice){
            case 1 -> {
                Payment pay = createPayment();
                Reservation res = createReservationPayment(pay);
                finalizeTickets(wayIn, wayOut, res.getId());
                menu.customer(u);
            }
            case 2 -> {
                Reservation res = createReservationPayment();
                finalizeTickets(wayIn, wayOut, res.getId());
                menu.customer(u);
            }
            case 3 -> {
                menu.customer(u);}
        }
    }

    public int getIntChoice(int min, int max){
        Scanner userInput = new Scanner(System.in);
        int choice;
        while (true) {
            try {
                // tables.User input to choose element of the menu
                System.out.print("\t=> ");
                choice = userInput.nextInt();
                if (choice > min && choice < max) {
                    break;
                } else {
                    System.out.print("\tPlease input an integer between "+(min+1)+" and " + (max-1)+ " => ");
                }

                // Catching if a user input something else than an integer
            } catch (InputMismatchException e) {
                // Skip the last input
                userInput.next();
                System.out.print("\tThat’s not an integer. => ");
            }
        }
        return choice;
    }

    public String destinationAirport(String origin){
        ArrayList<Flight> listOfFlight = getOriginFlights(origin);
        if (listOfFlight.isEmpty()){
            System.out.println("There are currently no flight departing from "+ origin +".");
            booking();
        }
        int i = 1;
        System.out.print("\nCurrent list of Airport(s) destination from " + origin + " :\n");
        for (Flight f : listOfFlight) {
            System.out.println("\t"+(i++)+". = " + f.getDestAirport());

        }
        int choice = getIntChoice(0, i);
        return listOfFlight.get(choice -1).getDestAirport();
    }

    public void finalizeTickets(TicketBoardingPass wayIn, TicketBoardingPass wayOut, String id){
        wayIn.setReservationId(id);
        wayOut.setReservationId(id);
        wayIn.insertTicketBoardingPass(con);
        wayOut.insertTicketBoardingPass(con);
    }

    public void createTickets(TicketBoardingPass wayIn, TicketBoardingPass wayOut){
        System.out.println();
        wayIn.setSeat();
        wayOut.setSeat(wayIn.getSeat());

        wayIn.setPriority();
        wayOut.setPriority(wayIn.isPriority());

        wayIn.setBaggage();
        wayOut.setBaggage(wayIn.getBaggage());
    }

    public Reservation createReservationPayment(){
        Reservation res = new Reservation();
        res.setID();
        res.setUserId(u.getId());
        res.setStatus("pending");
        res.insertReservation(con);
        return res;
    }

    public Reservation createReservationPayment(Payment pay){
        Reservation res = new Reservation();
        res.setID();
        res.setUserId(u.getId());
        res.setPaymentId(pay.getId());
        if (pay.isSuccess()){
            res.setStatus("complete");
        } else {
            res.setStatus("failed");
        }
        res.insertReservation(con);
        return res;
    }

    public Payment createPayment(){
        Payment pay = new Payment();
        pay.setID();
        pay.setTimestamp();
        pay.attemptPayment();
        pay.insertPayment(con);
        return pay;
    }

    public int chooseFlightFromList(ArrayList<Flight> l) {
        int i = 1;
        for(Flight f : l){
            System.out.println("\t"+ (i++) + ". " + f.getAirlineCode() + " " + f.getFlightNumber() + " Departure date and time "
                    + f.getDepartureDateTime() + ", Price " + f.getFlightCost() + "$, Place left " +
                    PlaceLeft(f) + ".");
        }
        return getIntChoice(0, i);
    }

    public int PlaceLeft(Flight flt){
        int capacity = getCapacity(flt.getAirlineCode(), flt.getFlightNumber());
        int count = 0;
        String id = flt.getID();
        String table = "ticketboardingpass";
        try {
            String query = "SELECT count(*) from " + table + " WHERE flightID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, id);
            ResultSet rs = pStmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return capacity-count;
    }

    public int getCapacity(String fN, int aC){
        String table = "flightnumber";
        int capacity = 0;
        try {
            String query = "SELECT capacity from " + table + " WHERE airlineCode = ? AND flightNumber = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, fN);
            pStmt.setInt(2, aC);
            ResultSet rs = pStmt.executeQuery();
            rs.next();
            capacity = rs.getInt(1);
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return capacity;
    }

    public void printAvailableAirports(){
        Airport a1 = new Airport();
        ArrayList<Airport> listOfAirport = a1.listAirport(con);
        ArrayList<String> availableAirports = distinctFlights();
        System.out.print("\nCurrent list of available Airport(s) origin :\n");
        for (Airport a : listOfAirport) {
            if (availableAirports.contains(a.getIata())) {
            System.out.println("\t- IATA = " + a.getIata() + ", " + a.getName() + " "+ a.getCountryIso());
            }
        }
    }

    public ArrayList<String> distinctFlights(){
        ArrayList<String> listOfIata = new ArrayList<>();
        String table = "flight";
        try {
            String query = "SELECT DISTINCT depAirport from " + table;
            PreparedStatement pStmt = con.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                listOfIata.add(rs.getString(1));
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfIata;
    }

    public String airportSelection(String point){
        String s = "Enter " + point + " Airport IATA (3 chars only letters) => ";
        Airport createA = new Airport();
        String iata = createA.createIata(s);
        String check = createA.checkIata(con, iata);
        if (check.equals("failure")) {
            System.out.println("Airport not in database. Please refer to the list above.");
            airportSelection(point);
        }
        return iata;
    }

    public ArrayList<Flight> getOriginFlights(String origin){
        ArrayList<Flight> listOfFlight = new ArrayList<>();
        String table = "flight";
        try {
            String query = "SELECT * from " + table + " WHERE depAirport = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, origin);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                Flight f = new Flight(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getTimestamp(6),
                        rs.getTimestamp(7), rs.getInt(8));
                listOfFlight.add(f);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfFlight;
    }

    public ArrayList<Flight> getDirectFlights(String origin, String destination){
        ArrayList<Flight> listOfFlight = new ArrayList<>();
        String table = "flight";
        try {
            String query = "SELECT * from " + table + " WHERE depAirport = ? AND destAirport = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, origin);
            pStmt.setString(2, destination);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                Flight f = new Flight(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getTimestamp(6),
                        rs.getTimestamp(7), rs.getInt(8));
                listOfFlight.add(f);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfFlight;
    }

}
