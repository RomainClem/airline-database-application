import javamysql.execUpdate;
import tables.Airport;
import tables.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FlightMenu {
    private final Connection con;
    private final String s;

    public FlightMenu(Connection con, String s) {
        this.con = con;
        this.s = s;
    }

    public void flightMenu(){
        System.out.println("\n\n"+ s +" Flight " + s);
        Scanner userInput = new Scanner(System.in);
        System.out.print("1. Add\n2. Update\n3. Delete\n4. Quit\n=> ");
        int queryType = 0;
        try {
            // tables.User input to choose element of the menu
            queryType = userInput.nextInt();
            // Catching if a user input something else than an integer
        } catch (InputMismatchException e) {
            // Skip the last input
            userInput.next();
            System.out.print("That’s not an integer. ");
        }
        System.out.println();
        switch (queryType) {
            case 1 -> {createFlight();}
            case 2 -> {updateFlight();}
            case 3 -> {deleteFlight();}
            case 4 -> {
                Menu menu = new Menu(con,s);
                menu.admin();}
            default -> {
                System.out.print("Please input an integer between 1 and 4.");
                flightMenu();
            }
        }
    }

    public void createFlight(){
        Flight newFlight = new Flight();
        newFlight.setID();
        newFlight.setDepAirport(con);
        newFlight.setDestAirport(con);
        newFlight.setFnAc(con);
        newFlight.setDepartureDateTime();
        newFlight.setArrivalDateTime();
        newFlight.setFlightCost();
        newFlight.insertFlight(con);
        flightMenu();
    }

    public void updateFlight(){
        ArrayList<Flight> listOfFlight = listFlight();
        int choice = chooseFlightFromList(listOfFlight);
        int listSize = listOfFlight.size() + 1;
        Scanner userInput = new Scanner(System.in);
        int updateChoice;

        if(choice<listSize){
            do {
                Flight toUpdate = listOfFlight.get(choice - 1);
                toUpdate.print();

                while (true) {
                    try {
                        // tables.User input to choose element of the menu
                        updateChoice = userInput.nextInt();
                        break;
                        // Catching if a user input something else than an integer
                    } catch (InputMismatchException e) {
                        // Skip the last input
                        userInput.next();
                        System.out.print("That’s not an integer. => ");
                    }
                }
                String currentPk = toUpdate.getID();
                String pkName = "ID";
                String table = "flight";
                switch (updateChoice) {
                    case 1 -> {
                        toUpdate.setID();
                        execUpdate<String> eU = new execUpdate<>(toUpdate.getID(), "String");
                        eU.updateInfo(con, "ID",currentPk, pkName, table, null);
                    }
                    case 2 -> {
                        toUpdate.setDepAirport(con);
                        execUpdate<String> eU = new execUpdate<>(toUpdate.getDepAirport(), "String");
                        eU.updateInfo(con, "depAirport", currentPk, pkName,table, null);
                    }
                    case 3 -> {
                        toUpdate.setDestAirport(con);
                        execUpdate<String> eU = new execUpdate<>(toUpdate.getDestAirport(), "String");
                        eU.updateInfo(con, "destAirport", currentPk, pkName,table, null);
                    }
                    case 4 -> {
                        toUpdate.setAirlineCode(con);
                        execUpdate<String> eU = new execUpdate<>(toUpdate.getAirlineCode(), "String");
                        eU.updateInfo(con, "airlineCode", currentPk, pkName,table, null);
                    }
                    case 5 -> {
                        toUpdate.setFlightNumber(con);
                        execUpdate<Integer> eU = new execUpdate<>(toUpdate.getFlightNumber(), "Int");
                        eU.updateInfo(con, "flightNumber", currentPk, pkName,table, null);
                    }
                    case 6 -> {
                        toUpdate.setFlightCost();
                        execUpdate<Integer> eU = new execUpdate<>(toUpdate.getFlightCost(), "Int");
                        eU.updateInfo(con, "flightCost", currentPk, pkName,table, null);
                    }
                    case 7 -> {
                        toUpdate.setDepartureDateTime();
                        execUpdate<Timestamp> eU = new execUpdate<>(toUpdate.getDepartureDateTime(), "String");
                        eU.updateInfo(con, "departureDateTime", currentPk, pkName,table, null);
                    }
                    case 8 -> {
                        toUpdate.setArrivalDateTime();
                        execUpdate<Timestamp> eU = new execUpdate<>(toUpdate.getDepartureDateTime(), "String");
                        eU.updateInfo(con, "arrivalDateTime", currentPk, pkName,table, null);
                    }
                    case 9 -> {updateFlight();}
                    default -> {
                        System.out.println("Please input a integer between 1 and 4.");
                    }
                }
            }while (updateChoice != 9);
        } else if (choice == listSize){
            flightMenu();
        } else {
            System.out.println("Please input a integer between 1 and " + listSize +'.');
            updateFlight();
        }
    }

    public void deleteFlight(){
        ArrayList<Flight> listOfFlight = listFlight();
        int choice = chooseFlightFromList(listOfFlight);
        int listSize = listOfFlight.size() + 1;
        String deleteChoice = "";

        if(choice<listSize){
            do {
                Scanner userInput = new Scanner(System.in);
                Flight toDelete = listOfFlight.get(choice - 1);
                System.out.print("\tAre you sure you want to delete this Flight y/n ?\n\t=> ");
                deleteChoice = userInput.nextLine();

                if (deleteChoice.equals("y")){
                    execUpdate<String> eu = new execUpdate<>(toDelete.getID(), "String");
                    eu.deleteRow(con, "ID", "flight", null);
                    deleteFlight();
                } else if (deleteChoice.equals("n")){
                    deleteFlight();
                } else {
                    System.out.println("\tPlease input y or n.\n");
                }
            } while (!deleteChoice.equals("n") && !deleteChoice.equals("y"));
        } else if (choice == listSize){
            flightMenu();
        } else {
            System.out.println("\tPlease input a integer between 1 and " + listSize +".\n");
            deleteFlight();
        }
    }

    public int chooseFlightFromList(ArrayList<Flight> listOfFlight){
        Scanner userInput = new Scanner(System.in);
        int i = 1;
        for(Flight f : listOfFlight){
            System.out.println("\t"+ (i++) + ". " + f.getID() + ", " + f.getDepAirport() + ", " + f.getDestAirport() +
            ", " + f.getDepartureDateTime());
        }
        System.out.print("\t"+ i + ". Quit");
        int choice;

        while(true){
            try {
                System.out.print("\n\t=> ");
                // tables.User input to choose element of the menu
                choice = userInput.nextInt();
                break;
                // Catching if a user input something else than an integer
            } catch (InputMismatchException e) {
                // Skip the last input
                userInput.next();
                System.out.print("\tThat’s not an integer. ");
            }
        }
        return choice;
    }

    public ArrayList<Flight> listFlight(){
        ArrayList<Flight> listOfFlight = new ArrayList<>();
        String table = "flight";
        try {
            String query = "SELECT * from " + table;
            PreparedStatement pStmt = con.prepareStatement(query);
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
