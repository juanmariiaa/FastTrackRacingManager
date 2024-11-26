package org.juanmariiaa.model.DAO;

import org.juanmariiaa.model.connection.ConnectionMariaDB;
import org.juanmariiaa.model.domain.User;

import java.sql.*;

public class UserDAO {
    private final static String FINDBYDNI = "SELECT * FROM user WHERE dni=?";
    private final static String FINDBYUSERNAME = "SELECT * FROM user WHERE username=?";
    private final static String INSERT = "INSERT INTO user (dni, name, surname, username, password) VALUES (?, ?, ?, ?, ?)";
    private final static String LOGIN_VALIDATION = "SELECT id FROM user WHERE username = ? AND password = ?";



    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public UserDAO() {
        this.conn = ConnectionMariaDB.getConnection();
    }



    public User findByUsername(String username) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(FINDBYUSERNAME)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setDni(resultSet.getString("dni"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setUser(resultSet.getString("username"));
                return user;
            }
        }
        return null;
    }


    public User findByDni(String dni) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(FINDBYDNI)) {
            statement.setString(1, dni);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setDni(resultSet.getString("dni"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setUser(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        }
        return null;
    }


    public User save(User user) throws SQLException {
        if (user == null) {
            return null;
        }
        try (PreparedStatement statement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getDni());
            statement.setString(2, user.getName());
            statement.setString(3, user.getSurname());
            statement.setString(4, user.getUser());
            statement.setString(5, user.getPassword());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setDni(rs.getString(1));
                }
            }
        }
        return user;
    }

    public String validateLogin(String username, String password) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(LOGIN_VALIDATION)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return rs.getString("id");
            }
        }
        return null;
    }

}
