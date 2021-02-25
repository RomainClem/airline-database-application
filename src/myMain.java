import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class myMain {

    public static void romainApplication(){

        try {
            String spacing = "~~~~~~~~~~";
            String url = "jdbc:mysql://localhost:3306/r00193006_ryanair";
            String userName = "root";
            String password = "root";
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connection to the database
            Connection con = DriverManager.getConnection(url, userName , password );
            LoginRegister loginIn = new LoginRegister(con, spacing);

            // Launching the Login / Register menu
            loginIn.loginRegister();
            con.close();
        }
        catch(ClassNotFoundException | SQLException ex) {
            System.out.println("Error: unable to load driver class!");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        romainApplication();
    }
}
