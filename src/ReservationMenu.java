import javamysql.execUpdate;
import tables.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ReservationMenu {
    private final User u;
    private final Connection con;
    private final String s;

    public ReservationMenu(User u, Connection con, String s) {
        this.u = u;
        this.con = con;
        this.s = s;
    }

    public void menu(){
        System.out.println("\n\n"+ s +" Reservation(s) " + s);
        ArrayList<Reservation> listOfReservation = listReservation();
        int choice = chooseReservationFromList(listOfReservation);
        int listSize = listOfReservation.size() + 1;

        if(choice<listSize) {
            Reservation toUpdate = listOfReservation.get(choice - 1);
            if (toUpdate.getStatus().equals("failed") || toUpdate.getStatus().equals("pending")) {
                System.out.print("1. Complete payment\n2. Cancel reservation\n3. Quit\n=> ");
                int updateChoice = getIntChoice(0, 4);
                switch (updateChoice) {
                    case 1 -> {completePayment(toUpdate);}
                    case 2 -> {cancelReservation(toUpdate);}
                    case 3 -> {menu();}
                    default -> {
                        System.out.print("Please input an integer between 1 and 3.");
                        menu();
                    }
                }
            } else if (toUpdate.getStatus().equals("complete")) {
                System.out.print("1. Cancel reservation\n2. Quit\n=> ");
                int updateChoice = getIntChoice(0, 3);
                switch (updateChoice) {
                    case 1 -> {cancelReservation(toUpdate);}
                    case 2 -> {menu();}
                    default -> {
                        System.out.print("Please input an integer between 1 and 2.");
                        menu();
                    }
                }
            } else {
                toUpdate.print();
                System.out.print("Press enter to exit => ");
                Scanner userInput = new Scanner(System.in);
                userInput.nextLine();
                menu();
                }
            }else if (choice == listSize){
                Menu menu = new Menu(con,s);
                menu.customer(u);
            } else {
                System.out.println("Please input a integer between 1 and " + listSize +'.');
                menu();
    }
    }

    public int getIntChoice(int min, int max){
        Scanner userInput = new Scanner(System.in);
        int choice;
        while (true) {
            try {
                // tables.User input to choose element of the menu
                choice = userInput.nextInt();
                if (choice > min && choice < max) {
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
        return choice;
    }

    public void cancelReservation(Reservation r){
        Scanner userInput = new Scanner(System.in);
        ArrayList<TicketBoardingPass> listOfTickets = listOfTickets(r.getId());
        TicketBoardingPass ticket1 = listOfTickets.get(0);
        TicketBoardingPass ticket2 = listOfTickets.get(1);
        while (true){
            System.out.print("\tAre you sure you want to cancel this reservation y/n ?\n\t=> ");
            String deleteChoice = userInput.nextLine();
            if (deleteChoice.equals("y")){
                break;
            } else if (deleteChoice.equals("n")){
                menu();
            } else {
                System.out.println("\tPlease input y or n.\n");
            }
        }
        deleteTicket(ticket1);
        deleteTicket(ticket2);
        r.setStatus("cancelled");
        updateReservation(r);
        menu();
    }

    public void updateReservation(Reservation r){
        String currentPk = r.getId();
        String pkName = "ID";
        String table = "reservation";
        execUpdate<String> ir = new execUpdate<>(r.getStatus(), "String");
        ir.updateInfo(con, "status",currentPk, pkName, table, null);
    }

    public void deleteTicket(TicketBoardingPass ticket){
        execUpdate eu = new execUpdate<>(ticket.getReservationId(),
                ticket.getFlightId(), "String", "String");
        eu.deleteRow(con, "reservationID", "ticketboardingpass","flightID");
    }

    public void completePayment(Reservation r){
        ArrayList<TicketBoardingPass> listOfTickets = listOfTickets(r.getId());
        TicketBoardingPass ticket1 = listOfTickets.get(0);
        TicketBoardingPass ticket2 = listOfTickets.get(1);
        Flight flight1 = getFlight(ticket1.getFlightId());
        Flight flight2 = getFlight(ticket2.getFlightId());
        int price = flight1.getFlightCost() + flight2.getFlightCost();

        System.out.println("Total price for " + flight1.getDepAirport() + "->" + flight1.getDestAirport()+ ", " +
                flight1.getDestAirport() + "->" + flight1.getDepAirport()+ " = " + price + "$.");

        String currentPk = r.getId();
        String pkName = "ID";
        String table = "reservation";
        if (r.getPaymentId() == null){
            BookFlight bF = new BookFlight(u, con, s);
            Payment pay = bF.createPayment();
            r.setPaymentId(pay.getId());
            execUpdate<String> ir = new execUpdate<>(r.getPaymentId(), "String");
            ir.updateInfo(con, "paymentID",currentPk, pkName, table, null);
            setSuccess(r, pay);

        } else {
            Payment pay = getPayment(r.getPaymentId());
            pay.attemptPayment();
            setSuccess(r, pay);
        }
        menu();
    }

    public void setSuccess(Reservation r, Payment pay){
        String currentPk = r.getId();
        String pkName = "ID";
        String table = "reservation";
        if (pay.isSuccess()){
            r.setStatus("complete");
        } else {
            r.setStatus("failed");
        }
        execUpdate<String> eU = new execUpdate<>(r.getStatus(), "String");
        eU.updateInfo(con, "status",currentPk, pkName, table, null);
    }

    public Payment getPayment(String id){
        String table = "payment";
        Payment pay = null;
        try {
            String query = "SELECT * from " + table + " where ID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, id);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()){
                pay = new Payment(rs.getString(1), rs.getTimestamp(2), rs.getBoolean(3),
                        rs.getString(4));
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return pay;
    }

    public int chooseReservationFromList(ArrayList<Reservation> listOfReservation){
        Scanner userInput = new Scanner(System.in);
        int i = 1;
        for(Reservation r : listOfReservation){
            if (r.getStatus().equals("cancelled")) {
                System.out.println(""+ (i++) + ". ID: " + r.getId() + ", Status: " + r.getStatus());
            } else{
                TicketBoardingPass tbd = getTicket(r.getId());
                Flight f = getFlight(tbd.getFlightId());
                System.out.println(""+ (i++) + ". ID: " + r.getId() + ", Origin: " + f.getDepAirport() +
                        ", Destination: "+ f.getDestAirport()+ ", Status: "+ r.getStatus());
            }

        }
        System.out.print(""+ i + ". Quit");
        int choice;
        while(true){
            try {
                System.out.print("\n=> ");
                // tables.User input to choose element of the menu
                choice = userInput.nextInt();
                break;
                // Catching if a user input something else than an integer
            } catch (InputMismatchException e) {
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. ");
            }
        }
        return choice;
    }

    public Flight getFlight(String id){
        String table = "flight";
        Flight flight = null;
        try {
            String query = "SELECT * from " + table + " where ID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, id);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()){
                flight = new Flight(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getTimestamp(6),
                        rs.getTimestamp(7), rs.getInt(8));
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return flight;
    }

    public ArrayList<TicketBoardingPass> listOfTickets(String id){
        ArrayList<TicketBoardingPass> listOfTickets = new ArrayList<>();
        String table = "ticketboardingpass";

        try {
            String query = "SELECT * from " + table + " where reservationID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, id);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                TicketBoardingPass t = new TicketBoardingPass(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getBoolean(4 ), rs.getString(5));
                listOfTickets.add(t);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfTickets;
    }

    public TicketBoardingPass getTicket(String id){
        String table = "ticketboardingpass";
        TicketBoardingPass ticket = null;
        try {
            String query = "SELECT * from " + table + " where reservationID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, id);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()){
                ticket = new TicketBoardingPass(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getBoolean(4 ), rs.getString(5));
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return ticket;
    }

    public ArrayList<Reservation> listReservation(){
        ArrayList<Reservation> listOfReservation = new ArrayList<>();
        String table = "reservation";
        try {
            String query = "SELECT * from " + table + " where userID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, u.getId());
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                Reservation r = new Reservation(rs.getString(1), rs.getString(2),
                        rs.getString(3),rs.getString(4 ));
                listOfReservation.add(r);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfReservation;
    }
}
