package md.turcuman.jasper.jrbean.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import md.turcuman.jasper.jrbean.Model.Holiday;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class XmlDataReader {

    public static List<Holiday> readDataFromXml(Properties properties, Logger logger) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            holidays = xmlMapper.readValue(new File(properties.getProperty("xml.data.path")),
                    new TypeReference<List<Holiday>>() {});
        } catch (IOException e) {
            logger.severe("Error reading data from XML: " + e.getMessage());
        }
        return holidays;
    }
}
