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
import javafx.util.StringConverter;
import org.juanmariiaa.model.DAO.CarRaceDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


public class AllRacesController implements Initializable {

    @FXML
    private TableView<CarRace> tableView;

    @FXML
    private TableColumn<CarRace,String> columnID;
    @FXML
    private TableColumn<CarRace,String> columnName;
    @FXML
    private TableColumn<CarRace,String> columnLocation;
    @FXML
    private TableColumn<CarRace,String> columnCity;
    @FXML
    private TableColumn<CarRace, String> columnDate;
    private ObservableList<CarRace> carRaces;
    CarRaceDAO carRaceDAO = new CarRaceDAO();
    private User currentUser;



    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SingletonUserSession.getCurrentUser();
        try {
            List<CarRace> carRaces = CarRaceDAO.build().findAll(currentUser.getId());
            this.carRaces = FXCollections.observableArrayList(carRaces);
        } catch (SQLException e) {
            Utils.showPopUp("Error", null, "Error while fetching races: " + e.getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }

        tableView.setItems(this.carRaces);
        tableView.setEditable(true);
        columnID.setCellValueFactory(race -> new SimpleIntegerProperty(race.getValue().getId()).asString());

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(event -> {
            CarRace carRace = event.getRowValue();
            carRace.setName(event.getNewValue());
            try {
                carRaceDAO.update(carRace);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while updating race: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            tableView.refresh();
        });

        columnLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnLocation.setCellFactory(TextFieldTableCell.forTableColumn());
        columnLocation.setOnEditCommit(event -> {
            CarRace carRace = event.getRowValue();
            carRace.setLocation(event.getNewValue());
            try {
                carRaceDAO.update(carRace);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while updating race: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            tableView.refresh();
        });


        columnCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        columnCity.setCellFactory(TextFieldTableCell.forTableColumn());
        columnCity.setOnEditCommit(event -> {
            CarRace carRace = event.getRowValue();
            carRace.setCity(event.getNewValue());
            try {
                carRaceDAO.update(carRace);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while updating race: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            tableView.refresh();
        });

        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnDate.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDate.setOnEditCommit(event -> {
            CarRace carRace = event.getRowValue();
            carRace.setDate(event.getNewValue());
            try {
                carRaceDAO.update(carRace);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while updating race: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            tableView.refresh();
        });

    }

    @FXML
    private void deleteSelected() {
        CarRace selectedT = tableView.getSelectionModel().getSelectedItem();

        if (selectedT != null) {
            try {
                tableView.getItems().remove(selectedT);
                carRaceDAO.delete(selectedT.getId());
                Utils.showPopUp("DELETE", "Tournament deleted", "This race has been deleted.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                Utils.showPopUp("Error", null, "Error while deleting race: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            Utils.showPopUp("Error", null, "Please select a race to delete.", Alert.AlertType.ERROR);
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
    private void switchToShowTeamsInSelectedTournament() throws IOException {
        CarRace selectedCarRace = tableView.getSelectionModel().getSelectedItem();
        if (selectedCarRace != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("allShowTeamsInSelectedRaces.fxml"));
            Parent root = loader.load();
            AllShowTeamsInSelectedRaceController controller = loader.getController();
            controller.show(selectedCarRace);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } else {
            Utils.showPopUp("Error", null, "Please select a race first!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void switchToAddRemoveTeamToTournament() throws IOException {
        CarRace selectedCarRace = tableView.getSelectionModel().getSelectedItem();
        if (selectedCarRace != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("allAddRemoveTeamFromRace.fxml"));
            Parent root = loader.load();
            AllAddRemoveTeamsRacesController controller = loader.getController();
            controller.show(selectedCarRace);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } else {
            Utils.showPopUp("Error", null, "Please select a race first!", Alert.AlertType.ERROR);
        }
    }
}