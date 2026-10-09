# Meghu Holidays

A Java Servlet/JSP holiday car-booking website backed by MySQL. The public site showcases the Meghu Holidays fleet; the application also includes dated bookings, customer booking history, admin management, and a driver assignment dashboard.

## Requirements

- JDK 11+
- Maven 3.8+
- MySQL 8+
- Tomcat 9 (this project uses the `javax.servlet` API)

## Setup

1. Create the database and tables by running `database/meghu_travels.sql` in MySQL.
2. Set the database connection environment variables:
   - `MEGHU_DB_URL` (default: `jdbc:mysql://localhost:3306/meghu_travels?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
   - `MEGHU_DB_USER` (default: `root`)
   - `MEGHU_DB_PASSWORD` (default: empty)
3. Build with `mvn clean package`, then deploy `target/MeghuTravels.war` to Tomcat 9.
4. Register normally for a customer account. To create the first administrator, register an account and promote it once in MySQL:

   ```sql
   UPDATE users SET role = 'admin' WHERE email = 'your-admin@example.com';
   ```

5. Sign in to the staff workspace as admin and use **Drivers** to create driver accounts, then assign drivers to bookings from **Bookings**.

The application intentionally does not ship with a default admin password. Passwords are stored as BCrypt hashes.
