package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import javamysql.*;

public class Flight {
    private String id, depAirport, destAirport, airlineCode;
    private int flightNumber, flightCost;
    private Timestamp departureDateTime, arrivalDateTime;

    public Flight(String id, String depAirport, String destAirport, String airlineCode, int flightNumber,
                  Timestamp departureDateTime, Timestamp arrivalDateTime, int flightCost) {
        this.id = id;
        this.depAirport = depAirport;
        this.destAirport = destAirport;
        this.airlineCode = airlineCode;
        this.flightNumber = flightNumber;
        this.flightCost = flightCost;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    public Flight(){
    }

    public void insertFlight(Connection con) {
        String table = "flight";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, id);
            pStmt.setString(2, depAirport);
            pStmt.setString(3, destAirport);
            pStmt.setString(4, airlineCode);
            pStmt.setInt(5, flightNumber);
            pStmt.setTimestamp(6,departureDateTime);
            pStmt.setTimestamp(7, arrivalDateTime);
            pStmt.setInt(8, flightCost);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setID(){
        String flightIdentifier = "fli";
        Identifier idC = new Identifier();
        this.id = idC.createID(flightIdentifier);
    }

    public void setDepAirport(Connection con) {
        String s = "\tEnter departure Airport IATA (3 chars only letters) => ";
        Airport createA = new Airport();
        String iata = createA.createIata(s);
        String check = createA.checkIata(con, iata);
        if (check.equals("success")) {
            this.depAirport = iata;
        } else {
            System.out.print("\n\tIata not present in the database.\n\tCurrent list of Airport(s) IATA: ");
            ArrayList<Airport> listOfAirport = createA.listAirport(con);
            for (Airport a : listOfAirport) {
                System.out.print(" -" + a.getIata());
            }
            System.out.println();
            setDepAirport(con);
        }
    }

    public void setDestAirport(Connection con) {
        String s = "\tEnter destination Airport IATA (3 chars only letters) => ";
        Airport createA = new Airport();
        String iata = createA.createIata(s);
        String check = createA.checkIata(con, iata);
        if (check.equals("success")) {
            this.destAirport = iata;
        } else {
            System.out.print("\n\tIata not present in the database.\n\tCurrent list of Airport(s) IATA: ");
            ArrayList<Airport> listOfAirport = createA.listAirport(con);
            for (Airport a : listOfAirport) {
                System.out.print(" -" + a.getIata());
            }
            System.out.println();
            setDestAirport(con);
        }
    }

    public void setFnAc(Connection con){
        FlightNumber createAC = new FlightNumber();
        String aC = createAC.createAirlineCode();
        int flightN = createAC.createFlightNumber();
        String check = createAC.checkFlightNumber(con, aC, flightN);
        if (check.equals("success")) {
            this.airlineCode = aC;
            this.flightNumber = flightN;
        } else {
            System.out.print("\n\tFlightNumber combination not present in the database." +
                    "\n\tCurrent list of Flight Numbers: ");
            ArrayList<FlightNumber> listOfFlightNumber = createAC.listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print("- " + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setFnAc(con);
        }
    }

    public void setAirlineCode(Connection con){
        FlightNumber createAC = new FlightNumber();
        String aC = createAC.createAirlineCode();
        String check = createAC.checkFlightNumber(con, aC, this.flightNumber);
        if (check.equals("success")) {
            this.airlineCode = aC;
        } else {
            System.out.print("\n\tFlightNumber combination not present in the database." +
                    "\n\tCurrent list of Flight Numbers: ");
            ArrayList<FlightNumber> listOfFlightNumber = createAC.listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print("- " + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setAirlineCode(con);
        }
    }

    public void setFlightNumber(Connection con){
        FlightNumber createAC = new FlightNumber();
        int flightN = createAC.createFlightNumber();
        String check = createAC.checkFlightNumber(con, this.airlineCode, flightN);
        if (check.equals("success")) {
            this.flightNumber = flightN;
        } else {
            System.out.print("\n\tFlightNumber combination not present in the database." +
                    "\n\tCurrent list of Flight Numbers: ");
            ArrayList<FlightNumber> listOfFlightNumber = createAC.listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print("- " + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setFlightNumber(con);
        }
    }

    public void setDepartureDateTime(){
        Scanner userInput = new Scanner(System.in);
        try {
            LocalDateTime datetime7days = LocalDateTime.now().plusDays(7);
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String formattedDT = datetime7days.format(dtFormatter);
            System.out.print("\tEnter a timestamp for the departure (yyyy-MM-dd hh:mm:ss)" +
                    "\n\te.g. current time in 7 days " + formattedDT + ".\n\t=> ");
            String inputTimeStamp = userInput.nextLine();
            this.departureDateTime = Timestamp.valueOf(inputTimeStamp);
        } catch(Exception e) {
            System.out.println("There was an error in your timestamp. ");
        }

    }

    public void setArrivalDateTime(){
        Scanner userInput = new Scanner(System.in);
        try {
            LocalDateTime datetime7days = LocalDateTime.now().plusDays(30).plusHours(2);
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String formattedDT = datetime7days.format(dtFormatter);
            System.out.print("\tEnter a timestamp for the arrival (yyyy-MM-dd hh:mm:ss)" +
                    "\n\te.g. current time in 7 days plus 2 hours " + formattedDT + ".\n\t=> ");
            String inputTimeStamp = userInput.nextLine();
            this.arrivalDateTime = Timestamp.valueOf(inputTimeStamp);
        } catch(Exception e) {
            System.out.println("There was an error in your timestamp. ");
        }
    }

    public void setFlightCost(){
        Scanner userInput = new Scanner(System.in);
        int price = -1;
        while (price < 0) {
            try {
                System.out.print("\tEnter a positive flight cost => ");
                price = userInput.nextInt();
            } catch (InputMismatchException e){
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. ");

            }
        }
        this.flightCost = price;
    }

    public String getDepAirport() {
        return depAirport;
    }

    public String getDestAirport() {
        return destAirport;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public int getFlightCost() {
        return flightCost;
    }

    public Timestamp getDepartureDateTime() {
        return departureDateTime;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = id;
    }

    public void print(){
        System.out.print("\n\t1. ID = " + id + "\n\t2. DepAirport = " + depAirport + "\n\t3. DestAirport = " +
                destAirport + "\n\t4. AirlineCode = " + airlineCode + "\n\t5. FlightNumber = " + flightNumber +
                "\n\t6. FlightCost = " + flightCost + "\n\t7. DepartureDateTime = " + departureDateTime +
                "\n\t8. ArrivalDatetime = " + arrivalDateTime + "\n\t9. Quit\n\t=> ");
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", depAirport='" + depAirport + '\'' +
                ", destAirport='" + destAirport + '\'' +
                ", airlineCode='" + airlineCode + '\'' +
                ", flightNumber=" + flightNumber +
                ", flightCost=" + flightCost +
                ", departureDateTime=" + departureDateTime +
                ", arrivalDateTime=" + arrivalDateTime +
                '}';
    }

}
