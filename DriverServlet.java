package com.meghu.servlet;

import com.meghu.dao.BookingDAO;
import com.meghu.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DriverServlet.class.getName());
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = (User) request.getSession(false).getAttribute("user");
            request.setAttribute("bookings", bookingDAO.findForDriver(user.getId()));
            request.getRequestDispatcher("/driver/dashboard.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load driver dashboard", exception);
            throw new ServletException("Unable to load driver dashboard.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("status");
        if (status == null || !Set.of("confirmed", "completed").contains(status)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking status.");
            return;
        }
        try {
            User user = (User) request.getSession(false).getAttribute("user");
            long bookingId = Long.parseLong(request.getParameter("bookingId"));
            if (!bookingDAO.updateAssignedStatus(bookingId, user.getId(), status)) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "This booking cannot be updated.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/driver/dashboard?saved=1");
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID.");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to update driver booking", exception);
            throw new ServletException("Unable to update booking.", exception);
        }
    }
}
