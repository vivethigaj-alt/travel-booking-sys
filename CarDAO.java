package com.meghu.dao;

import com.meghu.config.DBConnection;
import com.meghu.model.Car;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    public List<Car> findActive() throws SQLException {
        return findByActive(true);
    }

    public List<Car> findAll() throws SQLException {
        return findByActive(null);
    }

    public Car findActiveById(long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM cars WHERE id = ? AND active = TRUE")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? map(result) : null;
            }
        }
    }

    public void save(long id, String name, String category, int seats, BigDecimal price,
                     String imageUrl, String description) throws SQLException {
        String sql = id == 0
                ? "INSERT INTO cars (name, category, seats, price_per_day, image_url, description) VALUES (?, ?, ?, ?, ?, ?)"
                : "UPDATE cars SET name = ?, category = ?, seats = ?, price_per_day = ?, image_url = ?, description = ?, active = TRUE WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setInt(3, seats);
            statement.setBigDecimal(4, price);
            statement.setString(5, imageUrl);
            statement.setString(6, description);
            if (id != 0) {
                statement.setLong(7, id);
            }
            statement.executeUpdate();
        }
    }

    public void deactivate(long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE cars SET active = FALSE WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private List<Car> findByActive(Boolean active) throws SQLException {
        String sql = "SELECT * FROM cars" + (active == null ? "" : " WHERE active = TRUE") + " ORDER BY id DESC";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                cars.add(map(result));
            }
        }
        return cars;
    }

    private Car map(ResultSet result) throws SQLException {
        return new Car(result.getLong("id"), result.getString("name"), result.getString("category"),
                result.getInt("seats"), result.getBigDecimal("price_per_day"),
                result.getString("image_url"), result.getString("description"), result.getBoolean("active"));
    }
}
