package org.juanmariiaa.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.CarRaceDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class that display all the Tournament associated with the currentUser.
 */
public class MyTournamentsController implements Initializable {
    @FXML
    private TableView<CarRace> tableView;
    @FXML
    private TableColumn<CarRace, String> columnID;
    @FXML
    private TableColumn<CarRace, String> columnName;
    @FXML
    private TableColumn<CarRace, String> columnLocation;
    @FXML
    private TableColumn<CarRace, String> columnCity;
    @FXML
    private TableColumn<CarRace, Date> columnDate;

    private ObservableList<CarRace> carRaces;
    private CarRaceDAO carRaceDAO = new CarRaceDAO();

    /**
     * Initializes the controller, and display all the tournament details.
     *
     * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    public void initialize(URL location, ResourceBundle resources) {
        int userId = SingletonUserSession.getCurrentUser().getId();

        try {
            List<CarRace> carRaces = CarRaceDAO.build().findAll(userId);
            this.carRaces = FXCollections.observableArrayList(carRaces);
        } catch (SQLException e) {
            Utils.showPopUp("Error", null, "Error while fetching tournaments: " + e.getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }

        tableView.setItems(this.carRaces);
        tableView.setEditable(true);

        // Set cell value factories for table columns
        columnID.setCellValueFactory(tournament -> new SimpleIntegerProperty(tournament.getValue().getId()).asString());
        columnName.setCellValueFactory(tournament -> new SimpleStringProperty(tournament.getValue().getName()));
        columnLocation.setCellValueFactory(tournament -> new SimpleStringProperty(tournament.getValue().getLocation()));
        columnCity.setCellValueFactory(tournament -> new SimpleStringProperty(tournament.getValue().getCity()));
        columnDate.setCellValueFactory(cellData -> {
            Date fecha = (Date) cellData.getValue().getDate();
            return new SimpleObjectProperty<>(fecha);
        });
    }

    /**
     * Handles the selection of a tournament.
     * Opens the details view for the selected tournament.
     */
    @FXML
    private void selectTournament() {
        CarRace selectedCarRace = tableView.getSelectionModel().getSelectedItem();
        if (selectedCarRace != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedTournament.fxml"));
                Parent root = loader.load();

                SelectedTournamentController controller = loader.getController();
                controller.initialize(selectedCarRace);

                Scene scene = new Scene(root);
                Stage stage = (Stage) tableView.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Tournament Details");
            } catch (IOException e) {
                Utils.showPopUp("Error", null, "Error while opening tournament details: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            Utils.showPopUp("Error", null, "Please select a tournament first.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Deletes the selected tournament.
     */
    @FXML
    private void deleteSelected() {
        CarRace selectedT = tableView.getSelectionModel().getSelectedItem();

        if (selectedT != null) {
            tableView.getItems().remove(selectedT);

            try {
                carRaceDAO.delete(selectedT.getId());
                Utils.showPopUp("DELETE", "Team deleted", "This team has been deleted.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btDelete() throws SQLException {
        deleteSelected();
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
    private void switchToCreateTournament() throws IOException {
        App.setRoot("createTournament");
    }
    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}
