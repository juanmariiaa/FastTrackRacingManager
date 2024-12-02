package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionMariaDB;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.GridPosition;
import org.juanmariiaa.model.domain.StartingGrid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartingGridDAO {
    private static final String INSERT = "INSERT INTO startingGrid (id_carRace, position, dni_driver, id_racingTeam) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_RACE_ID = "SELECT * FROM startingGrid WHERE id_carRace = ?";
    private static final String DELETE_BY_RACE_ID = "DELETE FROM startingGrid WHERE id_carRace = ?";

    private Connection conn;

    public StartingGridDAO(Connection conn) {
        this.conn = conn;
    }

    public StartingGridDAO() {
        this.conn = ConnectionMariaDB.getConnection();
    }

    public void save(StartingGrid startingGrid) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            for (GridPosition position : startingGrid.getGridPositions()) {
                stmt.setInt(1, startingGrid.getRaceId());
                stmt.setInt(2, position.getPosition());
                stmt.setString(3, position.getDriverDni());
                stmt.setInt(4, position.getRacingTeamId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public StartingGrid findByRaceId(int raceId) throws SQLException {
        List<GridPosition> positions = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_RACE_ID)) {
            stmt.setInt(1, raceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    positions.add(new GridPosition(
                            rs.getInt("position"),
                            rs.getString("dni_driver"),
                            rs.getInt("id_racingTeam")
                    ));
                }
            }
        }
        return new StartingGrid(raceId, positions);
    }

    public void createRandomStartingGrid(int raceId) throws SQLException {
        List<GridPosition> gridPositions = new ArrayList<>();
        DriverDAO driverDAO = new DriverDAO(conn);

        // Obtener todos los drivers asociados a la carrera
        List<Driver> drivers = driverDAO.findDriversByRaceId(raceId);

        // Mezclar la lista de drivers aleatoriamente
        Collections.shuffle(drivers);

        // Asignar posiciones
        int position = 1;
        for (Driver driver : drivers) {
            gridPositions.add(new GridPosition(position++, driver.getDni(), driver.getIdRacingTeam()));
        }

        // Crear la StartingGrid y guardarla
        StartingGrid startingGrid = new StartingGrid(raceId, gridPositions);
        save(startingGrid);
    }

    public void deleteByRaceId(int raceId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BY_RACE_ID)) {
            stmt.setInt(1, raceId);
            stmt.executeUpdate();
        }
    }
}
