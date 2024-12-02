package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.RacingTeam;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the view that shows participants in the selected team.
 * This class manages the display of team name.
 */
public class AllShowDriversInSelectedTeamController implements Initializable {

    @FXML
    private Label teamNameLabel;

    @FXML
    private ListView<String> participantsListView;

    private RacingTeam selectedRacingTeam;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void show(RacingTeam racingTeam) {
        this.selectedRacingTeam = racingTeam;
        teamNameLabel.setText(racingTeam.getName());
        List<Driver> drivers = DriverDAO.build().findDriversByTeam(selectedRacingTeam.getId());
        ObservableList<String> participantNames = FXCollections.observableArrayList();
        for (Driver driver : drivers) {
            participantNames.add(driver.getName());
        }
        participantsListView.setItems(participantNames);
    }


}