package com.meghu.servlet;

import com.meghu.dao.CarDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CarServlet.class.getName());
    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("cars", carDAO.findActive());
            request.getRequestDispatcher("/cars.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load active vehicles", exception);
            throw new ServletException("Unable to load vehicles.", exception);
        }
    }
}
