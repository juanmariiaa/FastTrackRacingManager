package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionMariaDB;
import org.juanmariiaa.model.domain.Driver;
import org.juanmariiaa.model.domain.User;
import org.juanmariiaa.model.enums.Gender;
import org.juanmariiaa.model.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private final static String FINDALL = "SELECT * FROM driver WHERE id_user=?";
    private final static String INSERT = "INSERT INTO driver (dni, role, gender, first_name, surname, age, id_racingTeam, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String UPDATE = "UPDATE driver SET role=?, gender=?, first_name=?, surname=?, age=?, id_racingTeam=? WHERE dni=?";
    private final static String DELETE = "DELETE FROM driver WHERE dni=?";
    private final static String FIND_DRIVER_BY_TEAM = "SELECT * FROM driver WHERE id_racingTeam = ?";

    private Connection conn;

    public DriverDAO(Connection conn) {
        this.conn = conn;
    }

    public DriverDAO() {
        this.conn = ConnectionMariaDB.getConnection();
    }

    public List<Driver> findAll(int userId) {
        List<Driver> result = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(FINDALL)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Driver driver = driverEager(resultSet);
                    result.add(driver);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public Driver save(User user, Driver driver) {
        try (PreparedStatement statement = conn.prepareStatement(INSERT)) {
            statement.setString(1, driver.getDni());
            statement.setString(2, driver.getRole().toString());
            statement.setString(3, driver.getGender().toString());
            statement.setString(4, driver.getName());
            statement.setString(5, driver.getSurname());
            statement.setInt(6, driver.getAge());
            statement.setInt(7, driver.getTeam().getId());
            statement.setInt(8, user.getId());
            statement.executeUpdate();

            return driver;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Driver update(Driver driver) {
        try (PreparedStatement statement = conn.prepareStatement(UPDATE)) {
            statement.setString(1, driver.getRole().toString());
            statement.setString(2, driver.getGender().toString());
            statement.setString(3, driver.getName());
            statement.setString(4, driver.getSurname());
            statement.setInt(5, driver.getAge());
            statement.setInt(6, driver.getTeam().getId());
            statement.setString(7, driver.getDni());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driver;
    }


    public List<Driver> findDriversByTeam(int teamId) {
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(FIND_DRIVER_BY_TEAM)) {
            statement.setInt(1, teamId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Driver driver = driverEager(resultSet);
                    drivers.add(driver);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }


    public void delete(String dni) {
        try (PreparedStatement statement = conn.prepareStatement(DELETE)) {
            statement.setString(1, dni);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Driver driverEager(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setDni(resultSet.getString("dni"));
        driver.setRole(Role.valueOf(resultSet.getString("role")));
        driver.setGender(Gender.valueOf(resultSet.getString("gender")));
        driver.setName(resultSet.getString("first_name"));
        driver.setSurname(resultSet.getString("surname"));
        driver.setAge(resultSet.getInt("age"));
        driver.setTeam(RacingTeamDAO.build().findById(resultSet.getInt("id_racingTeam")));
        return driver;
    }


    public static DriverDAO build() {
        return new DriverDAO();
    }
}