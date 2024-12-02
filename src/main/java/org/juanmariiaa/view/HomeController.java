package org.juanmariiaa.view;

import javafx.fxml.FXML;
import org.juanmariiaa.view.App;

import java.io.IOException;

/**
 * Controller that links to the sites you can go.
 */
public class HomeController {
    @FXML
    private void switchToMyTournaments() throws IOException {
        App.setRoot("myTournaments");
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
    @FXML
    private void switchToDatabase() throws IOException {
        App.setRoot("finder");
    }



}
