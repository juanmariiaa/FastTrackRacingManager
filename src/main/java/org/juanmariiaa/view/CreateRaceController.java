package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.DAO.CarRaceDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CreateRaceController {
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfLocation;
    @FXML
    private TextField tfCity;
    @FXML
    private DatePicker dtDate;
    @FXML
    private ListView<String> lvTeams;
    @FXML
    private Button btnCreate;

    private CarRaceDAO carRaceDAO = new CarRaceDAO();
    private RacingTeamDAO racingTeamDAO = new RacingTeamDAO();
    private ObservableList<CarRace> carRaces;

    private User currentUser;


    @FXML
    private void initialize() throws SQLException {
        currentUser = SingletonUserSession.getCurrentUser();


        List<RacingTeam> racingTeams = racingTeamDAO.findAll(currentUser.getId());

        ObservableList<String> nameTeamList = FXCollections.observableArrayList();

        for (RacingTeam racingTeam : racingTeams) {
            nameTeamList.add(racingTeam.getName());
        }
        lvTeams.setItems(nameTeamList);
        lvTeams.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void createTournament() throws IOException {
        try {
            String name = tfName.getText();
            String location = tfLocation.getText();
            String city = tfCity.getText();
            LocalDate localDate = dtDate.getValue();

            if (localDate == null) {
                Utils.showPopUp("Error", null, "Please select a valid date.", Alert.AlertType.ERROR);
                return;
            }

            java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
            String dateString = sqlDate.toString();


            ObservableList<String> selectedTeamNames = lvTeams.getSelectionModel().getSelectedItems();
            List<RacingTeam> selectedRacingTeams = new ArrayList<>();
            for (String teamName : selectedTeamNames) {
                List<RacingTeam> racingTeams = racingTeamDAO.findByName(teamName);
                if (!racingTeams.isEmpty()) {
                    selectedRacingTeams.add(racingTeams.get(0));
                }
            }

            CarRace carRace = new CarRace(name, location, city, dateString, selectedRacingTeams);

            try {
                carRace = carRaceDAO.save(currentUser, carRace);

                for (RacingTeam racingTeam : selectedRacingTeams) {
                    carRaceDAO.addTeamToRace(carRace.getId(), racingTeam.getId());
                }

                tfName.clear();
                tfLocation.clear();
                tfCity.clear();
                dtDate.getEditor().clear();
                lvTeams.getSelectionModel().clearSelection();
                switchToMyRaces();

                Utils.showPopUp("Tournament Created", null, "Tournament has been created successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while creating tournament: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Utils.showPopUp("Error", null, "An error occurred while creating the tournament.", Alert.AlertType.ERROR);
        }
    }




    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
    @FXML
    private void switchToMyRaces() throws IOException {
        App.setRoot("myRaces");
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
