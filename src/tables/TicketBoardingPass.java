package tables;

import javamysql.execUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class TicketBoardingPass {
    private String reservationId, flightId, seat, baggage;
    private boolean priority;

    public TicketBoardingPass(String reservationId, String flightId, String seat, boolean priority,  String baggage) {
        this.reservationId = reservationId;
        this.flightId = flightId;
        this.seat = seat;
        this.baggage = baggage;
        this.priority = priority;
    }

    public TicketBoardingPass(String flightId) {
        this.flightId = flightId;
    }

    public void insertTicketBoardingPass(Connection con) {
        String table = "ticketboardingpass";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, reservationId);
            pStmt.setString(2, flightId);
            pStmt.setString(3, seat);
            pStmt.setBoolean(4, priority);
            pStmt.setString(5, baggage);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setSeat(){
        System.out.println("Seat Preference:");
        Scanner userInput = new Scanner(System.in);
        int choice;
        System.out.print("\t1. Aisle\n\t2. Center\n\t3. Window\n\t=> ");
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
        switch (choice){
            case 1 -> {this.seat = "Aisle";}
            case 2 -> {this.seat = "Center";}
            case 3 -> {this.seat = "Window";}
        }
    }

    public void setPriority(){
        String choice = "";
        do {
            Scanner userInput = new Scanner(System.in);
            System.out.print("\tDo you want to opt in for priority y/n ?\n\t=> ");
            choice = userInput.nextLine();

            if (choice.equals("y")){
                this.priority = true;
            } else if (choice.equals("n")){
                this.priority = false;
            } else {
                System.out.println("\tPlease input y or n.\n");
            }
        } while (!choice.equals("n") && !choice.equals("y"));

    }

    public void setBaggage(){
        System.out.println("Baggage preference:");
        Scanner userInput = new Scanner(System.in);
        int choice;
        System.out.print("\t1. Cabin Baggage\n\t2. Checked Baggage\n\t3. No Baggage\n\t=> ");
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
        switch (choice){
            case 1 -> {this.baggage = "Cabin";}
            case 2 -> {this.baggage = "Checked";}
            case 3 -> {this.baggage = "none";}
        }}

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getBaggage() {
        return baggage;
    }

    public void setBaggage(String baggage) {
        this.baggage = baggage;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketBoardingPass that = (TicketBoardingPass) o;
        return priority == that.priority &&
                Objects.equals(reservationId, that.reservationId) &&
                Objects.equals(flightId, that.flightId) &&
                Objects.equals(seat, that.seat) &&
                Objects.equals(baggage, that.baggage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, flightId, seat, baggage, priority);
    }

    @Override
    public String toString() {
        return "TicketBoardingPass{" +
                "reservationId='" + reservationId + '\'' +
                ", flightId='" + flightId + '\'' +
                ", seat='" + seat + '\'' +
                ", baggage='" + baggage + '\'' +
                ", priority=" + priority +
                '}';
    }

    public TicketBoardingPass(){

    }

}
