package org.juanmariiaa.model.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSQLite {
    private static final String URL = "jdbc:sqlite:database/sqlitedb.db";
    private static ConnectionSQLite _instance;
    private static Connection conn;

    private ConnectionSQLite() {
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null;
        }
    }

    public static Connection getConnection() {
        if (_instance == null) {
            _instance = new ConnectionSQLite();
        }
        return conn;
    }
}
