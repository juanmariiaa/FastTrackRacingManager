package org.juanmariiaa.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.juanmariiaa.model.DAO.PictureDAO;
import org.juanmariiaa.model.domain.Picture;
import org.juanmariiaa.model.domain.CarRace;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

import static javafx.scene.layout.TilePane.setMargin;

public class PicturesTournamentController {

    @FXML
    private TilePane picturesTilePane;

    private CarRace selectedCarRace;
    private PictureDAO pictureDAO = new PictureDAO();
    private Picture selectedPicture;

    public void loadPictures(CarRace carRace) throws SQLException {
        this.selectedCarRace = carRace;
        picturesTilePane.getChildren().clear(); // Clear existing pictures
        List<Picture> pictures = pictureDAO.getPicturesByRaceId(carRace.getId());

        for (Picture picture : pictures) {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(picture.getImageData())));
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            imageView.setOnMouseClicked(event -> handlePictureSelection(picture));
            setMargin(imageView, new Insets(5)); // Add margin around each image

            picturesTilePane.getChildren().add(imageView);
        }
    }

    @FXML
    public void handleAddPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] imageData = Files.readAllBytes(selectedFile.toPath());
                Picture picture = new Picture(0, imageData);
                pictureDAO.addPicture(picture, selectedCarRace.getId());
                loadPictures(selectedCarRace);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePictureSelection(Picture picture) {
        selectedPicture = picture;
    }

    @FXML
    public void handleViewButton() {
        if (selectedPicture != null) {
            showLargePictureView(selectedPicture);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Picture Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a picture to view.");
            alert.showAndWait();
        }
    }

    private void showLargePictureView(Picture picture) {
        Stage stage = new Stage();
        // Create an ImageView with the actual image size
        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(picture.getImageData())));
        imageView.setPreserveRatio(true); // Maintain aspect ratio
        // Create a BorderPane to center the ImageView
        BorderPane borderPane = new BorderPane(imageView);
        // Create a Scene with the BorderPane
        Scene scene = new Scene(borderPane);
        // Set the stage title
        stage.setTitle("Selected Picture");
        // Set the scene and show the stage
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void handleDeletePicture() {
        if (selectedPicture != null) {
            try {
                pictureDAO.deletePicture(selectedPicture.getId());
                loadPictures(selectedCarRace);
                selectedPicture = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Picture Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a picture to delete.");
            alert.showAndWait();
        }
    }
}
