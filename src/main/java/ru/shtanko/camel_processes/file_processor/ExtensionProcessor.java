package ru.shtanko.camel_processes.file_processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shtanko.camel_processes.Main;
import ru.shtanko.camel_processes.util.PropertyLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ru.shtanko.camel_processes.util.Constants.RESOURCES;
import static ru.shtanko.camel_processes.util.Constants.SQL;

public class ExtensionProcessor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final long BATCH_FILES = 100;
    private static final String EXT_TXT = "txt";
    private static final String EXT_XML = "xml";
    private static final String EXT_ANOTHER = "ext_another";

    private static Map<String, Long> countByTypeFiles;
    private static long startTimeHandlingBatchFiles = 0;
    private static long countFiles = 0;
    private static boolean isStartHandlingBatchFiles = true;

    static {
        countByTypeFiles = new HashMap<>();
        countByTypeFiles.put(EXT_TXT, 0L);
        countByTypeFiles.put(EXT_XML, 0L);
        countByTypeFiles.put(EXT_ANOTHER, 0L);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if(isStartHandlingBatchFiles) {
            startTimeHandlingBatchFiles = System.currentTimeMillis();
            isStartHandlingBatchFiles = false;
        }
        handlingMessage(exchange);
        countFiles++;

        if (countFiles % BATCH_FILES == 0){
            logger.info("------------------------------------------------");
            logger.info("Количество файлов txt " + countByTypeFiles.get(EXT_TXT));
            logger.info("Количество файлов xml " + countByTypeFiles.get(EXT_XML));
            logger.info("Количество остальных файлов " + countByTypeFiles.get(EXT_ANOTHER));
            logger.info("Время обработки сообщений " + (System.currentTimeMillis() - startTimeHandlingBatchFiles));
            logger.info("------------------------------------------------");
            isStartHandlingBatchFiles = true;
        }

    }

    private String getFileName(Exchange exchange) {
        return exchange.getIn().getHeader(Exchange.FILE_NAME).toString();
    }

    private String getExtensionFile(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    private String getBody(Exchange exchange) {
        return exchange.getIn().getBody(String.class);
    }

    private void handlingMessage(Exchange exchange) {
        String fileName = getFileName(exchange);

        String extensionFile = getExtensionFile(fileName);

        if (extensionFile.equals(EXT_TXT) || extensionFile.equals(EXT_XML)) {
            incrementCountFiles(extensionFile);
        } else {
            incrementCountFiles(EXT_ANOTHER);
        }

        if (extensionFile.equals(EXT_TXT)) {
            String body = getBody(exchange);
            saveBodyMessageDB(body);
        }
    }

    private void incrementCountFiles(String extensionFile) {
        long count = countByTypeFiles.get(extensionFile);
        countByTypeFiles.put(extensionFile, ++count);
    }


    private void saveBodyMessageDB(String body) {
        PropertyLoader propertyLoader = new PropertyLoader(RESOURCES);
        try (Connection connection = DriverManager.getConnection(
                propertyLoader.getConnectionURL(),
                propertyLoader.getUser(),
                propertyLoader.getPassword())) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setString(1, body);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
