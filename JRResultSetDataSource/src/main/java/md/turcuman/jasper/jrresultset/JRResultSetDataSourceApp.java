package md.turcuman.jasper.jrresultset;

import md.turcuman.jasper.jrresultset.Utils.DatabaseConnector;
import md.turcuman.jasper.jrresultset.Utils.ReportGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class JRResultSetDataSourceApp {
    private static final Logger LOGGER = Logger.getLogger(JRResultSetDataSourceApp.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnector.getConnection(PROPERTIES, LOGGER)) {
            if (connection == null) {
                LOGGER.severe("No database connection available.");
                return;
            }
            ReportGenerator.generateReport(connection, PROPERTIES, LOGGER);
        } catch (SQLException e) {
            LOGGER.severe("Error closing the database connection: " + e.getMessage());
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = JRResultSetDataSourceApp.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                LOGGER.severe("Input from application properties is NULL");
                return null;
            }
            properties.load(input);
        } catch (IOException e) {
            LOGGER.severe("Error reading application properties" + e.getMessage());
        }
        return properties;
    }
}
