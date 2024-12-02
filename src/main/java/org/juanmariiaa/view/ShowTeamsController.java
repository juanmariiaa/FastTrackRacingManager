package org.juanmariiaa.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class that display all the Teams associated with the selectedTournament.
 */
public class ShowTeamsController implements Initializable {

    @FXML
    private TableView<RacingTeam> tableView;
    @FXML
    private TableColumn<RacingTeam, String> nameColumn;
    @FXML
    private TableColumn<RacingTeam, String> cityColumn;
    @FXML
    private TableColumn<RacingTeam, String> institutionColumn;

    private User currentUser;
    private ObservableList<RacingTeam> racingTeams;
    private final RacingTeamDAO racingTeamDAO = new RacingTeamDAO();
    private CarRace selectedCarRace;

    /**
     * Initializes the table view with the current user's data.
     *
     */
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SingletonUserSession.getCurrentUser();
        tableView.setEditable(true);
        nameColumn.setCellValueFactory(team -> new SimpleStringProperty(team.getValue().getName()));
        cityColumn.setCellValueFactory(team -> new SimpleStringProperty(team.getValue().getCity()));
        institutionColumn.setCellValueFactory(team -> new SimpleStringProperty(team.getValue().getInstitution()));
    }

    /**
     * Deletes the selected team from the table and the database.
     */
    @FXML
    private void deleteSelected() {
        RacingTeam selectedT = (RacingTeam) tableView.getSelectionModel().getSelectedItem();

        if (selectedT != null) {
            tableView.getItems().remove(selectedT);

            try {
                racingTeamDAO.delete(selectedT);
                Utils.showPopUp("DELETE", "Team deleted", "This team has deleted.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while deleting team: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }

        }
    }


    /**
     * Loads the teams associated with the given tournament.
     *
     * @param carRace The tournament for which the teams are to be loaded.
     */
    public void loadTeams(CarRace carRace) {
        this.selectedCarRace = carRace;
        List<RacingTeam> racingTeams = racingTeamDAO.findTeamsByRace(carRace.getId());
        this.racingTeams = FXCollections.observableArrayList(racingTeams);
        tableView.setItems(this.racingTeams);
    }


    @FXML
    public void switchToSelectTeam() {
        RacingTeam selectedRacingTeam = tableView.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedTeam.fxml"));
            Parent root = loader.load();

            SelectedTeamController controller = loader.getController();
            controller.initialize(selectedRacingTeam);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Select Team");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void refresh() {
        if (selectedCarRace != null) {
            racingTeams.clear();
            racingTeams.addAll(racingTeamDAO.findTeamsByRace(selectedCarRace.getId()));
        }
    }
    public void closeWindow() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}