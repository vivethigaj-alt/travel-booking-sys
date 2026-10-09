package com.meghu.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Booking {
    private final long id;
    private final long userId;
    private final long carId;
    private final Long driverId;
    private final Date startDate;
    private final Date endDate;
    private final String pickupLocation;
    private final String destination;
    private final int passengers;
    private final BigDecimal totalPrice;
    private final String status;
    private final String carName;
    private final String customerName;
    private final String driverName;

    public Booking(long id, long userId, long carId, Long driverId, Date startDate, Date endDate,
                   String pickupLocation, String destination, int passengers, BigDecimal totalPrice,
                   String status, String carName, String customerName, String driverName) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.driverId = driverId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.passengers = passengers;
        this.totalPrice = totalPrice;
        this.status = status;
        this.carName = carName;
        this.customerName = customerName;
        this.driverName = driverName;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public long getCarId() { return carId; }
    public Long getDriverId() { return driverId; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestination() { return destination; }
    public int getPassengers() { return passengers; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getCarName() { return carName; }
    public String getCustomerName() { return customerName; }
    public String getDriverName() { return driverName; }
}
