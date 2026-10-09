package com.meghu.servlet;

import com.meghu.dao.UserDAO;
import com.meghu.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email == null || password == null || email.length() > 190
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            request.setAttribute("error", "Enter your email and password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        try {
            User user = userDAO.authenticate(email.trim().toLowerCase(Locale.ROOT), password);
            if (user == null) {
                request.setAttribute("error", "The email or password is incorrect.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            HttpSession previous = request.getSession(false);
            if (previous != null) {
                previous.invalidate();
            }
            request.getSession(true).setAttribute("user", user);
            String destination = "admin".equals(user.getRole()) ? "/admin/dashboard"
                    : "driver".equals(user.getRole()) ? "/driver/dashboard" : "/cars";
            response.sendRedirect(request.getContextPath() + destination);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate user", exception);
            throw new ServletException("Unable to sign in right now.", exception);
        }
    }
}
