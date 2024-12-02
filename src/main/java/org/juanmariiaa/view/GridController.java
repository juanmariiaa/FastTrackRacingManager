package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.StartingGridDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.GridPosition;
import org.juanmariiaa.model.domain.StartingGrid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
        gridTableView.setEditable(true);
        positionColumn.setCellValueFactory(cellData -> {
            int index = gridTableView.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(index));
        });

        positionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        positionColumn.setOnEditCommit(event -> {
            Driver driver = event.getRowValue();
            try {
                int newPosition = Integer.parseInt(event.getNewValue());
                ObservableList<Driver> drivers = gridTableView.getItems();

                if (newPosition > 0 && newPosition <= drivers.size()) {
                    drivers.remove(driver);
                    drivers.add(newPosition - 1, driver);

                    gridTableView.setItems(FXCollections.observableArrayList(drivers));
                } else {
                    showAlert("Error", "La posición debe estar entre 1 y " + drivers.size(), Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "La posición debe ser un número válido.", Alert.AlertType.ERROR);
            }
        });

        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName() + " " + cellData.getValue().getSurname()));
        teamColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeam().getName()));

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
        this.selectedCarRace = carRace;

        raceIdField.setText(String.valueOf(carRace.getId()));

        try {
            StartingGrid savedGrid = startingGridDAO.findGridByRaceId(carRace.getId());

            if (savedGrid != null && savedGrid.getGridPositions() != null && !savedGrid.getGridPositions().isEmpty()) {
                List<Driver> drivers = new ArrayList<>();
                for (GridPosition position : savedGrid.getGridPositions()) {
                    Driver driver = driverDAO.findDriverByDni(position.getDriverDni()); // Asumiendo que tienes un método findDriverByDni
                    if (driver != null) {
                        drivers.add(driver);
                    }
                }

                ObservableList<Driver> observableDrivers = FXCollections.observableArrayList(drivers);
                gridTableView.setItems(observableDrivers);

            } else {
                List<Driver> drivers = driverDAO.findDriversByRaceId(carRace.getId());

                if (drivers.isEmpty()) {
                    showAlert("Info", "There are no drivers for this race.", Alert.AlertType.INFORMATION);
                    return;
                }

                Collections.shuffle(drivers);

                ObservableList<Driver> observableDrivers = FXCollections.observableArrayList(drivers);
                gridTableView.setItems(observableDrivers);

                StartingGrid startingGrid = new StartingGrid();
                startingGrid.setRaceId(carRace.getId());

                for (int i = 0; i < drivers.size(); i++) {
                    Driver driver = drivers.get(i);
                    GridPosition gridPosition = new GridPosition(i + 1, driver.getDni(), driver.getTeam().getId());
                    startingGrid.addGridPosition(gridPosition);
                }

                startingGridDAO.save(startingGrid);

                showAlert("Success", "No grid was found for this race. A new random grid has been generated and saved.", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            showAlert("Error", "An error occurred while loading the grid: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void handleGenerateGrid() {
        String raceIdText = raceIdField.getText();

        try {
            int raceId = Integer.parseInt(raceIdText);
            List<Driver> drivers = driverDAO.findDriversByRaceId(raceId);

            if (drivers.isEmpty()) {
                showAlert("Info", "There are no drivers for this race.", Alert.AlertType.INFORMATION);
                return;
            }

            Collections.shuffle(drivers);

            ObservableList<Driver> observableDrivers = FXCollections.observableArrayList(drivers);
            gridTableView.setItems(observableDrivers);

            StartingGrid startingGrid = new StartingGrid();
            startingGrid.setRaceId(raceId);

            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                GridPosition gridPosition = new GridPosition(i + 1, driver.getDni(), driver.getTeam().getId());
                startingGrid.addGridPosition(gridPosition);
            }

            startingGridDAO.save(startingGrid);

            showAlert("Success", "Grid generated and saved successfully.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid race ID. Please enter a valid number.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Error", "An error occurred while saving the grid: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void exportGridToFile() {
        if (gridTableView.getItems().isEmpty()) {
            showAlert("Error", "", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(" ", "*.txt"));

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
            Parent root = loader.load();

            ShowTeamsController controller = loader.getController();
            controller.loadTeams(selectedCarRace);

            Stage stage = (Stage) somePane.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, " ", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToSelectedRace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectedRace.fxml"));
            Parent root = loader.load();

            SelectedRaceController controller = loader.getController();
            controller.initialize(selectedCarRace);

            Stage stage = (Stage) somePane.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException(" ", e);
        }
    }
    @FXML
    private void switchToPictures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pictures.fxml"));
            Parent root = loader.load();

            PicturesController controller = loader.getController();
            controller.loadPictures(selectedCarRace);

            Stage stage = (Stage) somePane.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException(" ", e);
        } catch (SQLException e) {
            throw new RuntimeException(" ", e);
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
