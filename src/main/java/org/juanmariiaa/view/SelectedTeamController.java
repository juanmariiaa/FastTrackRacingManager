package org.juanmariiaa.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.enums.Gender;
import org.juanmariiaa.model.enums.Role;
import org.juanmariiaa.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Controller class for displaying and managing details of a selected team.
 * This controller handles updating team details, displaying associated participants
 * and creating new participants for this selected tournament.
 */
public class SelectedTeamController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField institutionField;
    @FXML
    private TableView<Driver> tableView;
    @FXML
    private TableColumn<Driver, String> columnDNI;
    @FXML
    private TableColumn<Driver, String> columnName;
    @FXML
    private TableColumn<Driver, String> columnSurname;
    @FXML
    private TableColumn<Driver, String> columnAge;
    @FXML
    private TableColumn<Driver, Role> columnRole;
    @FXML
    private TableColumn<Driver, Gender> columnGender;
    @FXML
    private ImageView logoImageView; // Add ImageView field

    private RacingTeam selectedRacingTeam;
    private DriverDAO driverDAO;
    private RacingTeamDAO racingTeamDAO;
    private ObservableList<Driver> participantsData;

    public void initialize(RacingTeam selectedRacingTeam) {
        this.selectedRacingTeam = selectedRacingTeam;
        this.racingTeamDAO = new RacingTeamDAO();
        this.driverDAO = new DriverDAO();
        nameField.setText(selectedRacingTeam.getName());
        cityField.setText(selectedRacingTeam.getCity());
        institutionField.setText(selectedRacingTeam.getInstitution());

        // Display the logo
        displayLogo();

        displayParticipants();
    }

    private void displayLogo() {
        if (selectedRacingTeam.getImageData() != null && selectedRacingTeam.getImageData().length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(selectedRacingTeam.getImageData());
            Image logoImage = new Image(bis);
            logoImageView.setImage(logoImage);
        }
    }

    private void displayParticipants() {
        List<Driver> drivers = driverDAO.findDriversByTeam(selectedRacingTeam.getId());
        participantsData = FXCollections.observableArrayList(drivers);

        tableView.setItems(this.participantsData);
        tableView.setEditable(true);
        columnDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        columnDNI.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDNI.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setDni(p.getNewValue());
            driverDAO.update(driver);
            participantsData.set(tableView.getSelectionModel().getSelectedIndex(), driver);
            tableView.refresh();
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setName(p.getNewValue());
            driverDAO.update(driver);
            tableView.refresh();
        });

        columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSurname.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setSurname(p.getNewValue());
            driverDAO.update(driver);
            participantsData.set(tableView.getSelectionModel().getSelectedIndex(), driver);
            tableView.refresh();
        });

        columnAge.setCellValueFactory(participant -> new SimpleStringProperty(Integer.toString(participant.getValue().getAge())));
        columnAge.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAge.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setAge(Integer.parseInt(p.getNewValue()));
            driverDAO.update(driver);
            participantsData.set(tableView.getSelectionModel().getSelectedIndex(), driver);
            tableView.refresh();
        });

        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        columnRole.setCellFactory(ComboBoxTableCell.forTableColumn(Role.values()));
        columnRole.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setRole(p.getNewValue());
            driverDAO.update(driver);
            participantsData.set(tableView.getSelectionModel().getSelectedIndex(), driver);
            tableView.refresh();
        });

        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnGender.setCellFactory(ComboBoxTableCell.forTableColumn(Gender.values()));
        columnGender.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setGender(p.getNewValue());
            driverDAO.update(driver);
            participantsData.set(tableView.getSelectionModel().getSelectedIndex(), driver);
            tableView.refresh();
        });
    }

    @FXML
    private void updateTeam() {
        if (nameField.getText().isEmpty() || cityField.getText().isEmpty() || institutionField.getText().isEmpty()) {
            Utils.showPopUp("ERROR", "Update Failed", "Please fill in all the fields.", Alert.AlertType.ERROR);
            return;
        }

        selectedRacingTeam.setName(nameField.getText());
        selectedRacingTeam.setCity(cityField.getText());
        selectedRacingTeam.setInstitution(institutionField.getText());
        racingTeamDAO.update(selectedRacingTeam);
        Utils.showPopUp("UPDATE", "Team Updated", "Team details updated successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void deleteSelected() {
        Driver selectedP = tableView.getSelectionModel().getSelectedItem();

        if (selectedP != null) {
            tableView.getItems().remove(selectedP);
            driverDAO.delete(selectedP.getDni());
            Utils.showPopUp("DELETE", "Participant deleted", "Participant has been deleted.", Alert.AlertType.INFORMATION);
        } else {
            Utils.showPopUp("ERROR", "No Participant Selected", "Please select a participant to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void switchToCreateParticipant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateParticipant.fxml"));
            Parent root = loader.load();
            CreateParticipantController controller = loader.getController();
            controller.setSelectedTeam(selectedRacingTeam);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Participant");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            displayParticipants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWindow() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}
