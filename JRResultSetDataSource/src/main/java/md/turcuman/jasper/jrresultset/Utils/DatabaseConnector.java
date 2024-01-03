package md.turcuman.jasper.jrresultset.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnector {

    public static Connection getConnection(Properties properties, Logger logger) {
        try {
            return DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        } catch (SQLException e) {
            logger.severe("Error while connecting to DB: " + e.getMessage());
            return null;
        }
    }

}