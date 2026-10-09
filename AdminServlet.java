package com.meghu.servlet;

import com.meghu.dao.BookingDAO;
import com.meghu.dao.CarDAO;
import com.meghu.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());
    private final CarDAO carDAO = new CarDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String route = route(request);
        try {
            switch (route) {
                case "/":
                case "/dashboard":
                    request.setAttribute("cars", carDAO.findActive().size());
                    request.setAttribute("bookings", bookingDAO.findAll().size());
                    request.setAttribute("drivers", userDAO.findDrivers().size());
                    forward(request, response, "/admin/dashboard.jsp");
                    break;
                case "/cars":
                    request.setAttribute("cars", carDAO.findAll());
                    forward(request, response, "/admin/cars.jsp");
                    break;
                case "/bookings":
                    request.setAttribute("bookings", bookingDAO.findAll());
                    request.setAttribute("drivers", userDAO.findDrivers());
                    forward(request, response, "/admin/bookings.jsp");
                    break;
                case "/drivers":
                    request.setAttribute("drivers", userDAO.findDrivers());
                    forward(request, response, "/admin/drivers.jsp");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load admin page " + route, exception);
            throw new ServletException("Unable to load admin page.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String route = route(request);
        String action = request.getParameter("action");
        try {
            if ("/cars".equals(route)) {
                if ("save".equals(action)) {
                    long id = number(request, "id", 0);
                    String name = required(request, "name", 120);
                    String category = required(request, "category", 60);
                    int seats = (int) number(request, "seats", 0);
                    BigDecimal price = new BigDecimal(required(request, "pricePerDay", 15));
                    if (seats < 1 || seats > 50 || price.signum() <= 0) {
                        throw new IllegalArgumentException("Seats and price must be greater than zero.");
                    }
                    carDAO.save(id, name, category, seats, price,
                            optional(request, "imageUrl", 500), optional(request, "description", 500));
                } else if ("deactivate".equals(action)) {
                    carDAO.deactivate(number(request, "id", -1));
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown vehicle action.");
                    return;
                }
            } else if ("/bookings".equals(route)) {
                long id = number(request, "bookingId", -1);
                if ("status".equals(action)) {
                    String status = request.getParameter("status");
                    if (status == null || !Set.of("pending", "confirmed", "cancelled", "completed").contains(status)) {
                        throw new IllegalArgumentException("Invalid booking status.");
                    }
                    bookingDAO.updateStatus(id, status);
                } else if ("assign".equals(action)) {
                    bookingDAO.assignDriver(id, number(request, "driverId", -1));
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown booking action.");
                    return;
                }
            } else if ("/drivers".equals(route) && "create".equals(action)) {
                String name = required(request, "fullName", 100);
                String email = required(request, "email", 190).toLowerCase(java.util.Locale.ROOT);
                if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    throw new IllegalArgumentException("Enter a valid driver email address.");
                }
                String phone = required(request, "phone", 30);
                String password = required(request, "password", 72);
                if (password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
                    throw new IllegalArgumentException("Driver passwords must use 8–72 UTF-8 bytes.");
                }
                userDAO.register(name, email, password, phone, "driver");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown admin action.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin" + route + "?saved=1");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            doGet(request, response);
        } catch (SQLException exception) {
            if ("/drivers".equals(route) && "create".equals(action)
                    && "23000".equals(exception.getSQLState())) {
                request.setAttribute("error", "A driver account with that email already exists.");
                doGet(request, response);
                return;
            }
            LOGGER.log(Level.SEVERE, "Unable to update admin data", exception);
            throw new ServletException("Unable to save admin changes.", exception);
        }
    }

    private String route(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null ? "/" : path;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        request.getRequestDispatcher(jsp).forward(request, response);
    }

    private long number(HttpServletRequest request, String name, long fallback) {
        String value = request.getParameter(name);
        return value == null || value.isEmpty() ? fallback : Long.parseLong(value);
    }

    private String required(HttpServletRequest request, String name, int max) {
        String value = optional(request, name, max);
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Please fill in all required fields.");
        }
        return value;
    }

    private String optional(HttpServletRequest request, String name, int max) {
        String value = request.getParameter(name);
        value = value == null ? "" : value.trim();
        if (value.length() > max) {
            throw new IllegalArgumentException("One of the values is too long.");
        }
        return value;
    }
}
