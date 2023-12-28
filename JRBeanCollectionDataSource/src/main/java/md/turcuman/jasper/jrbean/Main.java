package md.turcuman.jasper.jrbean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {

    static Connection connection = null;
    static Properties props = loadProperties();
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.info("SQLDriver not found");
        }
        try {
            connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password")
            );
        } catch (SQLException e) {
            logger.severe("Error while connecting to DB");
            e.printStackTrace();
        }

        try {
            List<Holiday> holidays = readDataFromXml();

            logger.info("Report generation initialized");

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(holidays);
            JasperReport jasperReport = JasperCompileManager.compileReport(props.getProperty("report.template.path"));
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_CONNECTION", connection);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, props.getProperty("report.output.path"));

            logger.info("Report generated successfully.");
        } catch (JRException e) {
            logger.severe("Error during report generation: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.severe("Error during closing DB");
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<Holiday> readDataFromXml() {
        List<Holiday> holidays = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            holidays = xmlMapper.readValue(new File(props.getProperty("xml.data.path")),
                    new TypeReference<List<Holiday>>() {});
        } catch (IOException e) {
            logger.severe("Error reading data from XML: " + e.getMessage());
        }
        return holidays;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        logger = Logger.getAnonymousLogger();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.severe("Input from application properties is NULL");
                return null;
            }
            props.load(input);
        } catch (IOException e) {
            logger.severe("Error reading application properties");
            e.printStackTrace();
        }
        return props;
    }
}
