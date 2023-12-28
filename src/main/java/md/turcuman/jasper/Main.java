package md.turcuman.jasper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Compile the JRXML template file
            JasperReport jasperReport = JasperCompileManager.compileReport("C:\\Users\\crme126\\JaspersoftWorkspace\\MyReports\\TestReport.jrxml");

            // Create a collection of data (maps) to fill the report
            Collection<Map<String, ?>> dataSource = new ArrayList<>();

            // Populate the collection with data. Each map corresponds to one row in the report.
            Map<String, Object> row1 = new HashMap<>();
            row1.put("country", "Italia");
            row1.put("name", "Capodanno");
            row1.put("date", new Date()); // Use appropriate date format
            dataSource.add(row1);

            // Add more rows as needed...

            // Create the JRMapCollectionDataSource
            JRMapCollectionDataSource jrDataSource = new JRMapCollectionDataSource(dataSource);

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), jrDataSource);

            // Export the report to a PDF file
            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\");

            // Or display the report in a viewer
            // JasperViewer.viewReport(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
