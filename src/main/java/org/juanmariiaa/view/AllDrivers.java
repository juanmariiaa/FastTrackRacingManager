package org.juanmariiaa.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.juanmariiaa.model.DAO.DriverDAO;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.model.enums.Gender;
import org.juanmariiaa.model.enums.Role;
import org.juanmariiaa.others.SingletonUserSession;
import org.juanmariiaa.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;


public class AllDrivers implements Initializable {

    @FXML
    private TableView<Driver> tableView;

    @FXML
    private TableColumn<Driver,String> columnDNI;
    @FXML
    private TableColumn<Driver,String> columnName;
    @FXML
    private TableColumn<Driver,String> columnSurname;
    @FXML
    private TableColumn<Driver,String> columnAge;
    @FXML
    private TableColumn<Driver, Role> columnRole;
    @FXML
    private TableColumn<Driver, Gender> columnGender;
    @FXML
    private TableColumn<Driver,String> columnTeam;
    private ObservableList<Driver> drivers;
    DriverDAO driverDAO = new DriverDAO();
    private User currentUser;





    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SingletonUserSession.getCurrentUser();
        List<Driver> drivers = DriverDAO.build().findAll(currentUser.getId());
        this.drivers = FXCollections.observableArrayList(drivers);

        tableView.setItems(this.drivers);
        tableView.setEditable(true);
        columnDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        columnDNI.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDNI.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setDni(p.getNewValue());
            driverDAO.update(driver);
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
            tableView.refresh();
        });

        columnAge.setCellValueFactory(participant -> new SimpleStringProperty(Integer.toString(participant.getValue().getAge())));
        columnAge.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAge.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setAge(Integer.parseInt(p.getNewValue()));
            driverDAO.update(driver);
            tableView.refresh();
        });
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        columnRole.setCellFactory(ComboBoxTableCell.forTableColumn(Role.values()));
        columnRole.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setRole(p.getNewValue());
            driverDAO.update(driver);
            tableView.refresh();
        });

        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnGender.setCellFactory(ComboBoxTableCell.forTableColumn(Gender.values()));
        columnGender.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            driver.setGender(p.getNewValue());
            driverDAO.update(driver);
            tableView.refresh();
        });

        columnTeam.setCellValueFactory(participant -> {
            RacingTeam racingTeam = participant.getValue().getTeam();
            if (racingTeam != null) {
                return new SimpleStringProperty(racingTeam.getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        try {
            columnTeam.setCellFactory(ComboBoxTableCell.forTableColumn(getTeams()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        columnTeam.setOnEditCommit(p -> {
            Driver driver = p.getRowValue();
            RacingTeam racingTeam = null;
            try {
                racingTeam = (RacingTeam) RacingTeamDAO.build().findOneByName(p.getNewValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            driver.setTeam(racingTeam);
            driverDAO.update(driver);
            tableView.refresh();
        });    }

    private ObservableList<String> getTeams() throws SQLException {
        List<RacingTeam> racingTeams = RacingTeamDAO.build().findAll(currentUser.getId());
        ObservableList<String> teamNames = FXCollections.observableArrayList();
        for (RacingTeam racingTeam : racingTeams) {
            teamNames.add(racingTeam.getName());
        }
        return teamNames;
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
    private void btDelete() throws SQLException {
        deleteSelected();
    }

    @FXML
    private void switchToDriver() throws IOException {
        App.setRoot("allDrivers");
    }
    @FXML
    private void switchToRaces() throws IOException {
        App.setRoot("allRaces");
    }
    @FXML
    private void switchToTeams() throws IOException {
        App.setRoot("allTeams");
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
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }



}