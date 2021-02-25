package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import javamysql.*;

public class FlightNumber {
    private String airlineCode, aircraft;
    private int flightNumber, capacity;

    public FlightNumber(String airlineCode, int flightNumber, String aircraft, int capacity) {
        this.airlineCode = airlineCode;
        this.flightNumber = flightNumber;
        this.aircraft = aircraft;
        this.capacity = capacity;
    }

    public FlightNumber(){

    }

    public void insertFlightNumber(Connection con){
        String table = "flightnumber";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, airlineCode);
            pStmt.setInt(2, flightNumber);
            pStmt.setString(3, aircraft);
            pStmt.setInt(4, capacity);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setFnAc(Connection con){
        String airlineCode = createAirlineCode();
        int flightNumber = createFlightNumber();
        String checkDB = checkFlightNumber(con, airlineCode, flightNumber);
        if (checkDB.equals("success")){
            System.out.print("\n\tFlightNumber combination already present in the database.\n\tCurrent list of Flight Numbers:");
            ArrayList<FlightNumber> listOfFlightNumber = listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print(" -" + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setFnAc(con);
        } else {
            this.airlineCode = airlineCode;
            this.flightNumber = flightNumber;
        }
    }

    public void setAirlineCode(Connection con){
        String airlineCode = createAirlineCode();
        String check = checkFlightNumber(con, airlineCode, this.flightNumber);
        if (check.equals("success")){
            System.out.print("\n\tFlightNumber combination already present in the database.\n\tCurrent list of Flight Numbers: ");
            ArrayList<FlightNumber> listOfFlightNumber = listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print(" -" + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setAirlineCode(con);
        } else {
            this.airlineCode = airlineCode;
        }
    }

    public void setFlightNumber(Connection con){
        int flightNumber = createFlightNumber();
        String check = checkFlightNumber(con, this.airlineCode, flightNumber);
        if (check.equals("success")){
            System.out.print("\n\tFlightNumber combination already present in the database.\n\tCurrent list of Flight Numbers: ");
            ArrayList<FlightNumber> listOfFlightNumber = listFlightNumber(con);
            for(FlightNumber fN : listOfFlightNumber){
                System.out.print(" -" + fN.getAirlineCode() + fN.getFlightNumber());
            }
            System.out.println();
            setFlightNumber(con);
        } else {
            this.flightNumber = flightNumber;
        }
    }

    public void setAircraft(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter Aircraft model => ");
        String aircraft = userInput.nextLine();
        this.aircraft = aircraft;
    }

    public void setCapacity(){
        Scanner userInput = new Scanner(System.in);
        int capacity = -1;
        while (capacity < 0) {
            try {
                System.out.print("\tEnter a positive capacity => ");
                capacity = userInput.nextInt();
            } catch (InputMismatchException e){
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. ");

            }
        }
        this.capacity = capacity;
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public String createAirlineCode(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter airline code (2 chars only letters) => ");
        // Use next line to allow space mistake, remove spaces with .replace
        String airlineCode = userInput.nextLine().replace(" ", "").toUpperCase();
        // If length is not 2 or if has an integer
        while (airlineCode.length() != 2 || !isAlpha(airlineCode)){
            System.out.print("\tPlease input a 2 characters long airline code (only letters) => ");
            airlineCode = userInput.nextLine().replace(" ", "").toUpperCase();
        }
        return airlineCode;
    }

    public int createFlightNumber(){
        Scanner userInput = new Scanner(System.in);
        int flightNumber = -1;
        while (flightNumber < 0 || flightNumber > 999) {
            try {
                System.out.print("\tEnter a positive flight number (max 999) => ");
                flightNumber = userInput.nextInt();
            } catch (InputMismatchException e){
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. ");
            }
        }
        return flightNumber;
    }

    public ArrayList<FlightNumber> listFlightNumber(Connection con){
        ArrayList<FlightNumber> listOfFlightNumber = new ArrayList<>();
        String table = "flightnumber";
        try {
            String query = "SELECT * from " + table;
            PreparedStatement pStmt = con.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                FlightNumber fN = new FlightNumber(rs.getString(1), rs.getInt(2),
                        rs.getString(3), rs.getInt(4));
                listOfFlightNumber.add(fN);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfFlightNumber;
    }

    public String checkFlightNumber(Connection con, String aC, int fN){
        String table = "flightnumber";
        String check = "";
        try {
            String query = "SELECT * from " + table + " WHERE airlineCode = ? AND flightNumber = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, aC);
            pStmt.setInt(2, fN);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()) {
                check =  "success";
            } else {
                check = "failure";
            }
            rs.close();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
        return check;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public String getAircraft() {
        return aircraft;
    }

    public int getCapacity() {
        return capacity;
    }

    public void print(){
        System.out.print("\n\t1. AirlineCode = " + airlineCode + "\n\t2. FlightNumber = " + flightNumber +
                "\n\t3. Aircraft = " + aircraft + "\n\t4. Capacity = " + capacity +"\n\t5. Quit\n\t=> ");
    }
}
