package com.meghu.servlet;

import com.meghu.dao.BookingDAO;
import com.meghu.dao.CarDAO;
import com.meghu.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(BookingServlet.class.getName());
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if ("/my-bookings".equals(request.getServletPath())) {
                User user = (User) request.getSession(false).getAttribute("user");
                request.setAttribute("bookings", bookingDAO.findForCustomer(user.getId()));
                request.setAttribute("notice", request.getSession().getAttribute("notice"));
                request.getSession().removeAttribute("notice");
                request.getRequestDispatcher("/my-bookings.jsp").forward(request, response);
                return;
            }
            request.setAttribute("cars", carDAO.findActive());
            request.setAttribute("selectedCarId", request.getParameter("carId"));
            request.setAttribute("minDate", LocalDate.now().toString());
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load booking information", exception);
            throw new ServletException("Unable to load booking information.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/my-bookings".equals(request.getServletPath())) {
            cancel(request, response);
            return;
        }
        User user = (User) request.getSession(false).getAttribute("user");
        try {
            long carId = Long.parseLong(request.getParameter("carId"));
            LocalDate start = LocalDate.parse(request.getParameter("startDate"));
            LocalDate end = LocalDate.parse(request.getParameter("endDate"));
            int passengers = Integer.parseInt(request.getParameter("passengers"));
            String pickup = clean(request.getParameter("pickup"));
            String destination = clean(request.getParameter("destination"));
            if (start.isBefore(LocalDate.now()) || end.isBefore(start) || passengers < 1 || passengers > 50
                    || pickup.length() < 2 || pickup.length() > 180
                    || destination.length() < 2 || destination.length() > 180) {
                throw new IllegalArgumentException("Check your dates, passenger count, pickup, and destination.");
            }
            long id = bookingDAO.create(user.getId(), carId, Date.valueOf(start), Date.valueOf(end),
                    pickup, destination, passengers);
            response.sendRedirect(request.getContextPath() + "/my-bookings?created=" + id);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            request.setAttribute("selectedCarId", request.getParameter("carId"));
            request.setAttribute("minDate", LocalDate.now().toString());
            try {
                request.setAttribute("cars", carDAO.findActive());
            } catch (SQLException sqlException) {
                LOGGER.log(Level.SEVERE, "Unable to reload vehicles after booking validation failure", sqlException);
                throw new ServletException("Unable to reload booking form.", sqlException);
            }
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to create booking", exception);
            throw new ServletException("Unable to create booking.", exception);
        } catch (RuntimeException exception) {
            request.setAttribute("error", "Please check the booking form and enter valid dates and values.");
            try {
                request.setAttribute("cars", carDAO.findActive());
            } catch (SQLException sqlException) {
                LOGGER.log(Level.SEVERE, "Unable to reload booking form", sqlException);
                throw new ServletException("Unable to reload booking form.", sqlException);
            }
            request.setAttribute("minDate", LocalDate.now().toString());
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
        }
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        User user = (User) request.getSession(false).getAttribute("user");
        try {
            long bookingId = Long.parseLong(request.getParameter("bookingId"));
            if (!bookingDAO.cancel(bookingId, user.getId())) {
                request.getSession().setAttribute("notice", "Only pending bookings can be cancelled.");
            } else {
                request.getSession().setAttribute("notice", "Your booking was cancelled.");
            }
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID.");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to cancel booking", exception);
            throw new ServletException("Unable to cancel booking.", exception);
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
