package md.turcuman.jasper.jrmap;

import md.turcuman.jasper.jrmap.Utils.DatabaseConnector;
import md.turcuman.jasper.jrmap.Utils.XmlDataReader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class JRMapCollectionDataSourceApp {
    private static final Logger LOGGER = Logger.getLogger(JRMapCollectionDataSourceApp.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    public static void main(String[] args) {
        Connection connection = DatabaseConnector.getConnection(PROPERTIES, LOGGER);
        if (connection == null) {
            LOGGER.severe("No database connection available.");
            return;
        }

        try {
            List<Map<String, ?>> dataList = XmlDataReader.getDataFromXml(PROPERTIES, LOGGER);

            LOGGER.info("Report generation initialized");
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    PROPERTIES.getProperty("report.template.path")
                    );
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("MAP_DATA_SOURCE", new JRMapCollectionDataSource(dataList));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            JasperExportManager.exportReportToPdfFile(jasperPrint, PROPERTIES.getProperty("report.output.path"));
            LOGGER.info("Report generation complete");
        } catch (JRException e) {
            LOGGER.severe("Error during report generation: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.severe("Error closing the database connection: " + e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = JRMapCollectionDataSourceApp.class.getClassLoader()
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

