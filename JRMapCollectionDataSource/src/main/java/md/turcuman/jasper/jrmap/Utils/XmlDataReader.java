package md.turcuman.jasper.jrmap.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class XmlDataReader {

    public static List<Map<String, ?>> getDataFromXml(Properties properties, Logger logger) {
        List<Map<String, ?>> dataList = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            List<Map<String, Object>> dataMaps = xmlMapper.readValue(
                    new File(properties.getProperty("xml.data.path")),
                    new TypeReference<List<Map<String, Object>>>() {});

            dataList.addAll(dataMaps);
        } catch (IOException e) {
            logger.severe("Error reading data from xml file: " + e.getMessage());
        }
        return dataList;
    }

}
