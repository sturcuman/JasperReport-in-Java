package md.turcuman.jasper.jrmap;

import md.turcuman.jasper.jrmap.Utils.XmlDataReader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class JRMapCollectionDataSourceApp {
    private static final Logger LOGGER = Logger.getLogger(JRMapCollectionDataSourceApp.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    public static void main(String[] args) {

        try {
            List<Map<String, ?>> dataList = XmlDataReader.getDataFromXml(Objects.requireNonNull(PROPERTIES), LOGGER);

            LOGGER.info("Report generation initialized");
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    PROPERTIES.getProperty("report.template.path")
                    );
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    null,
                    new JRMapCollectionDataSource(dataList)
            );
            JasperExportManager.exportReportToPdfFile(jasperPrint, PROPERTIES.getProperty("report.output.path"));
            JasperViewer.viewReport(jasperPrint, false);
            LOGGER.info("Report generation complete");
        } catch(JRException e) {
            LOGGER.severe("Error during report generation: " + e.getMessage());
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

