package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class Payment {
    private String id, paymentMethod;
    private Timestamp timestamp;
    private boolean success;

    public Payment(String id, Timestamp timestamp, boolean success, String paymentMethod) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
        this.success = success;
    }

    public Payment(){

    }

    public void insertPayment(Connection con) {
        String table = "payment";
        try {
            // information needs to be from object created by the switch case
            String insertSQL = " Insert into " + table + " values (?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(insertSQL);
            pStmt.setString(1, id);
            pStmt.setTimestamp(2, timestamp);
            pStmt.setBoolean(3, success);
            pStmt.setString(4, paymentMethod);
            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }

    public void setID(){
        String paymentIdentifier = "pay";
        Identifier idC = new Identifier();
        this.id = idC.createID(paymentIdentifier);
    }

    public void setTimestamp(){
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String formattedDT = datetime.format(dtFormatter);
        this.timestamp = Timestamp.valueOf(formattedDT);
    }

    public void attemptPayment(){
        Scanner userInput = new Scanner(System.in);
        System.out.println("\tCurrent test card :\n\t- 4242424242424242 (payment succeeds)" +
                "\n\t- 4000000000000000 (payment declined)");
        System.out.print("\tPlease input card details => ");
        boolean success;
        String card = "";
        while (true) {
            // tables.User input to choose element of the menu
            card = userInput.next();
            if (card.equals("4242424242424242")){
                System.out.println("\tPayment successful!");
                success = true;
                break;
            } else if (card.equals("4000000000000000")) {
                System.out.println("\tPayment failed!");
                success = false;
                break;
            } else {
                System.out.print("Please use our test cards => ");
            }
        }
        this.success = success;
        this.paymentMethod = card;
    }

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return success == payment.success &&
                Objects.equals(id, payment.id) &&
                Objects.equals(paymentMethod, payment.paymentMethod) &&
                Objects.equals(timestamp, payment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentMethod, timestamp, success);
    }
}
