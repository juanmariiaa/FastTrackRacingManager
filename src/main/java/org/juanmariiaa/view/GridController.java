package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.StartingGridDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.Driver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

    private CarRace selectedCarRace;

    private final HashMap<String, String> teamColors = new HashMap<>();
    private final Random random = new Random();


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

        // Establecer estilos de filas con colores de equipo
        gridTableView.setRowFactory(tv -> new TableRow<Driver>() {
            @Override
            protected void updateItem(Driver driver, boolean empty) {
                super.updateItem(driver, empty);
                if (driver == null || empty) {
                    setStyle("");
                } else {
                    String teamColor = getOrAssignTeamColor(driver.getTeam().getName());
                    setStyle("-fx-background-color: " + teamColor + ";");
                }
            }
        });
    }

    /**
     * Obtiene el color asignado a un equipo o lo genera si no existe.
     */
    private String getOrAssignTeamColor(String teamName) {
        if (!teamColors.containsKey(teamName)) {
            String randomColor = generateRandomColor();
            teamColors.put(teamName, randomColor);
        }
        return teamColors.get(teamName);
    }
    private String generateRandomColor() {
        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }


    public void loadGridData(CarRace carRace) {
        this.selectedCarRace = carRace;  // Guardar la carrera seleccionada

        raceIdField.setText(String.valueOf(carRace.getId()));

        List<Driver> drivers = driverDAO.findDriversByRaceId(carRace.getId());

        if (drivers.isEmpty()) {
            showAlert("Información", "No hay drivers para esta carrera.", Alert.AlertType.INFORMATION);
            return;
        }

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
    private void exportGridToFile() {
        if (gridTableView.getItems().isEmpty()) {
            showAlert("Error", "No hay datos en el grid para exportar.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar lista del Grid");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));

        // Mostrar el diálogo para seleccionar dónde guardar el archivo
        File file = fileChooser.showSaveDialog(gridTableView.getScene().getWindow());
        if (file != null) {
            saveGridToFile(file);
        }
    }

    private void saveGridToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Grid: ");
            writer.newLine();
            writer.write(String.format("%-10s %-25s %-20s", "Position", "Driver", "Team"));
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();

            ObservableList<Driver> drivers = gridTableView.getItems();
            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                String line = String.format("%-10d %-25s %-20s",
                        i + 1,
                        driver.getName() + " " + driver.getSurname(),
                        driver.getTeam().getName());
                writer.write(line);
                writer.newLine();
            }

            showAlert("Success", "Grid list successfully exported.", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "An error occurred while saving the file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void switchToShowTeams() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("showTeams.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "showTeams.fxml"

            ShowTeamsController controller = loader.getController();
            controller.loadTeams(selectedCarRace);  // Aquí pasamos el objeto selectedCarRace

            Stage stage = (Stage) somePane.getScene().getWindow(); // Asegúrate de reemplazar `somePane` con el nodo adecuado
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hubo un error al cargar la vista de los equipos.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToSelectedRace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedRace.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "selectedRace.fxml"

            SelectedRaceController controller = loader.getController();
            controller.initialize(selectedCarRace);  // Pasar el objeto selectedCarRace

            Stage stage = (Stage) somePane.getScene().getWindow(); // Asegúrate de reemplazar `somePane` con el nodo adecuado
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal

        } catch (IOException e) {
            throw new RuntimeException("Error cargando el archivo FXML de SelectedRace", e);
        }
    }
    @FXML
    private void switchToPictures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pictures.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "pictures.fxml"

            PicturesTournamentController controller = loader.getController();
            controller.loadPictures(selectedCarRace); // Cargar los datos para la vista de Pictures

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
