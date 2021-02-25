package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.InputMismatchException;
import java.util.Scanner;
import javamysql.*;

public class User {
    private String id, firstName, lastName, email;
    private int phoneNumber;

    public User(String id, String firstName, String lastName, String email, int phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }

    public void insertUser(Connection con){
        String table = "user";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, id);
            pStmt.setString(2, firstName);
            pStmt.setString(3, lastName);
            pStmt.setString(4, email);
            pStmt.setInt(5, phoneNumber);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setID(){
        String userIdentifier = "usr";
        Identifier idC = new Identifier();
        this.id = idC.createID(userIdentifier);
    }

    public void setFirstName() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter First name => ");
        this.firstName = userInput.nextLine();
    }

    public void setLastName() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter Last name => ");
        this.lastName = userInput.nextLine();
    }

    public void setEmail() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter email address => ");
        this.email = userInput.nextLine();
    }

    public void setPhoneNumber(){
        Scanner userInput = new Scanner(System.in);
        int phoneNumber = -1;
        while (phoneNumber < 0) {
            try {
                System.out.print("\tEnter your phone number => ");
                phoneNumber = userInput.nextInt();
            } catch (InputMismatchException e){
                // Skip the last input
                userInput.next();
                System.out.print("That’s not an integer. ");

            }
        }
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void print(){
        System.out.print("\n\t1. First Name: " + firstName + "\n\t2. Last Name: " + lastName + "\n\t3. email: " + email +
                "\n\t4. Phonenumber: " + phoneNumber + "\n\t5. Quit\n\t=> ");
    }
}
