package ru.shtanko.camel_processes.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    private final String resourceName;
    private Properties properties;

    public PropertyLoader(String resourceName) {
        this.resourceName = resourceName;
    }

    private Properties getProperties() throws IOException {
        if (properties == null) {
            try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
                properties = new Properties();
                properties.load(stream);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        return properties;
    }

    public String getConnectionURL() throws IOException {
        return getProperties().getProperty("connection.url");
    }

    public String getUser() throws IOException {
        return getProperties().getProperty("connection.name");
    }

    public String getPassword() throws IOException {
        return getProperties().getProperty("connection.pwd");
    }
}
