import tables.Flight;
import tables.FlightNumber;
import tables.User;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private final Connection con;
    private final String s;

    public Menu(Connection con, String s) {
        this.con = con;
        this.s = s;
    }

    // Customer menu if admin is false
    public void customer(User u) {
        System.out.println("\n\n"+ s +" Menu " + s);
        Scanner userInput = new Scanner(System.in);
        System.out.print("1. Book a flight\n2. Reservation(s)\n3. Update personal details\n4. Quit\n=> ");
        int choice = 0;

        try {
            // tables.User input to choose element of the menu
            choice = userInput.nextInt();

            // Catching if a user input something else than an integer
        } catch (InputMismatchException e) {
            // Skip the last input
            userInput.next();
            System.out.print("That’s not an integer. ");
        }

        // sub menus to create Flight, update reservation and update personal information
        switch (choice){
            case 1 -> {BookFlight bF = new BookFlight(u, con, s); bF.booking();}
            case 2 -> {ReservationMenu rM = new ReservationMenu(u, con, s); rM.menu();}
            case 3 -> {UserMenu uM = new UserMenu(u, con, s); uM.menu();}
            case 4 -> {
                System.out.println("Logging out.");
                LoginRegister loginIn = new LoginRegister(con, s);
                loginIn.loginRegister();
            }
            default -> {
                System.out.print("Please input an integer between x and y.");
                customer(u);
            }
        }
    }

    // Admin menu if admin is true
    public void admin(){
        System.out.println("\n\n"+ s +" Admin board " + s);
        Scanner userInput = new Scanner(System.in);
        System.out.print("1. Airport\n2. Flight Number\n3. Flight\n4. Quit\n=> ");
        int choiceTable = 0;

        try {
            // tables.User input to choose element of the menu
            choiceTable = userInput.nextInt();
            // Catching if a user input something else than an integer
        } catch (InputMismatchException e) {
            // Skip the last input
            userInput.next();
            System.out.print("That’s not an integer. ");
        }

        // sub menus to insert, update and delete from the database
        switch (choiceTable){
            case 1 -> { AirportMenu aM = new AirportMenu(con, s); aM.airportMenu(); }
            case 2 -> { FlightNumberMenu fnM = new FlightNumberMenu(con, s); fnM.flightNumberMenu(); }
            case 3 -> { FlightMenu fM = new FlightMenu(con, s); fM.flightMenu();}
            case 4 -> {
                System.out.println("Logging out.");
                LoginRegister loginIn = new LoginRegister(con, s);
                loginIn.loginRegister();
            }
            default -> {
                System.out.print("Please input an integer between 1 and 4.");
                admin();
            }
        }
    }



}
