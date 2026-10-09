package com.meghu.model;

import java.math.BigDecimal;

public class Car {
    private final long id;
    private final String name;
    private final String category;
    private final int seats;
    private final BigDecimal pricePerDay;
    private final String imageUrl;
    private final String description;
    private final boolean active;

    public Car(long id, String name, String category, int seats, BigDecimal pricePerDay,
               String imageUrl, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.seats = seats;
        this.pricePerDay = pricePerDay;
        this.imageUrl = imageUrl;
        this.description = description;
        this.active = active;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getSeats() { return seats; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
}
