package md.turcuman.dynamicreport.crosstab.Utils;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabMeasureBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabRowGroupBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;


public class ReportBuilder {

    private static final Logger LOGGER = Logger.getLogger(ReportBuilder.class.getName());
    private static final Properties PROPERTIES = loadProperties();
    private static final Connection CONNECTION = DatabaseConnector.getConnection(PROPERTIES, LOGGER);

    public ReportBuilder() {
        if (CONNECTION == null) {
            LOGGER.severe("No database connection available.");
        }
    }

    public void build() {
        if (CONNECTION == null) return;

        LOGGER.info("Report generation initialized.");
        JasperReportBuilder report = createReport();
        try {
            report
                    .show()
                    .toPdf(new FileOutputStream(Objects.requireNonNull(PROPERTIES).getProperty("report.output.path")));
            LOGGER.info("Report generated successfully.");
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error exporting report as .PDF " + e.getMessage());
        } catch (DRException e) {
            LOGGER.severe("Error to show the generated report " + e.getMessage());
        }
    }

    private JasperReportBuilder createReport() {
        return DynamicReports.report()
                .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
                .title(Components.text("Holiday Report"))
                .pageFooter(Components.pageXofY())
                .setDataSource(getCrossTabDataSourceQuery(), CONNECTION)
                .summary(createCrosstab(), createBarChart());
    }

    private CrosstabBuilder createCrosstab() {
        return DynamicReports.ctab.crosstab()
                .headerCell(Components.text("State/Month"))
                .rowGroups(createRowGroup())
                .columnGroups(createColumnGroup())
                .measures(createMeasure());
    }

    private ComponentBuilder<?, ?> createBarChart() {
        TextColumnBuilder<String> monthColumn = DynamicReports.col.column(
                "Month",
                "month",
                DataTypes.stringType()
        );
        TextColumnBuilder<Integer> italyHolidayCountColumn = DynamicReports.col.column(
                "Italy",
                "italy_holiday_count",
                DataTypes.integerType()
        );
        TextColumnBuilder<Integer> moldovaHolidayCountColumn = DynamicReports.col.column(
                "Moldova",
                "moldova_holiday_count",
                DataTypes.integerType()
        );

        return DynamicReports.cht.barChart()
                .setCategory(monthColumn)
                .series(
                        DynamicReports.cht.serie(italyHolidayCountColumn),
                        DynamicReports.cht.serie(moldovaHolidayCountColumn)
                )
                .setCategoryAxisFormat(
                        DynamicReports.cht.axisFormat().setLabel("Month"))
                .setValueAxisFormat(
                        DynamicReports.cht.axisFormat().setLabel("Number of Holidays"))
                .setDataSource(getBarChartDataSourceQuery(), CONNECTION);
    }

    private CrosstabRowGroupBuilder<String> createRowGroup() {
        return DynamicReports.ctab.rowGroup("country", String.class)
                .setHeaderWidth(50);
    }

    private CrosstabColumnGroupBuilder<String> createColumnGroup() {
        return DynamicReports.ctab.columnGroup("month", String.class)
                .setTotalHeaderStyle(DynamicReports.stl.style()
                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
                .setHeaderHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
    }

    private CrosstabMeasureBuilder<Integer> createMeasure() {
        return DynamicReports.ctab.measure("id", Integer.class, Calculation.COUNT);
    }

    private String getCrossTabDataSourceQuery() {
        return "SELECT id, country, TO_CHAR(DATA,'mm/yyyy') as month " +
                "FROM holidays " +
                "where to_char(data, 'YYYY') = '2017'";
    }

    private String getBarChartDataSourceQuery() {
        return "SELECT\n" +
                "  TO_CHAR(data, 'Month') AS month,\n" +
                "  SUM(CASE WHEN country = 'Italia' THEN 1 ELSE 0 END) AS italy_holiday_count,\n" +
                "  SUM(CASE WHEN country = 'Moldavia' THEN 1 ELSE 0 END) AS moldova_holiday_count\n" +
                "FROM\n" +
                "  holidays\n" +
                "WHERE \n" +
                "  TO_CHAR(data, 'YYYY') = '2021'\n" +
                "GROUP BY\n" +
                "  TO_CHAR(data, 'Month')\n" +
                "ORDER BY\n" +
                "  TO_DATE(TO_CHAR(data, 'Month'), 'Month')";

    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ReportBuilder.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                LOGGER.severe("Input from application properties is NULL");
                return null;
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            LOGGER.severe("Error reading application properties" + e.getMessage());
            return null;
        }
    }

}