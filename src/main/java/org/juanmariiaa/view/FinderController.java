package org.juanmariiaa.view;

import javafx.fxml.FXML;

import java.io.IOException;


public class FinderController {

    @FXML
    private void switchToDriver() throws IOException {
        App.setRoot("allDrivers");
    }
    @FXML
    private void switchToRaces() throws IOException {
        App.setRoot("allRaces");
    }
    @FXML
    private void switchToTeams() throws IOException {
        App.setRoot("allTeams");
    }
    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
    @FXML
    private void switchToFinder() throws IOException {
        App.setRoot("finder");
    }
    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}
