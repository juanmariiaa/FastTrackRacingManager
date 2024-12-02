package org.juanmariiaa.view;

import javafx.fxml.FXML;
import org.juanmariiaa.model.connection.ConnectionManager;

public class DatabaseController {

    @FXML
    public void handleMariaDB() {
        ConnectionManager.setDatabaseType(ConnectionManager.DatabaseType.MARIADB);
    }

    @FXML
    public void handleSQLite() {
        ConnectionManager.setDatabaseType(ConnectionManager.DatabaseType.SQLITE);
    }
}

