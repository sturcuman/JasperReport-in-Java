package md.turcuman.jasper.dynamicreport.Utils;

import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class ReportBuilder {

    private static final Logger LOGGER = Logger.getLogger(ReportBuilder.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    private Connection connection;

    public ReportBuilder() {
        connection = DatabaseConnector.getConnection(Objects.requireNonNull(PROPERTIES), LOGGER);
        if (connection == null) {
            LOGGER.severe("No database connection available.");
            throw new IllegalStateException("No database connection available.");
        }
    }

    public void build() {
        try {
            // Initialize the report generation
            LOGGER.info("Report generation initialized.");

            // Build the report
            DynamicReports.report()
                    .setTemplate(DynamicReports.template().setColumnStyle(createColumnDataStyle()).setColumnTitleStyle(createColumnHeaderStyle()))
                    .title(createTitleComponent())
                    .columns(createColumns())
                    .pageFooter(createPageFooterComponent())
                    .setDataSource("SELECT * FROM holidays", connection)
                    .show()
                    .toPdf(new FileOutputStream(Objects.requireNonNull(PROPERTIES).getProperty("report.output.path")));

            // Log success
            LOGGER.info("Report generated successfully.");
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error exporting report as .PDF " + e.getMessage());
        } catch (DRException e) {
            LOGGER.severe("Error: Cannot show the generated report " + e.getMessage());
        }
    }

    private HorizontalListBuilder createTitleComponent() {
        StyleBuilder titleStyle = createTitleStyle();

        return Components.horizontalList()
                .add(
                        Components.text("Holidays").setStyle(titleStyle).setFixedWidth(400),
                        Components.image("C:/Users/crme126/Downloads/download.png").setFixedDimension(170, 59)
                ).newRow()
                .add(Components.filler().setStyle(DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point())));
    }

    private StyleBuilder createTitleStyle() {
        return DynamicReports.stl.style()
                .bold()
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(new Color(0, 102, 153))
                .setForegroundColor(Color.WHITE)
                .setFontSize(24)
                .setPadding(10);
    }

    private StyleBuilder createColumnHeaderStyle() {
        return DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(0, 102, 153))
                .setForegroundColor(Color.WHITE)
                .setBorder(DynamicReports.stl.pen1Point())
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    private StyleBuilder createColumnDataStyle() {
        return DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.pen1Point());
    }

    private ColumnBuilder<?, ?>[] createColumns() {
        return new ColumnBuilder[]{
                DynamicReports.col.column("Country", "country", DataTypes.stringType()),
                DynamicReports.col.column("Name", "name", DataTypes.stringType()),
                DynamicReports.col.column("Date", "data", DataTypes.dateType())
        };
    }

    private HorizontalListBuilder createPageFooterComponent() {
        StyleBuilder footerStyle = DynamicReports.stl.style().setFontSize(10);

        return Components.horizontalList(
                Components.text("Page ").setStyle(footerStyle),
                Components.pageXofY().setStyle(footerStyle)
        );
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
        } catch (IOException e) {
            LOGGER.severe("Error reading application properties" + e.getMessage());
        }
        return properties;
    }

}