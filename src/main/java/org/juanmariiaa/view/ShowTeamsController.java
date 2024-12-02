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
import javafx.scene.layout.Pane;
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
    @FXML
    private Pane somePane; // Este es el Pane donde se cargará el nuevo contenido
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
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToPictures() {
        try {
            // Cargar el archivo FXML de la vista de "PicturesTournament"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pictures.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "pictures.fxml"

            // Obtener el controlador de la vista de "PicturesTournament"
            PicturesController controller = loader.getController();
            controller.loadPictures(selectedCarRace); // Cargar los datos para la vista de Pictures

            // Cambiar el contenido de la escena principal
            Stage stage = (Stage) somePane.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException("Error cargando el archivo FXML de PicturesTournament", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error al interactuar con la base de datos", e);
        }
    }

    @FXML
    private void switchToSelectedRace() {
        try {
            // Cargar el archivo FXML para SelectedTournament
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedRace.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "selectedRace.fxml"

            // Obtener el controlador de la vista de SelectedTournament
            SelectedRaceController controller = loader.getController();
            controller.initialize(selectedCarRace); // Pasar los datos necesarios al controlador

            // Cambiar el contenido de la escena principal
            Stage stage = (Stage) somePane.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException("Error cargando el archivo FXML de SelectedTournament", e);
        }
    }

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
    }

    @FXML
    public void switchToGrid() {
        try {
            // Cargar el archivo FXML para Grid
            FXMLLoader loader = new FXMLLoader(getClass().getResource("grid.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "grid.fxml"

            // Obtener el controlador y pasarle datos necesarios
            GridController controller = loader.getController();
            controller.loadGridData(selectedCarRace); // Llamar al método adecuado para cargar los datos

            // Cambiar el contenido de la escena principal
            Stage stage = (Stage) somePane.getScene().getWindow(); // `somePane` es un nodo actual de la escena
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
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
    private void switchToMyRaces() throws IOException {
        App.setRoot("myRaces");
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