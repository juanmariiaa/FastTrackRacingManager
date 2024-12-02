package org.juanmariiaa.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.DAO.CarRaceDAO;
import org.juanmariiaa.model.domain.CarRace;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class CreateTeamController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField institutionField;
    @FXML
    private ImageView logoImageView;

    private CarRace selectedCarRace;
    private CarRaceDAO carRaceDAO;
    private RacingTeamDAO racingTeamDAO;
    private User currentUser;
    private byte[] logoImageData;

    public CreateTeamController() {
        this.carRaceDAO = new CarRaceDAO();
    }

    public void setSelectedTournament(CarRace selectedCarRace) {
        this.selectedCarRace = selectedCarRace;
    }

    public void setTeamDAO(RacingTeamDAO racingTeamDAO) {
        this.racingTeamDAO = racingTeamDAO;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = SingletonUserSession.getCurrentUser();
    }

    @FXML
    private void handleExamineButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Team Logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) nameField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int readNum;
                while ((readNum = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, readNum);
                }
                logoImageData = bos.toByteArray();
                Image image = new Image(new FileInputStream(selectedFile));
                logoImageView.setImage(image);
            } catch (IOException e) {
                Utils.showPopUp("Error", null, "Failed to load image.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void createTeam() {
        try {
            String name = nameField.getText();
            String city = cityField.getText();
            String institution = institutionField.getText();

            if (name.isEmpty() || city.isEmpty() || institution.isEmpty() || selectedCarRace == null) {
                Utils.showPopUp("Error", null, "Please fill in all the fields and select a tournament.", Alert.AlertType.ERROR);
                return;
            }

            RacingTeam newRacingTeam = new RacingTeam();
            newRacingTeam.setName(name);
            newRacingTeam.setCity(city);
            newRacingTeam.setInstitution(institution);
            newRacingTeam.setImageData(logoImageData);

            if (selectedCarRace == null) {
                Utils.showPopUp("Error", null, "Please select a tournament.", Alert.AlertType.ERROR);
                return;
            }

            if (racingTeamDAO == null) {
                Utils.showPopUp("Error", null, "TeamDAO is not initialized.", Alert.AlertType.ERROR);
                return;
            }

            racingTeamDAO.save(currentUser, newRacingTeam, selectedCarRace.getId());

            carRaceDAO.addTeamToRace(selectedCarRace.getId(), newRacingTeam.getId());

            nameField.clear();
            cityField.clear();
            institutionField.clear();
            logoImageView.setImage(null); // Clear the ImageView
            logoImageData = null; // Reset the image data
            closeWindow();

            Utils.showPopUp("Success", null, "Team created successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            Utils.showPopUp("Error", null, "An error occurred while creating the team.", Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}