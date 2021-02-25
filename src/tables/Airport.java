package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
import javamysql.*;

public class Airport {
    private String iata, name, countryIso;

    public Airport(){}

    public Airport(String iata, String name, String countryIso) {
        this.iata = iata;
        this.name = name;
        this.countryIso = countryIso;
    }

    public void insertAirport(Connection con) {
        String table = "airport";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, iata);
            pStmt.setString(2, name);
            pStmt.setString(3, countryIso);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setIata(Connection con) {
        String s = "\tEnter Airport IATA (3 chars only letters) => ";
        String iata = createIata(s);
        String check = checkIata(con, iata);
        if (check.equals("success")){
            System.out.print("\n\tIata already present in the database.\n\tCurrent list of Airport(s) IATA:");
            ArrayList<Airport> listOfAirport = listAirport(con);
            for (Airport a : listOfAirport) {
                System.out.print(" -" + a.getIata());
            }
            System.out.println();
            setIata(con);
        } else {
            this.iata = iata;
        };
    }

    public void setName() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter Airport name => ");
        this.name = userInput.nextLine();
    }

    public void setCountryIso() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\tEnter Country ISO (2 chars only letters) => ");
        // Use next line to allow space mistake, remove spaces with .replace
        String countryIso = userInput.nextLine().replace(" ", "").toUpperCase();
        // If length is not 2 or if has an integer
        while (countryIso.length() != 2 || !isAlpha(countryIso)){
            System.out.print("\tPlease input a 2 characters long Country ISO (only letters) => ");
            countryIso = userInput.nextLine().replace(" ", "").toUpperCase();
        }
        this.countryIso = countryIso;
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

    public String createIata(String s){
        Scanner userInput = new Scanner(System.in);
        System.out.print(s);
        // Use next line to allow space mistake, remove spaces with .replace
        String iata = userInput.nextLine().replace(" ", "").toUpperCase();
        // If length is not 3 or if it has an integer
        while (iata.length() != 3 || !isAlpha(iata)){
            System.out.print("\tPlease input a 3 characters long IATA (only letters) => ");
            iata = userInput.nextLine().replace(" ", "").toUpperCase();
        }
        return iata;
    }

    public String getIata() {
        return iata;
    }

    public String getName() {
        return name;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public ArrayList<Airport> listAirport(Connection con){
        ArrayList<Airport> listOfAirport = new ArrayList<>();
        String table = "airport";
        try {
            String query = "SELECT * from " + table;
            PreparedStatement pStmt = con.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()){
                Airport a = new Airport(rs.getString(1), rs.getString(2),
                        rs.getString(3));
                listOfAirport.add(a);
            }
            rs.close();
            pStmt.close();
        }
        catch (Exception io) {
            System.out.println("error"+io);
        }
        return listOfAirport;
    }

    public String checkIata(Connection con, String i){
        String table = "airport";
        String check = "";
        try {
            String query = "SELECT * from " + table + " WHERE IATA = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, i);
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

    @Override
    public String toString() {
        return "Airport{" +
                "iata='" + iata + '\'' +
                ", name='" + name + '\'' +
                ", countryIso='" + countryIso + '\'' +
                '}';
    }

    public void print(){
        System.out.print("\n\t1. Iata = " + iata + "\n\t2. Name = " + name + "\n\t3. Country ISO = " + countryIso +
                "\n\t4. Quit\n\t=> ");
    }
}
