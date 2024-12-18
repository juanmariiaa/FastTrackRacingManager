package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.model.enums.Gender;
import org.juanmariiaa.model.enums.Role;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class CreateDriverController implements Initializable {
    @FXML
    private TextField dniField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField ageField;
    @FXML
    ComboBox<Role> cbRole = new ComboBox<>();
    @FXML
    ComboBox<Gender> cbGender = new ComboBox<>();
    private User currentUser;
    private RacingTeam selectedRacingTeam;
    private DriverDAO driverDAO;

    public void setSelectedTeam(RacingTeam selectedRacingTeam) {
        this.selectedRacingTeam = selectedRacingTeam;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = SingletonUserSession.getCurrentUser();
        cbRole.setItems(FXCollections.observableArrayList(Role.values()));
        cbGender.setItems(FXCollections.observableArrayList(Gender.values()));
    }

    @FXML
    private void createParticipant() {
        String DNI = dniField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        int age = 0;

        // Verificar si el DNI es un número válido
        try {
            age = Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            Utils.showPopUp("Error", null, "Please enter a valid age.", Alert.AlertType.ERROR);
            return;
        }

        Role role = cbRole.getValue();
        Gender gender = cbGender.getValue();

        // Validación de los campos
        if (DNI.isEmpty() || name.isEmpty() || surname.isEmpty() || !isValidDNI(DNI) || age <= 0 || role == null || gender == null) {
            Utils.showPopUp("Error", null, "Please fill in all fields correctly.", Alert.AlertType.ERROR);
            return;
        }

        // Verificar si el DNI ya existe en la base de datos
        driverDAO = new DriverDAO();
        if (driverDAO.dniExists(DNI)) {
            Utils.showPopUp("Error", null, "A participant with this DNI already exists.", Alert.AlertType.ERROR);
            return;
        }

        // Crear el nuevo conductor
        Driver newDriver = new Driver();
        newDriver.setDni(DNI);
        newDriver.setName(name);
        newDriver.setSurname(surname);
        newDriver.setAge(age);
        newDriver.setRole(role);
        newDriver.setGender(gender);
        newDriver.setTeam(selectedRacingTeam);

        // Guardar el nuevo participante en la base de datos
        driverDAO.save(currentUser, newDriver);
        Utils.showPopUp("Success", null, "Participant created successfully!", Alert.AlertType.INFORMATION);
        closeWindow();
    }

    private boolean isValidDNI(String dni) {
        return dni.matches("^\\d{8}[a-zA-Z]$");
    }


    private void closeWindow() {
        Stage stage = (Stage) dniField.getScene().getWindow();
        stage.close();
    }
}
