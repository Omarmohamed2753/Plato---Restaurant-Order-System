package javaproject1;
import java.sql.Connection;
import java.sql.SQLException;
import javaproject1.DAL.DataBase.DBConnection;
public class Main {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Connected successfully!");
        }
    }
}