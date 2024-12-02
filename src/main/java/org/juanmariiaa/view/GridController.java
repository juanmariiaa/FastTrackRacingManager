package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.StartingGridDAO;
import org.juanmariiaa.model.domain.CarRace;  // Asegúrate de importar CarRace
import org.juanmariiaa.model.domain.Driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class GridController {

    @FXML
    private TextField raceIdField;

    @FXML
    private TableView<Driver> gridTableView;

    @FXML
    private TableColumn<Driver, String> positionColumn;

    @FXML
    private TableColumn<Driver, String> nameColumn;

    @FXML
    private TableColumn<Driver, String> teamColumn;

    @FXML
    private Pane somePane;

    private DriverDAO driverDAO;
    private StartingGridDAO startingGridDAO;

    private CarRace selectedCarRace;  // Aquí cambiamos a usar selectedCarRace

    public GridController() {
        driverDAO = new DriverDAO();
        startingGridDAO = new StartingGridDAO();
    }

    @FXML
    public void initialize() {
        // Inicializar las columnas de la tabla
        positionColumn.setCellValueFactory(cellData -> {
            int index = gridTableView.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(index));
        });
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName() + " " + cellData.getValue().getSurname()));
        teamColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeam().getName()));
    }

    // Método para cargar los datos del grid utilizando el objeto selectedCarRace
    public void loadGridData(CarRace carRace) {
        this.selectedCarRace = carRace;  // Guardar la carrera seleccionada

        // Cargar el ID de la carrera en el campo de texto
        raceIdField.setText(String.valueOf(carRace.getId()));

        // Obtener los pilotos asociados con la carrera seleccionada
        List<Driver> drivers = driverDAO.findDriversByRaceId(carRace.getId());

        if (drivers.isEmpty()) {
            showAlert("Información", "No hay drivers para esta carrera.", Alert.AlertType.INFORMATION);
            return;
        }

        // Mezclar la lista para generar el grid aleatorio
        Collections.shuffle(drivers);

        // Actualizar la tabla con los pilotos
        ObservableList<Driver> observableDrivers = FXCollections.observableArrayList(drivers);
        gridTableView.setItems(observableDrivers);
    }

    @FXML
    public void handleGenerateGrid() {
        String raceIdText = raceIdField.getText();

        if (raceIdText.isEmpty()) {
            showAlert("Error", "Por favor ingrese un Race ID.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int raceId = Integer.parseInt(raceIdText);
            List<Driver> drivers = driverDAO.findDriversByRaceId(raceId);

            if (drivers.isEmpty()) {
                showAlert("Información", "No hay drivers para esta carrera.", Alert.AlertType.INFORMATION);
                return;
            }

            // Mezclar la lista para generar el grid aleatorio
            Collections.shuffle(drivers);

            // Actualizar la tabla
            ObservableList<Driver> observableDrivers = FXCollections.observableArrayList(drivers);
            gridTableView.setItems(observableDrivers);

        } catch (NumberFormatException e) {
            showAlert("Error", "Race ID debe ser un número.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error al generar la parrilla: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void switchToShowTeams() {
        try {
            // Cargar el archivo FXML para mostrar los equipos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("showTeams.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "showTeams.fxml"

            // Obtener el controlador de la vista de ShowTeams y pasarle los datos necesarios
            ShowTeamsController controller = loader.getController();
            controller.loadTeams(selectedCarRace);  // Aquí pasamos el objeto selectedCarRace

            // Cambiar el contenido de la escena principal
            Stage stage = (Stage) somePane.getScene().getWindow(); // Asegúrate de reemplazar `somePane` con el nodo adecuado
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal

        } catch (IOException e) {
            // Imprimir el error para el seguimiento
            e.printStackTrace();
            // Mostrar un mensaje de error al usuario si lo prefieres
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hubo un error al cargar la vista de los equipos.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToSelectedRace() {
        try {
            // Cargar el archivo FXML para SelectedRace
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedRace.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "selectedRace.fxml"

            // Obtener el controlador de la vista de SelectedRace y pasarle los datos necesarios
            SelectedRaceController controller = loader.getController();
            controller.initialize(selectedCarRace);  // Pasar el objeto selectedCarRace

            // Cambiar el contenido de la escena principal
            Stage stage = (Stage) somePane.getScene().getWindow(); // Asegúrate de reemplazar `somePane` con el nodo adecuado
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal

        } catch (IOException e) {
            throw new RuntimeException("Error cargando el archivo FXML de SelectedRace", e);
        }
    }
    @FXML
    private void switchToPictures() {
        try {
            // Cargar el archivo FXML de la vista de "PicturesTournament"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pictures.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "pictures.fxml"

            // Obtener el controlador de la vista de "PicturesTournament"
            PicturesTournamentController controller = loader.getController();
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
