package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionManager;
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
    private static final String INSERT =
            "INSERT INTO startinggrid (id_carRace, position, dni_driver, id_racingTeam)\n" +
                    "VALUES (?, ?, ?, ?)\n" +
                    "ON DUPLICATE KEY UPDATE dni_driver = VALUES(dni_driver), id_racingTeam = VALUES(id_racingTeam);\n";
    private static final String DELETE_BY_RACE_ID = "DELETE FROM startingGrid WHERE id_carRace = ?";
    private static final String FINDGRIDBYRACEID = "SELECT * FROM startingGrid WHERE id_carRace = ? ORDER BY position";


    private Connection conn;

    public StartingGridDAO(Connection conn) {
        this.conn = conn;
    }

    public StartingGridDAO() {
        this.conn = ConnectionManager.getConnection();
    }

    public void save(StartingGrid startingGrid) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            for (GridPosition position : startingGrid.getGridPositions()) {
                stmt.setInt(1, startingGrid.getRaceId());
                stmt.setInt(2, position.getPosition());
                stmt.setString(3, position.getDriverDni());
                stmt.setInt(4, position.getRacingTeamId());

                stmt.executeUpdate();
            }
        }
    }





    public StartingGrid findGridByRaceId(int raceId) throws SQLException {
        List<GridPosition> gridPositions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(FINDGRIDBYRACEID)) {
            stmt.setInt(1, raceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int position = rs.getInt("position");
                String driverDni = rs.getString("dni_driver");
                int racingTeamId = rs.getInt("id_racingTeam");

                GridPosition gridPosition = new GridPosition(position, driverDni, racingTeamId);
                gridPositions.add(gridPosition);
            }

            if (!gridPositions.isEmpty()) {
                return new StartingGrid(raceId, gridPositions);
            }
        }
        return null;
    }






    public void deleteByRaceId(int raceId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BY_RACE_ID)) {
            stmt.setInt(1, raceId);
            stmt.executeUpdate();
        }
    }
}
