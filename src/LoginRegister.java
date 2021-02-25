import tables.Account;
import tables.User;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LoginRegister {
    private final Connection con;
    private final String s;

    public LoginRegister(Connection con, String s) {
        this.con = con;
        this.s = s;
    }

    public void loginRegister(){
        Scanner userInput = new Scanner(System.in);
        System.out.println("\n\n"+ s +" Romains' Ryanair Application " + s);
        System.out.print("1. Login\n2. Register\n3. Quit\n=> ");
        int choice;
        while (true) {
            try {
                // tables.User input to choose element of the menu
                choice = userInput.nextInt();
                if (choice > 0 && choice < 4) {
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

        // Login in to the application or Registering
        switch (choice){
            case 1 -> {login();}
            case 2 -> {register();}
            case 3 -> {System.out.println("Good bye!");}
            }
    }

    // Method to check login details
    public void login(){
        Account getInfo = new Account();
        String login = getInfo.getNonEmptyInfo("Login").toLowerCase(); // not case sensitive
        String check = getInfo.checkLogin(con, login, 0);
        if (check.equals("success")) {
            String password = getInfo.getNonEmptyInfo("Password");
            if (getInfo.getPassword().equals(password)){

                Menu menu = new Menu(con,s);
                if (getInfo.isAdmin()){menu.admin();}
                else {menu.customer(getInfo.getUser(con));};

            } else { System.out.println("Incorrect password.");loginRegister();}

        } else { System.out.println("Login doesn't exist.");loginRegister();}
    }

    // Method to create and user and account and login directly
    public void register(){
        Account newAccount = new Account();
        String login = newAccount.getNonEmptyInfo("Login").toLowerCase(); // not case sensitive
        String check = newAccount.checkLogin(con, login, 1);
        if (check.equals("failure")) {
            newAccount.setPassword();

            User newUser = new User();
            newUser.setID();
            newUser.setFirstName();
            newUser.setLastName();
            newUser.setEmail();
            newUser.setPhoneNumber();
            newUser.insertUser(con);

            newAccount.setLogin(login);
            newAccount.setUserId(newUser.getId());
            newAccount.insertAccount(con);

            Menu menu = new Menu(con,s);
            menu.customer(newUser);
        } else {
            System.out.println("Login already exist, retry and input a different login.");
            loginRegister();
        }
    }
}