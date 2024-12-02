package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.DAO.CarRaceDAO;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller class for displaying and managing details of a selected tournament.
 * This controller handles updating tournament details, displaying associated teams
 * and creating new teams for this selected tournament.
 */
public class SelectedTournamentController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField cityField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<RacingTeam> teamsListView;
    private User currentUser;


    private CarRace selectedCarRace;

    private CarRaceDAO carRaceDAO;
    private RacingTeamDAO racingTeamDAO;
    private ObservableList<RacingTeam> teamsData;

    /**
     * Initializes the controller with the given tournament.
     *
     * @param carRace The tournament to be displayed in the controller.
     */
    public void initialize(CarRace carRace) {
        currentUser = SingletonUserSession.getCurrentUser();
        this.selectedCarRace = carRace;
        this.carRaceDAO = new CarRaceDAO();
        this.racingTeamDAO = new RacingTeamDAO();
        displayTournamentDetails();
        displayTeams();
    }
    /**
     * Displays the details of the selected tournament.
     */
    private void displayTournamentDetails() {
        nameField.setText(selectedCarRace.getName());
        locationField.setText(selectedCarRace.getLocation());
        cityField.setText(selectedCarRace.getCity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(selectedCarRace.getDate());
        datePicker.setValue(LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    /**
     * Display the list of teams associated with the selected tournament.
     *
     * @throws IOException if an error occurs while loading the ShowTeams.fxml file
     */
    private void displayTeams() {
        List<RacingTeam> racingTeams = racingTeamDAO.findTeamsByRace(selectedCarRace.getId());
        if (teamsData == null) {
            teamsData = FXCollections.observableArrayList();
        }
        teamsData.clear(); // Clear the existing data before adding new data
        teamsData.addAll(racingTeams); // Add new data
        teamsListView.setItems(teamsData);
    }

    /**
     * Updates the selected tournament's details, this method is then associated with a button if
     * the user changes any fields in the TextField.
     *
     * @throws SQLException if an error occurs while updating the tournament in the database.
     * @throws NullPointerException if any of the fields are empty.
     */
    @FXML
    private void update() {
        try {
            selectedCarRace.setName(nameField.getText());
            selectedCarRace.setLocation(locationField.getText());
            selectedCarRace.setCity(cityField.getText());
            selectedCarRace.setDate(Date.valueOf(datePicker.getValue()));
            carRaceDAO.update(selectedCarRace);
            Utils.showPopUp("UPDATE", "Tournament Updated", "Tournament details updated successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.showPopUp("ERROR", "Update Failed", "Failed to update tournament details.", Alert.AlertType.ERROR);
        } catch (NullPointerException e) {
            Utils.showPopUp("ERROR", "Update Failed", "Please fill in all the fields.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }

    /**
     * Refreshes the list of teams associated with the selected tournament.
     * This associated with a button in the fxml file.
     * @throws IOException if an error occurs while loading the ShowTeams.fxml file.
     */
    @FXML
    private void refresh() throws IOException {
        List<RacingTeam> racingTeams = racingTeamDAO.findTeamsByRace(selectedCarRace.getId());
        teamsListView.getItems().setAll(racingTeams);
    }


    @FXML
    private void switchToMyTournament() throws IOException {
        App.setRoot("myTournaments");
    }

    @FXML
    private void switchToFinder() throws IOException {
        App.setRoot("finder");
    }

    /**
     * Switches to the ShowTeams.fxml window to display the list of teams for the selected tournament.
     *
     * @throws IOException if an error occurs while loading the ShowTeams.fxml file.
     */
    @FXML
    public void switchToShowTeams() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("showTeams.fxml"));
            Parent root = loader.load();

            ShowTeamsController controller = loader.getController();
            controller.loadTeams(selectedCarRace);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToPictures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pictures.fxml"));
            Parent root = loader.load();

            PicturesTournamentController controller = loader.getController();
            controller.loadPictures(selectedCarRace);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tournament Pictures");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showPopUp("ERROR", "Picture Display Failed", "Failed to load pictures.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
    /**
     * Switches to the CreateTeam.fxml window to allow the user to create a new team for the selected tournament.
     *
     * @throws IOException if an error occurs while loading the CreateTeam.fxml file.
     */
    @FXML
    private void switchToCreateTeam() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createTeam.fxml"));
        Parent root = loader.load();

        CreateTeamController controller = loader.getController();
        controller.setSelectedTournament(selectedCarRace);
        controller.setTeamDAO(racingTeamDAO);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Create Team");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        displayTeams();
    }


}