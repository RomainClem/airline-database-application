import javamysql.execUpdate;
import tables.Flight;
import tables.FlightNumber;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FlightNumberMenu {
    private final Connection con;
    private final String s;

    public FlightNumberMenu(Connection con, String s) {
        this.con = con;
        this.s = s;
    }

    public void flightNumberMenu(){
        System.out.println("\n\n"+ s +" FlightNumber " + s);
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
            case 1 -> {createFlightNumber();}
            case 2 -> {updateFlightNumber();}
            case 3 -> {deleteFlightNumber();}
            case 4 -> {
                Menu menu = new Menu(con,s);
                menu.admin();}
            default -> {
                System.out.print("Please input an integer between 1 and 4.");
                flightNumberMenu();
            }
        }
    }

    public void createFlightNumber(){
        FlightNumber newFlightNumber = new FlightNumber();
        newFlightNumber.setFnAc(con);
        newFlightNumber.setAircraft();
        newFlightNumber.setCapacity();
        newFlightNumber.insertFlightNumber(con);
        flightNumberMenu();
    }

    public void updateFlightNumber(){
        FlightNumber fM = new FlightNumber();
        ArrayList<FlightNumber> listOfFlightNumber = fM.listFlightNumber(con);
        int choice = chooseFlightNumberFromList(listOfFlightNumber);
        int listSize = listOfFlightNumber.size() + 1;
        Scanner userInput = new Scanner(System.in);
        int updateChoice;

        if(choice<listSize){
            do {
                FlightNumber toUpdate = listOfFlightNumber.get(choice - 1);
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

                String currentPk = toUpdate.getAirlineCode();
                int currentPk2 = toUpdate.getFlightNumber();
                String pkName = "airlineCode";
                String pk2Name = "flightNumber";
                String table = "flightnumber";
                switch (updateChoice) {
                    case 1 -> {
                        toUpdate.setAirlineCode(con);
                        execUpdate ir = new execUpdate<>(toUpdate.getAirlineCode(),
                                currentPk2, "String", "Int");
                        ir.updateInfo(con, "airlineCode", currentPk, pkName, table, pk2Name);
                    }
                    case 2 -> {
                        toUpdate.setFlightNumber(con);
                        execUpdate ir = new execUpdate<>(toUpdate.getFlightNumber(),
                                currentPk2, "Int", "Int");
                        ir.updateInfo(con, "flightNumber", currentPk, pkName, table, pk2Name);
                    }
                    case 3 -> {
                        toUpdate.setAircraft();
                        execUpdate ir = new execUpdate<>(toUpdate.getAircraft(),
                                currentPk2, "String", "Int");
                        ir.updateInfo(con, "aircraft", currentPk, pkName, table, pk2Name);
                    }
                    case 4 -> {
                        toUpdate.setCapacity();
                        execUpdate ir = new execUpdate<>(toUpdate.getCapacity(),
                                currentPk2, "Int", "Int");
                        ir.updateInfo(con, "capacity", currentPk, pkName, table, pk2Name);
                    }
                    case 5 -> {updateFlightNumber();}
                    default -> {
                        System.out.println("Please input a integer between 1 and 4.");
                    }
                }
            }while (updateChoice != 5);
        } else if (choice == listSize){
            flightNumberMenu();
        } else {
            System.out.println("Please input a integer between 1 and " + listSize +'.');
            flightNumberMenu();
        }
    }

    public void deleteFlightNumber(){
        FlightNumber fM = new FlightNumber();
        ArrayList<FlightNumber> listOfFlightNumber = fM.listFlightNumber(con);
        int choice = chooseFlightNumberFromList(listOfFlightNumber);
        int listSize = listOfFlightNumber.size() + 1;
        String deleteChoice = "";

        if(choice<listSize){
            do {
                Scanner userInput = new Scanner(System.in);
                FlightNumber toDelete = listOfFlightNumber.get(choice - 1);
                System.out.print("\tAre you sure you want to delete this FlightNumber y/n ?\n\t=> ");
                deleteChoice = userInput.nextLine();

                if (deleteChoice.equals("y")){
                    execUpdate eu = new execUpdate<>(toDelete.getAirlineCode(),
                            toDelete.getFlightNumber(), "String", "Int");
                    eu.deleteRow(con, "airlineCode", "flightnumber",
                            "flightNumber");
                    deleteFlightNumber();
                } else if (deleteChoice.equals("n")){
                    deleteFlightNumber();
                } else {
                    System.out.println("\tPlease input y or n.\n");
                }
            } while (!deleteChoice.equals("n") && !deleteChoice.equals("y"));
        } else if (choice == listSize){
            flightNumberMenu();
        } else {
            System.out.println("\tPlease input a integer between 1 and " + listSize +".\n");
            deleteFlightNumber();
        }
    }

    public int chooseFlightNumberFromList(ArrayList<FlightNumber> listOfFlightNumber){
        Scanner userInput = new Scanner(System.in);
        int i = 1;
        for(FlightNumber fN : listOfFlightNumber){
            System.out.println("\t"+ (i++) + ". " + fN.getAirlineCode() + fN.getFlightNumber());
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



}
