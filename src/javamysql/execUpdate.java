package javamysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;


public class execUpdate<T> {
    private T t;
    private T pK2;
    private String type;
    private String pK2Type;

    public execUpdate(T t, String type) {
        this.t = t;
        this.type = type;
    }

    public execUpdate(T t, T pK2, String type, String pK2Type) {
        this.t = t;
        this.pK2 = pK2;
        this.type = type;
        this.pK2Type = pK2Type;
    }

    public void deleteRow(Connection con, String pkName, String table, String pk2Name) {
        try {
            String deleteSQL = "";

            if (pk2Name == null){
                deleteSQL = " Delete from " + table + " where " + pkName + " = ?";
            } else { deleteSQL = " Delete from " + table + " where " + pkName + " = ? and " + pk2Name + " = ?"; }
            PreparedStatement pStmt = con.prepareStatement(deleteSQL);
            switch (type) {
                case "String" -> {pStmt.setString(1, (String) t);}
                case "Int" -> {pStmt.setInt(1, (Integer) t);}
                default -> {System.out.println("uh oh spaghettio");}
            }
            if (pk2Name != null) {
                switch (pK2Type){
                    case "String" -> {pStmt.setString(2, (String) pK2);}
                    case "Int" -> {pStmt.setInt(2, (Integer) pK2);}
                    default -> {System.out.println("uh oh spaghettio");}
                }
            }
            pStmt.executeUpdate();
        }catch (Exception io) {
            System.out.println("error"+io);
        };
    }

    public void updateInfo(Connection con, String infoToUpdate, String pk, String pkName, String table, String pk2Name){
        try {
            // information needs to be from object created by the switch case
            String insertSQL = "";
            if (pk2Name == null){
                insertSQL = " Update " + table + " set " + infoToUpdate + " = ? where " + pkName + " = ?";
            } else {
                insertSQL = " Update " + table + " set " + infoToUpdate + " = ? where " + pkName + " = ? and "
                        + pk2Name + " = ?";
            }

            PreparedStatement pStmt = con.prepareStatement(insertSQL);

            switch (type) {
                case "String" -> {pStmt.setString(1, (String) t);}
                case "Int" -> {pStmt.setInt(1, (Integer) t);}
                case "Boolean" -> {pStmt.setBoolean(1, (Boolean) t);}
                case "Timestamp" -> {pStmt.setTimestamp(1, (Timestamp) t);}
                default -> {System.out.println("uh oh spaghettio");}
            }

            pStmt.setString(2, pk);

            if (pk2Name != null) {
                switch (pK2Type){
                    case "String" -> {pStmt.setString(3, (String) pK2);}
                    case "Int" -> {pStmt.setInt(3, (Integer) pK2);}
                    default -> {System.out.println("uh oh spaghettio");}
                }
            }

            pStmt.executeUpdate();
            pStmt.close();
        }catch (Exception io) {
            System.out.println("error"+io);
        }
    }
}
