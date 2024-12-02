package org.juanmariiaa.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.juanmariiaa.model.DAO.RacingTeamDAO;
import org.juanmariiaa.model.domain.RacingTeam;

import java.sql.SQLException;
import java.util.List;

public class allMostParticipationController {


    @FXML
    private TableView<RacingTeam> teamTable;

    @FXML
    private TableColumn<RacingTeam, String> nameColumn;

    @FXML
    private TableColumn<RacingTeam, Integer> participationsColumn;

    private ObservableList<RacingTeam> racingTeams;

    private RacingTeamDAO racingTeamDAO;

    @FXML
    public void initialize() {
        racingTeamDAO = new RacingTeamDAO();
        racingTeams = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        participationsColumn.setCellValueFactory(new PropertyValueFactory<>("participations"));

        // Cargar datos en la tabla
        loadTeamsWithMostParticipations(10); // Cargar los 10 equipos con más participaciones
    }

    private void loadTeamsWithMostParticipations(int limit) {
        try {
            List<RacingTeam> teams = racingTeamDAO.findTeamsWithMostParticipations(limit);
            racingTeams.setAll(teams);
            teamTable.setItems(racingTeams);
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores: mostrar mensaje al usuario
        }
    }
}
