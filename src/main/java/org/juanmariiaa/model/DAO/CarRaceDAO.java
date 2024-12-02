package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionManager;
import org.juanmariiaa.model.connection.ConnectionMariaDB;
import org.juanmariiaa.model.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarRaceDAO {
    private final static String FIND_ALL = "SELECT * FROM carRace WHERE id_user=?";
    private final static String INSERT = "INSERT INTO carRace (name, location, city, date, id_user) VALUES (?, ?, ?, ?, ?)";
    private final static String UPDATE = "UPDATE carRace SET name=?, location=?, city=?, date=? WHERE id=?";
    private final static String DELETE = "DELETE FROM carRace WHERE id=?";
    private final static String ADD_TEAM_TO_CAR_RACE = "INSERT INTO participation (id_carRace, id_racingTeam) VALUES (?, ?)";
    private final static String DELETE_TEAM_FROM_CAR_RACE = "DELETE FROM participation WHERE id_carRace = ? AND id_racingTeam = ?";
    private final static String IS_TEAM_IN_CAR_RACE = "SELECT COUNT(*) FROM participation WHERE id_racingTeam = ? AND id_carRace = ?";

    private Connection conn;
    private TeamRaceDAO teamRaceDAO;

    public CarRaceDAO(Connection conn, TeamRaceDAO teamRaceDAO) {
        this.conn = conn;
        this.teamRaceDAO = teamRaceDAO;
    }

    public CarRaceDAO() {
        this.conn = ConnectionManager.getConnection();
        this.teamRaceDAO = new TeamRaceDAO(conn);
    }


    public List<CarRace> findAll(int userId) throws SQLException {
        List<CarRace> carRaces = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(FIND_ALL)) {
            preparedStatement.setInt(1, userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    CarRace carRace = carRaceEager(resultSet);
                    carRaces.add(carRace);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carRaces;
    }


    public CarRace save(User user, CarRace carRace) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, carRace.getName());
            statement.setString(2, carRace.getLocation());
            statement.setString(3, carRace.getCity());
            statement.setString(4, carRace.getDate());
            statement.setInt(5, user.getId());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    carRace.setId(rs.getInt(1));
                }
            }
        }
        return carRace;
    }


    public CarRace update(CarRace carRace) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(UPDATE)) {
            statement.setString(1, carRace.getName());
            statement.setString(2, carRace.getLocation());
            statement.setString(3, carRace.getCity());
            statement.setString(4, carRace.getDate());
            statement.setInt(5, carRace.getId());
            statement.executeUpdate();
        }
        return carRace;
    }


    public void delete(int raceId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(DELETE)) {
            statement.setInt(1, raceId);
            statement.executeUpdate();
        }
    }


    public List<RacingTeam> findTeamsByRaceId(int raceId) throws SQLException {
        return teamRaceDAO.findTeamsByRaceId(raceId);
    }


    public void addTeamToRace(int raceId, int racingTeamId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(ADD_TEAM_TO_CAR_RACE)) {
            statement.setInt(1, raceId);
            statement.setInt(2, racingTeamId);
            statement.executeUpdate();
        }
    }

    public void removeTeamFromRace(int racingTeamId, int raceId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(DELETE_TEAM_FROM_CAR_RACE)) {
            statement.setInt(1, raceId);
            statement.setInt(2, racingTeamId);
            statement.executeUpdate();
        }
    }


    public boolean isTeamInCarRace(int racingTeamId, int raceId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(IS_TEAM_IN_CAR_RACE)) {
            stmt.setInt(1, racingTeamId);
            stmt.setInt(2, raceId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }




    private CarRace carRaceEager(ResultSet resultSet) throws SQLException {
        CarRace carRace = new CarRace();
        carRace.setId(resultSet.getInt("id"));
        carRace.setName(resultSet.getString("name"));
        carRace.setLocation(resultSet.getString("location"));
        carRace.setCity(resultSet.getString("city"));
        carRace.setDate(resultSet.getString("date"));
        carRace.setTeams(findTeamsByRaceId(carRace.getId()));
        return carRace;
    }



    public static CarRaceDAO build(){
        return new CarRaceDAO();
    }

}
