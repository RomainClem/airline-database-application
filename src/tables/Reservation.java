package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Objects;

public class Reservation {
    private String id, userId, paymentId, status;

    public Reservation(String id, String userId, String paymentId, String status) {
        this.id = id;
        this.userId = userId;
        this.paymentId = paymentId;
        this.status = status;
    }

    public Reservation() {
    }

    public void setID(){
        String reservationIdentifier = "res";
        Identifier idC = new Identifier();
        this.id = idC.createID(reservationIdentifier);
    }

    public void insertReservation(Connection con) {
        String table = "reservation";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, id);
            pStmt.setString(2, userId);
            pStmt.setString(3, paymentId);
            pStmt.setString(4, status);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void print(){
        System.out.println("\tID: " + id + ", Status: " + status);
    }

}
