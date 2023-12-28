package md.turcuman.jasper.jrresultset.Utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class ReportGenerator {

    public static void generateReport(Connection connection, Properties properties, Logger logger) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("Select * FROM Holidays")) {
            logger.info("Report generation initialized");
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);

            JasperReport jasperReport = JasperCompileManager.compileReport(
                    properties.getProperty("report.template.path")
            );
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_CONNECTION", connection);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, properties.getProperty("report.output.path"));

            logger.info("Report generated successfully.");
        } catch (SQLException | JRException e) {
            logger.severe("Error generating the report: " + e.getMessage());
        }
    }
}