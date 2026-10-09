CREATE DATABASE IF NOT EXISTS meghu_travels
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE meghu_travels;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    role ENUM('customer', 'admin', 'driver') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cars (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(60) NOT NULL,
    seats INT NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    driver_id BIGINT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    pickup_location VARCHAR(180) NOT NULL,
    destination VARCHAR(180) NOT NULL,
    passengers INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'cancelled', 'completed') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_car FOREIGN KEY (car_id) REFERENCES cars(id),
    CONSTRAINT fk_booking_driver FOREIGN KEY (driver_id) REFERENCES users(id),
    INDEX idx_booking_car_dates (car_id, start_date, end_date, status),
    INDEX idx_booking_customer (user_id, created_at),
    INDEX idx_booking_driver (driver_id, start_date)
);

UPDATE cars
SET name = 'Innova Crysta', category = 'MPV', seats = 7,
    description = 'A comfortable, seven-seat MPV for city trips and family holidays.'
WHERE name = 'Meghu Comfort Sedan';

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Innova Crysta', 'MPV', 7, 2200.00,
       'images/sedan.jpg',
       'A comfortable, seven-seat MPV for city trips and family holidays.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Innova Crysta');

UPDATE cars
SET category = 'MPV', seats = 7,
    description = 'A comfortable, seven-seat MPV for city trips and family holidays.'
WHERE name = 'Innova Crysta';

UPDATE cars SET name = 'Crysta', category = 'SUV', seats = 6,
    description = 'A comfortable, six-seat SUV with space for family and luggage.'
WHERE name = 'Meghu Family MPV';

UPDATE cars SET name = 'Crysta', category = 'SUV', seats = 6,
    description = 'A comfortable, six-seat SUV with space for family and luggage.'
WHERE name = 'Meghu Family SUV';

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Crysta', 'SUV', 6, 3800.00,
       'images/traveller.jpg',
       'A comfortable, six-seat SUV with space for family and luggage.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Crysta');

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Meghu Traveller', 'Van', 12, 6200.00,
       'images/suv.jpg',
       'A roomy option for larger groups, celebrations, and outstation journeys.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Meghu Traveller');

UPDATE cars SET image_url = 'images/sedan.jpg' WHERE name = 'Innova Crysta';
UPDATE cars SET image_url = 'images/traveller.jpg' WHERE name = 'Crysta';
UPDATE cars SET image_url = 'images/suv.jpg' WHERE name = 'Meghu Traveller';

UPDATE cars
SET name = 'Urbania', category = 'Traveller', seats = 16,
    description = 'A spacious 16-seat Urbania for family holidays and group journeys.'
WHERE name = 'Meghu Group Traveller';

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Urbania', 'Traveller', 16, 4800.00, 'images/touring-car.jpg',
       'A spacious 16-seat Urbania for family holidays and group journeys.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Urbania');

UPDATE cars
SET category = 'Traveller', seats = 16,
    description = 'A spacious 16-seat Urbania for family holidays and group journeys.',
    image_url = 'images/touring-car.jpg'
WHERE name = 'Urbania';

UPDATE cars SET name = 'Etios'
WHERE name = 'Meghu Premium Sedan';

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Etios', 'Premium', 4, 3200.00, 'images/van.jpg',
       'Extra comfort for special occasions and longer drives.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Etios');

INSERT INTO cars (name, category, seats, price_per_day, image_url, description)
SELECT 'Meghu Holiday Coach', 'Coach', 30, 5400.00, 'images/premium-sedan.jpg',
       'A spacious coach for family holidays and group journeys.'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE name = 'Meghu Holiday Coach');
