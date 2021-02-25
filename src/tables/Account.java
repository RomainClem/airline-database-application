package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import javamysql.*;

public class Account {
    private String login, userId, password;
    private boolean admin;

    public Account(String login, String userId, String password, boolean admin) {
        this.login = login;
        this.userId = userId;
        this.password = password;
        this.admin = admin;
    }

    public Account(){

    }

    public void insertAccount(Connection con){
        String table = "account";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = "Insert into " + table + " values (?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, login);
            pStmt.setString(2, userId);
            pStmt.setString(3, password);
            pStmt.setBoolean(4, admin);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setLogin(Connection con){
        String s = "Enter login";
        String login = getNonEmptyInfo(s);
        while (checkLogin(con, login, 1).equals("success")){
            System.out.println("Login already exist, retry and input a different login.");
            login = getNonEmptyInfo(s);
        }
        this.login = login;
    }

    public void setPassword(){
        String s = "Enter password";
        this.password = getNonEmptyInfo(s);
    }

    public String getNonEmptyInfo(String s){
        Scanner userInput = new Scanner(System.in);
        System.out.print("\t "+ s +" => ");
        String info = userInput.next().replace(" ", "");
        while (info.isEmpty()){
            System.out.print("\t "+ s +" (min 1 character) => ");
            info = userInput.next().replace(" ", "");
        }
        return info;
    }

    public String checkLogin(Connection con, String l, int i){
        String table = "account";
        String check = "";
        try {
            String query = "SELECT * from " + table + " WHERE login = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, l);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()) {
                if (i == 0) {
                    this.login = rs.getString(1);
                    this.userId = rs.getString(2);
                    this.password = rs.getString(3);
                    this.admin = rs.getBoolean(4);
                }
                check = "success";
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

    public User getUser(Connection con){
        String table = "user";
        User getUser = null;
        try {
            String query = "SELECT * from " + table + " WHERE id = ?";
            PreparedStatement pStmt = con.prepareStatement(query);
            pStmt.setString(1, this.userId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                getUser = new User(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5));
            }
            rs.close();
            pStmt.close();
        } catch (Exception io){
            System.out.println("error"+io);
        }
        return getUser;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void print(){
        System.out.print("\n\t1. Login: " + login + "\n\t2. Password: " + password +
                "\n\t3. Quit\n\t=> ");
    }

}
