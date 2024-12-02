package org.juanmariiaa.view;

import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.Driver;
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
 * Controller class for the All Teams view.
 * This class manages the display and editing of team information in a TableView,
 * as well as navigation to different views and delete of selected tournaments.
 */
public class AllTeamsController implements Initializable {

    @FXML
    private TableView<RacingTeam> tableView;

    @FXML
    private TableColumn<RacingTeam,String> columnID;
    @FXML
    private TableColumn<RacingTeam,String> columnName;
    @FXML
    private TableColumn<RacingTeam,String> columnCity;
    @FXML
    private TableColumn<RacingTeam,String> columnInstitution;
    RacingTeamDAO racingTeamDAO = new RacingTeamDAO();
    private User currentUser;
    private ObservableList<RacingTeam> racingTeams;



    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SingletonUserSession.getCurrentUser();
        try {
            List<RacingTeam> teamsList = RacingTeamDAO.build().findAll(currentUser.getId());
            for (RacingTeam racingTeam : teamsList) {
                List<Driver> drivers = DriverDAO.build().findDriversByTeam(racingTeam.getId());
                racingTeam.setParticipants(drivers);
            }
            this.racingTeams = FXCollections.observableArrayList(teamsList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        tableView.setItems(this.racingTeams);
        tableView.setEditable(true);
        columnID.setCellValueFactory(team -> new SimpleIntegerProperty(team.getValue().getId()).asString());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(event -> {
            RacingTeam racingTeam = event.getRowValue();
            racingTeam.setName(event.getNewValue());
            racingTeamDAO.update(racingTeam);
            tableView.refresh();
        });

        // City column
        columnCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        columnCity.setCellFactory(TextFieldTableCell.forTableColumn());
        columnCity.setOnEditCommit(event -> {
            RacingTeam racingTeam = event.getRowValue();
            racingTeam.setCity(event.getNewValue());
            racingTeamDAO.update(racingTeam);
            tableView.refresh();
        });

        // Institution column
        columnInstitution.setCellValueFactory(new PropertyValueFactory<>("institution"));
        columnInstitution.setCellFactory(TextFieldTableCell.forTableColumn());
        columnInstitution.setOnEditCommit(event -> {
            RacingTeam racingTeam = event.getRowValue();
            racingTeam.setInstitution(event.getNewValue());
            racingTeamDAO.update(racingTeam);
            tableView.refresh();
        });


    }

    @FXML
    private void deleteSelected() {
        RacingTeam selectedT = tableView.getSelectionModel().getSelectedItem();

        if (selectedT != null) {
            try {
                tableView.getItems().remove(selectedT);
                racingTeamDAO.delete(selectedT);
                Utils.showPopUp("DELETE", "Team deleted", "This team has been deleted.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while deleting team: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            Utils.showPopUp("Error", null, "Please select a team to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btDelete() throws SQLException {
        deleteSelected();
    }
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


    @FXML
    private void switchToShowParticipantsInSelectedTeam() throws IOException {
        RacingTeam selectedRacingTeam = tableView.getSelectionModel().getSelectedItem();
        if (selectedRacingTeam != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("allShowDriversInSelectedTeam.fxml"));
            Parent root = loader.load();
            AllShowDriversInSelectedTeamController controller = loader.getController();
            controller.show(selectedRacingTeam);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } else {
            Utils.showPopUp("Error", null, "Please select a team first!", Alert.AlertType.ERROR);
        }
    }
}