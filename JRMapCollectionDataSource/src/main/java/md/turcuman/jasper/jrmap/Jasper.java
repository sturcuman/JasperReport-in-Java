package md.turcuman.jasper.jrmap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

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

public class Jasper {
    static Connection connection = null;
    static Properties props = loadProperties();
    static Logger logger = Logger.getLogger(Jasper.class.getName());

    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.info("SQLDriver not found");
        }
        try {
            connection = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
        } catch (SQLException e) {
            logger.severe("Error while connecting to DB");
            e.printStackTrace();
        }

        try {
            List<Map<String, ?>> dataList = getDataFromXml();

            logger.info("Report generation initialized");

            JasperReport jasperReport = JasperCompileManager.compileReport(props.getProperty("report.template.path"));
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("MAP_DATA_SOURCE", new JRMapCollectionDataSource(dataList));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperExportManager.exportReportToPdfFile(jasperPrint, props.getProperty("report.output.path"));

            logger.info("Report generation complete");
        } catch (JRException e) {
            logger.severe("Error during report generation");
            e.printStackTrace();
        }
    }

    private static List<Map<String, ?>> getDataFromXml() {
        List<Map<String, ?>> dataList = new ArrayList<>();

        try {
            XmlMapper xmlMapper = new XmlMapper();
            List<Map<String, Object>> dataMaps = xmlMapper.readValue(
                    new File(props.getProperty("xml.data.path")),
                    new TypeReference<List<Map<String, Object>>>() {});

            dataList.addAll(dataMaps);

        } catch (IOException e) {
            logger.severe("Error reading data from xml file!");
            e.printStackTrace();
        }

        return dataList;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        logger = Logger.getAnonymousLogger();
        try (InputStream input = Jasper.class.getClassLoader().getResourceAsStream("application.properties")) {
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

