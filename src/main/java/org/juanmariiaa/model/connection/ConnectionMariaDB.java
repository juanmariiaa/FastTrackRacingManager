package org.juanmariiaa.model.connection;
import org.juanmariiaa.utils.XMLManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMariaDB {
    private final static String FILE="connection.xml";
    private static ConnectionMariaDB _instance;
    private static Connection conn;

    /**
     * Constructor to initialize a new MariaDB connection using properties from an XML file.
     */
    public ConnectionMariaDB() {
        ConnectionProperties properties = (ConnectionProperties) XMLManager.readXML(new ConnectionProperties(), FILE);

        try {
            conn = DriverManager.getConnection(properties.getURL(), properties.getUser(), properties.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null;
        }
    }

    /**
     * Get a connection instance. If no instance exists, a new one is created.
     *
     * @return Connection instance
     */
    public static Connection getConnection() {
        if (_instance == null) {
            _instance = new ConnectionMariaDB();
        }
        return _instance.conn;
    }
}



