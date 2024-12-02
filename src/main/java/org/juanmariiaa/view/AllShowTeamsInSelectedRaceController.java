package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.RacingTeam;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the view that shows all teams in the selected tournament.
 * This class manages the display of teams associated with a specific tournament.
 */
public class AllShowTeamsInSelectedRaceController implements Initializable {

    @FXML
    private Label tournamentNameLabel;

    @FXML
    private ListView<String> teamsListView;

    private CarRace selectedCarRace;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void show(CarRace carRace) {
        this.selectedCarRace = carRace;
        tournamentNameLabel.setText(carRace.getName());
        List<RacingTeam> racingTeams = RacingTeamDAO.build().findTeamsByRace(selectedCarRace.getId());
        ObservableList<String> teamNames = FXCollections.observableArrayList();
        for (RacingTeam racingTeam : racingTeams) {
            teamNames.add(racingTeam.getName());
        }
        teamsListView.setItems(teamNames);
    }

}
