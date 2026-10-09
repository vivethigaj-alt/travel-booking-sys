package com.meghu.dao;

import com.meghu.config.DBConnection;
import com.meghu.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public boolean register(String fullName, String email, String password, String phone, String role)
            throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            statement.setString(4, phone);
            statement.setString(5, role);
            return statement.executeUpdate() == 1;
        }
    }

    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, full_name, email, password_hash, phone, role FROM users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !BCrypt.checkpw(password, result.getString("password_hash"))) {
                    return null;
                }
                return new User(result.getLong("id"), result.getString("full_name"),
                        result.getString("email"), result.getString("phone"), result.getString("role"));
            }
        }
    }

    public List<User> findDrivers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, full_name, email, phone, role FROM users WHERE role = 'driver' ORDER BY full_name");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                users.add(new User(result.getLong("id"), result.getString("full_name"),
                        result.getString("email"), result.getString("phone"), result.getString("role")));
            }
        }
        return users;
    }
}
