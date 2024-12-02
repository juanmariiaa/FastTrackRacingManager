package org.juanmariiaa.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.juanmariiaa.model.DAO.UserDAO;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;


public class RegisterController {

    @FXML
    private TextField tfDni;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfSurname;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField tfPassword;

    UserDAO userDAO = new UserDAO();


    @FXML
    public void btSignUp() {
        addUser();
    }


    @FXML
    private void addUser() {
        App a = new App();
        String dni = tfDni.getText();
        String name = tfName.getText();
        String surname = tfSurname.getText();
        String username = tfUsername.getText();
        String password = tfPassword.getText();

        if (dni.isEmpty() || name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Utils.showPopUp("Error", "Empty fields", "Please complete all fields to continue.", Alert.AlertType.ERROR);
        } else if (!isValidDni(dni)) {
            Utils.showPopUp("Error", "Invalid DNI", null, Alert.AlertType.ERROR);
        } else if (username.length() < 4) {
            Utils.showPopUp("Error", "Invalid username", "Username must be at least 4 characters long.", Alert.AlertType.ERROR);
        } else if (!isValidPassword(password)) {
            Utils.showPopUp("Error", "Invalid password",
                    "Password must be more complex.", Alert.AlertType.ERROR);
        } else {
            try {
                if (userDAO.findByDni(dni) != null) {
                    Utils.showPopUp("Error", "DNI already exists", "Please choose another DNI.", Alert.AlertType.ERROR);
                } else if (userDAO.findByUsername(username) != null) {
                    Utils.showPopUp("Error", "Username already exists", "Please choose another username.", Alert.AlertType.ERROR);
                } else {
                    String hashedPassword = Utils.hashPassword(password);
                    User user = new User(dni, name, surname, username, hashedPassword);
                    userDAO.save(user);

                    Utils.showPopUp("Success", "User created", "User created successfully.", Alert.AlertType.INFORMATION);
                    switchToLogin();
                }
            } catch (SQLException e) {
                Utils.showPopUp("Error", "Database error", "Please try again later.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void switchToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isValidDni(String dni) {
        return dni.matches("^\\d{8}[a-zA-Z]$");
    }


    public static boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[^a-zA-Z0-9].*");
    }
}
