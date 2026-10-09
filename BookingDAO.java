package com.meghu.dao;

import com.meghu.config.DBConnection;
import com.meghu.model.Booking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private static final String SELECT_BOOKINGS = "SELECT b.*, c.name AS car_name, u.full_name AS customer_name, "
            + "d.full_name AS driver_name FROM bookings b JOIN cars c ON c.id = b.car_id "
            + "JOIN users u ON u.id = b.user_id LEFT JOIN users d ON d.id = b.driver_id ";

    public long create(long userId, long carId, Date start, Date end, String pickup, String destination,
                       int passengers) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                BigDecimal dayPrice;
                int seats;
                try (PreparedStatement car = connection.prepareStatement(
                        "SELECT price_per_day, seats FROM cars WHERE id = ? AND active = TRUE FOR UPDATE")) {
                    car.setLong(1, carId);
                    try (ResultSet result = car.executeQuery()) {
                        if (!result.next()) {
                            throw new IllegalArgumentException("That vehicle is no longer available.");
                        }
                        dayPrice = result.getBigDecimal("price_per_day");
                        seats = result.getInt("seats");
                    }
                }
                if (passengers > seats) {
                    throw new IllegalArgumentException("Passenger count exceeds this vehicle's capacity.");
                }
                try (PreparedStatement overlap = connection.prepareStatement(
                        "SELECT id FROM bookings WHERE car_id = ? AND status IN ('pending', 'confirmed') "
                                + "AND start_date <= ? AND end_date >= ? LIMIT 1")) {
                    overlap.setLong(1, carId);
                    overlap.setDate(2, end);
                    overlap.setDate(3, start);
                    try (ResultSet result = overlap.executeQuery()) {
                        if (result.next()) {
                            throw new IllegalArgumentException("This vehicle is already booked for those dates.");
                        }
                    }
                }
                long days = Math.max(1, ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) + 1);
                BigDecimal total = dayPrice.multiply(BigDecimal.valueOf(days));
                String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, pickup_location, "
                        + "destination, passengers, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                long id;
                try (PreparedStatement insert = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insert.setLong(1, userId);
                    insert.setLong(2, carId);
                    insert.setDate(3, start);
                    insert.setDate(4, end);
                    insert.setString(5, pickup);
                    insert.setString(6, destination);
                    insert.setInt(7, passengers);
                    insert.setBigDecimal(8, total);
                    insert.executeUpdate();
                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Booking was saved without an ID.");
                        }
                        id = keys.getLong(1);
                    }
                }
                connection.commit();
                return id;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Booking> findForCustomer(long userId) throws SQLException {
        return find(SELECT_BOOKINGS + "WHERE b.user_id = ? ORDER BY b.created_at DESC", userId, "customer");
    }

    public List<Booking> findAll() throws SQLException {
        return find(SELECT_BOOKINGS + "ORDER BY b.created_at DESC", 0, "all");
    }

    public List<Booking> findForDriver(long driverId) throws SQLException {
        return find(SELECT_BOOKINGS + "WHERE b.driver_id = ? ORDER BY b.start_date", driverId, "driver");
    }

    public boolean cancel(long bookingId, long userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bookings SET status = 'cancelled' WHERE id = ? AND user_id = ? AND status = 'pending'")) {
            statement.setLong(1, bookingId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public void updateStatus(long id, String status) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bookings SET status = ? WHERE id = ? AND status <> 'cancelled'")) {
            statement.setString(1, status);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    public void assignDriver(long bookingId, long driverId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bookings b JOIN users u ON u.id = ? AND u.role = 'driver' "
                             + "SET b.driver_id = u.id WHERE b.id = ? AND b.status IN ('pending', 'confirmed')")) {
            statement.setLong(1, driverId);
            statement.setLong(2, bookingId);
            statement.executeUpdate();
        }
    }

    public boolean updateAssignedStatus(long bookingId, long driverId, String status) throws SQLException {
        String expectedStatus = "confirmed".equals(status) ? "pending" : "confirmed";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bookings SET status = ? WHERE id = ? AND driver_id = ? "
                             + "AND status = ?")) {
            statement.setString(1, status);
            statement.setLong(2, bookingId);
            statement.setLong(3, driverId);
            statement.setString(4, expectedStatus);
            return statement.executeUpdate() == 1;
        }
    }

    private List<Booking> find(String sql, long id, String scope) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!"all".equals(scope)) {
                statement.setLong(1, id);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    long driver = result.getLong("driver_id");
                    Long driverId = result.wasNull() ? null : driver;
                    bookings.add(new Booking(result.getLong("id"), result.getLong("user_id"),
                            result.getLong("car_id"), driverId, result.getDate("start_date"),
                            result.getDate("end_date"), result.getString("pickup_location"),
                            result.getString("destination"), result.getInt("passengers"),
                            result.getBigDecimal("total_price"), result.getString("status"),
                            result.getString("car_name"), result.getString("customer_name"),
                            result.getString("driver_name")));
                }
            }
        }
        return bookings;
    }
}
