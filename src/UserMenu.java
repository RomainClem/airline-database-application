import javamysql.execUpdate;
import tables.Account;
import tables.Flight;
import tables.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserMenu {
    private final User u;
    private final Connection con;
    private final String s;

    public UserMenu(User u, Connection con, String s) {
        this.u = u;
        this.con = con;
        this.s = s;
    }

    public void menu(){
        System.out.println("\n\n"+ s +" Profile " + s);
        System.out.print("1. Update personal details\n2. Update login/password\n3. Close account\n4. Quit\n=> ");
        int choice = getIntChoice(0, 5);
        switch (choice){
            case 1 -> {updatePersonalInformation();}
            case 2 -> {updateLoginPassword();}
            case 3 -> {deleteAccount();}
            case 4 -> {
                Menu menu = new Menu(con,s);
                menu.customer(u);
            }
            default -> {
                System.out.print("Please input an integer between 1 and 3.");
                menu();
            }
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

    public void deleteAccount(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tAre you sure you want to delete this Account y/n ?\n\t=> ");
        String deleteChoice = userInput.nextLine();
        if (deleteChoice.equals("y")){
            Account userAccount = getAccount();
            execUpdate<String> eu = new execUpdate<>(userAccount.getLogin(), "String");
            eu.deleteRow(con, "login", "account", null);
            System.out.println("Logging out.");
            LoginRegister loginIn = new LoginRegister(con, s);
            loginIn.loginRegister();
        } else if (deleteChoice.equals("n")){
            menu();
        } else {
            System.out.println("\tPlease input y or n.\n");
            deleteAccount();
        }
    }

    public Account getAccount(){
        String table = "account";
        Account acct = null;
        try {
            String query = "SELECT * from " + table + " where userID = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, u.getId());
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()){
                acct = new Account(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getBoolean(4));
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return acct;
    }

    public void updateLoginPassword(){
        Account userAccount = getAccount();
        userAccount.print();
        int choice = getIntChoice(0, 4);
        String currentPk = userAccount.getLogin();
        String pkName = "login";
        String table = "account";
        switch (choice) {
            case 1 -> {
                userAccount.setLogin(con);
                execUpdate<String> eU = new execUpdate<>(userAccount.getLogin(), "String");
                eU.updateInfo(con, "login",currentPk, pkName, table, null);
                updateLoginPassword();
            }
            case 2 -> {
                userAccount.setPassword();
                execUpdate<String> eU = new execUpdate<>(userAccount.getPassword(), "String");
                eU.updateInfo(con, "password",currentPk, pkName, table, null);
                updateLoginPassword();
            }
            case 3 -> {
                menu();
            }
            default -> {
                System.out.print("Please input an integer between 1 and 3.");
                updatePersonalInformation();
            }
        }
    }

    public void updatePersonalInformation(){
        u.print();
        int choice = getIntChoice(0, 6);
        String currentPk = u.getId();
        String pkName = "ID";
        String table = "user";
        switch (choice) {
            case 1 -> {
                u.setFirstName();
                execUpdate<String> eU = new execUpdate<>(u.getFirstName(), "String");
                eU.updateInfo(con, "firstName",currentPk, pkName, table, null);
                updatePersonalInformation();
            }
            case 2 -> {
                u.setLastName();
                execUpdate<String> eU = new execUpdate<>(u.getLastName(), "String");
                eU.updateInfo(con, "lastName",currentPk, pkName, table, null);
                updatePersonalInformation();
            }
            case 3 -> {
                u.setEmail();
                execUpdate<String> eU = new execUpdate<>(u.getEmail(), "String");
                eU.updateInfo(con, "email",currentPk, pkName, table, null);
                updatePersonalInformation();
            }
            case 4 -> {
                u.setPhoneNumber();
                execUpdate<Integer> eU = new execUpdate<>(u.getPhoneNumber(), "Int");
                eU.updateInfo(con, "phoneNumber",currentPk, pkName, table, null);
                updatePersonalInformation();
            }
            case 5 -> {
                menu();
            }
            default -> {
                System.out.print("Please input an integer between 1 and 5.");
                updatePersonalInformation();
            }
        }
    }

}
