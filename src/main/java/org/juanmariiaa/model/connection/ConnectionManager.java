package org.juanmariiaa.model.connection;

import org.juanmariiaa.utils.XMLManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    public enum DatabaseType {
        MARIADB,
        SQLITE
    }

    private static DatabaseType currentDatabaseType = DatabaseType.MARIADB;

    public static void setDatabaseType(DatabaseType databaseType) {
        currentDatabaseType = databaseType;
    }

    public static Connection getConnection() {
        try {
            switch (currentDatabaseType) {
                case MARIADB:
                    return getMariaDBConnection();
                case SQLITE:
                    return getSQLiteConnection();
                default:
                    throw new IllegalStateException(" ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(" ", e);
        }
    }

    private static Connection getMariaDBConnection() throws SQLException {
        ConnectionProperties properties = (ConnectionProperties) XMLManager.readXML(new ConnectionProperties(), "connection.xml");
        return DriverManager.getConnection(properties.getURL(), properties.getUser(), properties.getPassword());
    }

    private static Connection getSQLiteConnection() throws SQLException {
        try {
            var resource = ConnectionManager.class.getClassLoader().getResource("car_race.sqlite");
            if (resource == null) {
                throw new RuntimeException(" ");
            }
            String url = "jdbc:sqlite:" + resource.getPath();
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            throw new RuntimeException(" ", e);
        }
    }

}

