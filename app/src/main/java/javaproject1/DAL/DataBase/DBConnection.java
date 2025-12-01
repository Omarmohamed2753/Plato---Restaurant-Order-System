package javaproject1.DAL.DataBase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String PROPS_FILE = "/config.properties";    
    private static String url;
    private static String user;
    private static String pass;
    static {
        try (InputStream in = DBConnection.class.getResourceAsStream(PROPS_FILE)) {
            Properties props = new Properties();
            if (in == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            props.load(in);
            url = props.getProperty("db.url").trim();
            user = props.getProperty("db.user").trim();
            pass = props.getProperty("db.password").trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
