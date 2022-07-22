package ru.shtanko.camel_processes.file_processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import ru.shtanko.camel_processes.util.PropertyLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static ru.shtanko.camel_processes.util.Constants.*;

public class TxtFileProcessor implements Processor {
    public static long countTxtFile = 0;

    @Override
    public void process(Exchange exchange) {
        String body = exchange.getIn().getBody(String.class);
        System.out.println("body = " + body);
        saveBodyMessageDB(body);
        ++TxtFileProcessor.countTxtFile;

    }

    private void saveBodyMessageDB(String body) {
        PropertyLoader propertyLoader = new PropertyLoader(RESOURCES);
        try (Connection connection = DriverManager.getConnection(
                propertyLoader.getConnectionURL(),
                propertyLoader.getUser(),
                propertyLoader.getPassword())){
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
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
