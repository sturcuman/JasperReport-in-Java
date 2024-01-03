package md.turcuman.jasper.dynamicreport.Utils;

import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            throw new IllegalStateException("No database connection available.");
        }
    }

    public void build() {
        try {

            LOGGER.info("Report generation initialized.");

            DynamicReports.report()
                    .setTemplate(DynamicReports
                            .template()
                            .setColumnStyle(createColumnDataStyle())
                            .setColumnTitleStyle(createColumnHeaderStyle()))
                    .title(createTitleComponent())
                    .columns(createColumns())
                    .pageFooter(createPageFooterComponent())
                    .setDataSource("SELECT * FROM holidays", CONNECTION)
                    .show()
                    .toPdf(new FileOutputStream(Objects.requireNonNull(PROPERTIES).getProperty("report.output.path")));

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
                        Components.horizontalList()
                                .add(
                                        Components.text("Holidays")
                                                .setStyle(titleStyle)
                                                .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
                                        Components.image(PROPERTIES.getProperty("image.path"))
                                                .setHorizontalImageAlignment(HorizontalImageAlignment.RIGHT)
                                                .setFixedDimension(170, 59)
                                )
                                .setStyle(titleStyle)
                )
                .newRow()
                .add(Components.filler().setStyle(
                        DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point()))
                );
    }

    private StyleBuilder createTitleStyle() {
        return DynamicReports.stl.style()
                .bold()
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setBackgroundColor(new Color(0, 102, 153))
                .setForegroundColor(Color.WHITE)
                .setFontSize(24)
                .setPadding(10);
    }

    private StyleBuilder createColumnHeaderStyle() {
        return DynamicReports.stl.style()
                .bold()
                .setFontSize(12)
                .setBackgroundColor(new Color(230, 230, 230))
                .setForegroundColor(new Color(0, 102, 153))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    private StyleBuilder createColumnDataStyle() {
        return DynamicReports.stl.style()
                .setBottomBorder(DynamicReports.stl.pen1Point())
                .setFontSize(12)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    private ColumnBuilder<?, ?>[] createColumns() {
        return new ColumnBuilder[]{
                DynamicReports.col.column("Country", "country", DataTypes.stringType()),
                DynamicReports.col.column("Name", "name", DataTypes.stringType()),
                DynamicReports.col.column("Data", "data", DataTypes.dateType())
        };
    }

    private HorizontalListBuilder createPageFooterComponent() {
        StyleBuilder footerStyle = DynamicReports.stl.style().setFontSize(10);
        StyleBuilder footerTextStyle = DynamicReports.stl.style(footerStyle)
                .setHorizontalAlignment(HorizontalAlignment.LEFT);
        StyleBuilder footerPageStyle = DynamicReports.stl.style(footerStyle)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        HorizontalListBuilder footer = Components.horizontalList()
                .add(
                        Components.text(new SimpleDateFormat("EEEE dd")
                                .format(new Date())).setStyle(footerTextStyle),
                        Components.horizontalGap(10),
                        Components.pageXofY().setStyle(footerPageStyle)
                )
                .newRow()
                .add(Components.line());

        return footer;
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