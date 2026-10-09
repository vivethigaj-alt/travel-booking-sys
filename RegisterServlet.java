package com.meghu.servlet;

import com.meghu.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = value(request, "fullName").trim();
        String email = value(request, "email").trim().toLowerCase(Locale.ROOT);
        String phone = value(request, "phone").trim();
        String password = value(request, "password");
        if (fullName.length() < 2 || fullName.length() > 100 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                || phone.length() < 7 || phone.length() > 30 || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            request.setAttribute("error", "Please enter a valid name, email, and phone number. Passwords must use 8–72 UTF-8 bytes.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        try {
            userDAO.register(fullName, email, password, phone, "customer");
            response.sendRedirect(request.getContextPath() + "/login.html?registered=1");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to register customer", exception);
            if ("23000".equals(exception.getSQLState())) {
                request.setAttribute("error", "An account with this email already exists.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            throw new ServletException("Unable to complete registration.", exception);
        }
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }
}
