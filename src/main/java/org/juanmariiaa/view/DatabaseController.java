package org.juanmariiaa.view;

import javafx.fxml.FXML;
import org.juanmariiaa.model.connection.ConnectionManager;

import java.io.IOException;

public class DatabaseController {

    @FXML
    public void handleMariaDB() throws IOException {
        ConnectionManager.setDatabaseType(ConnectionManager.DatabaseType.MARIADB);
        App.setRoot("login");
    }

    @FXML
    public void handleSQLite() throws IOException {
        ConnectionManager.setDatabaseType(ConnectionManager.DatabaseType.SQLITE);
        App.setRoot("login");
    }
}

