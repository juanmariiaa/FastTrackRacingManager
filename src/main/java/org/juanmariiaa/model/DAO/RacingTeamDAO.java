package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionMariaDB;
import org.juanmariiaa.model.domain.RacingTeam;
import org.juanmariiaa.model.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacingTeamDAO {

    private final static String FINDALL = "SELECT * FROM racingTeam WHERE id_user=?";
    private final static String FINDBYID = "SELECT * FROM racingTeam WHERE id=?";
    private final static String FINDBYNAME = "SELECT * FROM racingTeam WHERE name LIKE ?";
    private final static String INSERT = "INSERT INTO racingTeam (id, name, city, institution, id_user, logo) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String UPDATE = "UPDATE racingTeam SET name=?, city=?, institution=? WHERE id=?";
    private final static String DELETE = "DELETE FROM racingTeam WHERE id=?";
    private final static String FIND_TEAMS_BY_RACE = "SELECT t.id, t.name, t.city, t.institution " +
            "FROM racingTeam t " +
            "JOIN participation p ON t.id = p.id_racingTeam " +
            "WHERE p.id_carRace = ?";




    CarRaceDAO carRaceDAO = new CarRaceDAO();

    private Connection conn;

    public RacingTeamDAO(Connection conn) {
        this.conn = conn;
    }

    public RacingTeamDAO() {
        this.conn = ConnectionMariaDB.getConnection();
    }

    public List<RacingTeam> findAll(int userId) throws SQLException {
        List<RacingTeam> result = new ArrayList<>();
        try (PreparedStatement pst = this.conn.prepareStatement(FINDALL)) {
            pst.setInt(1, userId);
            try (ResultSet res = pst.executeQuery()) {
                while (res.next()) {
                    RacingTeam racingTeam = teamEager(res);
                    result.add(racingTeam);
                }
            }
        }
        return result;
    }


    public RacingTeam findById(int id) throws SQLException {
        RacingTeam racingTeam = null;
        try (PreparedStatement pst = this.conn.prepareStatement(FINDBYID)) {
            pst.setInt(1, id);
            try (ResultSet res = pst.executeQuery()) {
                if (res.next()) {
                    racingTeam = new RacingTeam();
                    racingTeam.setId(res.getInt("id"));
                    racingTeam.setName(res.getString("name"));
                    racingTeam.setCity(res.getString("city"));
                    racingTeam.setInstitution(res.getString("institution"));
                }
            }
        }
        return racingTeam;
    }

    public List<RacingTeam> findByName(String name) throws SQLException {
        List<RacingTeam> racingTeams = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(FINDBYNAME)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RacingTeam racingTeam = new RacingTeam();
                    racingTeam.setId(resultSet.getInt("id"));
                    racingTeam.setName(resultSet.getString("name"));
                    racingTeam.setCity(resultSet.getString("city"));
                    racingTeam.setInstitution(resultSet.getString("institution"));
                    racingTeams.add(racingTeam);
                }
            }
        }
        return racingTeams;
    }



    public RacingTeam findOneByName(String name) throws SQLException {
        RacingTeam racingTeam = null;
        try (PreparedStatement statement = conn.prepareStatement(FINDBYNAME)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    racingTeam = new RacingTeam();
                    racingTeam.setId(resultSet.getInt("id"));
                    racingTeam.setName(resultSet.getString("name"));
                    racingTeam.setCity(resultSet.getString("city"));
                    racingTeam.setInstitution(resultSet.getString("institution"));

                    if (resultSet.next()) {
                        System.out.println("Warning: Multiple teams found with name: " + name);
                    }
                }
            }
        }
        return racingTeam;
    }





    public RacingTeam save(User user, RacingTeam racingTeam, int raceId) throws SQLException {
        if (racingTeam.getId() == 0) {
            try (PreparedStatement pst = this.conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
                pst.setNull(1, Types.INTEGER);
                pst.setString(2, racingTeam.getName());
                pst.setString(3, racingTeam.getCity());
                pst.setString(4, racingTeam.getInstitution());
                pst.setInt(5, user.getId());
                pst.setBytes(6, racingTeam.getImageData());

                pst.executeUpdate();
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        racingTeam.setId(rs.getInt(1));
                    }
                }
            }
        }
        return racingTeam;
    }


    public RacingTeam update(RacingTeam racingTeam) {
        try (PreparedStatement statement = conn.prepareStatement(UPDATE)) {
            statement.setString(1, racingTeam.getName());
            statement.setString(2, racingTeam.getCity());
            statement.setString(3, racingTeam.getInstitution());
            statement.setInt(4, racingTeam.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return racingTeam;
    }





    public void delete(RacingTeam entity) throws SQLException {
        if (entity != null && entity.getId() != 0) {
            try (PreparedStatement pst = this.conn.prepareStatement(DELETE)) {
                pst.setInt(1, entity.getId());
                pst.executeUpdate();
            }
        }
    }




    public List<RacingTeam> findTeamsByRace(int raceId) {
        List<RacingTeam> racingTeams = new ArrayList<>();
        try (PreparedStatement statement = this.conn.prepareStatement(FIND_TEAMS_BY_RACE)) {
            statement.setInt(1, raceId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    RacingTeam racingTeam = teamEager(rs);
                    racingTeams.add(racingTeam);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return racingTeams;
    }

    private RacingTeam teamEager(ResultSet res) throws SQLException {
        RacingTeam racingTeam = new RacingTeam();
        racingTeam.setId(res.getInt("id"));
        racingTeam.setName(res.getString("name"));
        racingTeam.setCity(res.getString("city"));
        racingTeam.setInstitution(res.getString("institution"));
        return racingTeam;
    }



    public static RacingTeamDAO build(){
        return new RacingTeamDAO();
    }


}