import javamysql.execUpdate;
import tables.Airport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AirportMenu {
    private final Connection con;
    private final String s;

    public AirportMenu(Connection con, String s) {
        this.con = con;
        this.s = s;
    }

    public void airportMenu(){
        System.out.println("\n\n"+ s +" Airport " + s);
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
            case 1 -> {createAirport();}
            case 2 -> {updateAirport();}
            case 3 -> {deleteAirport();}
            case 4 -> {
                Menu menu = new Menu(con,s);
                menu.admin();}
            default -> {
                System.out.print("Please input an integer between 1 and 4.");
                airportMenu();
            }
        }
    }

    public void createAirport(){
        Airport newAirport = new Airport();
        newAirport.setIata(con);
        newAirport.setCountryIso();
        newAirport.setName();
        newAirport.insertAirport(con);
        airportMenu();
    }

    public void updateAirport(){
        Airport apr = new Airport();
        ArrayList<Airport> listOfAirport = apr.listAirport(con);
        int choice = chooseAirportFromList(listOfAirport);
        int listSize = listOfAirport.size() + 1;
        Scanner userInput = new Scanner(System.in);
        int updateChoice;

        if(choice<listSize){
            do {
                Airport toUpdate = listOfAirport.get(choice - 1);
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
                String currentPk = toUpdate.getIata();
                String pkName = "IATA";
                String table = "airport";
                switch (updateChoice) {
                    case 1 -> {
                        toUpdate.setIata(con);
                        execUpdate<String> ir = new execUpdate<>(toUpdate.getIata(), "String");
                        ir.updateInfo(con, "IATA",currentPk, pkName, table, null);
                    }
                    case 2 -> {
                        toUpdate.setName();
                        execUpdate<String> ir = new execUpdate<>(toUpdate.getName(), "String");
                        ir.updateInfo(con, "name", currentPk, pkName,table, null);
                    }
                    case 3 -> {
                        toUpdate.setCountryIso();
                        execUpdate<String> ir = new execUpdate<>(toUpdate.getCountryIso(), "String");
                        ir.updateInfo(con, "countryISO", currentPk, pkName,table, null);
                    }
                    case 4 -> {
                        updateAirport();
                    }
                    default -> {
                        System.out.println("Please input a integer between 1 and 4.");
                    }
                }
            }while (updateChoice != 4);
        } else if (choice == listSize){
            airportMenu();
        } else {
            System.out.println("Please input a integer between 1 and " + listSize +'.');
            updateAirport();
        }
    }

    public void deleteAirport(){
        Airport apr = new Airport();
        ArrayList<Airport> listOfAirport = apr.listAirport(con);
        int choice = chooseAirportFromList(listOfAirport);
        int listSize = listOfAirport.size() + 1;
        String deleteChoice = "";

        if(choice<listSize){
            do {
                Scanner userInput = new Scanner(System.in);
                Airport toDelete = listOfAirport.get(choice - 1);
                System.out.print("\tAre you sure you want to delete this Airport y/n ?\n\t=> ");
                deleteChoice = userInput.nextLine();

                if (deleteChoice.equals("y")){
                    execUpdate<String> eu = new execUpdate<>(toDelete.getIata(), "String");
                    eu.deleteRow(con, "IATA", "airport", null);
                    deleteAirport();
                } else if (deleteChoice.equals("n")){
                    deleteAirport();
                } else {
                    System.out.println("\tPlease input y or n.\n");
                }
            } while (!deleteChoice.equals("n") && !deleteChoice.equals("y"));
        } else if (choice == listSize){
            airportMenu();
        } else {
            System.out.println("\tPlease input a integer between 1 and " + listSize +".\n");
            deleteAirport();
        }
    }

    public int chooseAirportFromList(ArrayList<Airport> listOfAirport){
        Scanner userInput = new Scanner(System.in);
        int i = 1;
        for(Airport a : listOfAirport){
            System.out.println("\t"+ (i++) + ". " + a.getIata());
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
