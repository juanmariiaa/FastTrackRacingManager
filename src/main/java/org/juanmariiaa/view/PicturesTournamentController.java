package org.juanmariiaa.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
    @FXML
    private Pane somePane;

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
    @FXML
    public void switchToShowTeams() {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("showTeams.fxml"));
            Parent root = loader.load(); // Carga la interfaz de "showTeams.fxml"

            // Obtener el controlador y pasarle los datos necesarios
            ShowTeamsController controller = loader.getController();
            controller.loadTeams(selectedCarRace); // Asegúrate de que `selectedCarRace` esté correctamente inicializado

            // Obtener la referencia a la ventana actual (Stage)
            Stage stage = (Stage) somePane.getScene().getWindow(); // Reemplaza `somePane` con el nodo adecuado, si es necesario
            stage.getScene().setRoot(root); // Cambiar el contenido de la escena principal

        } catch (IOException e) {
            // Imprimir el error para el seguimiento
            e.printStackTrace();
            // O también podrías mostrar un mensaje de error al usuario si prefieres
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hubo un error al cargar la vista de los equipos.", ButtonType.OK);
            alert.showAndWait();
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
    private void switchToMyRaces() throws IOException {
        App.setRoot("myRaces");
    }

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }

    @FXML
    private void switchToFinder() throws IOException {
        App.setRoot("finder");
    }


}
