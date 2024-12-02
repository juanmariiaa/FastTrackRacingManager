package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.domain.RacingTeam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamRaceDAO {
    private final static String FIND_BY_RACE_ID = "SELECT racingTeam.id, racingTeam.name, racingTeam.city, racingTeam.institution\n" +
            "FROM racingTeam\n" +
            "INNER JOIN participation ON racingTeam.id = participation.id_racingTeam\n" +
            "WHERE participation.id_racingTeam = ?;";


    private Connection conn;

    public TeamRaceDAO(Connection conn) {
        this.conn = conn;
    }



    public List<RacingTeam> findTeamsByRaceId(int tournamentId) throws SQLException {
        List<RacingTeam> result = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(FIND_BY_RACE_ID)) {
            statement.setInt(1, tournamentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RacingTeam racingTeam = new RacingTeam();
                    racingTeam.setId(resultSet.getInt("id"));
                    racingTeam.setName(resultSet.getString("name"));
                    racingTeam.setCity(resultSet.getString("city"));
                    racingTeam.setInstitution(resultSet.getString("institution"));
                    result.add(racingTeam);
                }
            }
        }
        return result;
    }
}
