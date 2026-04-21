DROP DATABASE IF EXISTS tak_limited;
CREATE DATABASE IF NOT EXISTS tak_limited;
USE tak_limited;

-- Added backticks around `password` and `role`
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY, 
    username VARCHAR(50) UNIQUE NOT NULL, 
    password VARCHAR(50) NOT NULL, 
    role VARCHAR(10) NOT NULL
);

CREATE TABLE owner_info (
    owner_id INT AUTO_INCREMENT PRIMARY KEY, 
    user_id INT, 
    name VARCHAR(100), 
    phone VARCHAR(20), 
    email VARCHAR(100), 
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
CREATE TABLE tenant_info (
    tenant_id INT AUTO_INCREMENT PRIMARY KEY, 
    user_id INT, 
    name VARCHAR(100), 
    phone VARCHAR(20), 
    email VARCHAR(100), 
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE flats (
    flat_id INT AUTO_INCREMENT PRIMARY KEY, 
    owner_id INT, 
    location VARCHAR(50), 
    rent DOUBLE, 
    size INT, 
    bedroom INT, 
    washroom INT, 
    status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE flat_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY, 
    flat_id INT, 
    image_path VARCHAR(255), 
    FOREIGN KEY (flat_id) REFERENCES flats(flat_id)
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY, 
    flat_id INT, 
    tenant_id INT, 
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transport (
    transport_id INT AUTO_INCREMENT PRIMARY KEY, 
    booking_id INT, 
    pickup_address VARCHAR(255), 
    moving_date DATE, 
     time VARCHAR(50), 
    truck_size VARCHAR(20), 
    manpower INT, 
    cost DOUBLE
);

-- Dummy Data (Run this to test the login!)
INSERT INTO users (username, `password`, `role`) VALUES ('admin', '123', 'Owner'), ('test', '123', 'Tenant');
INSERT INTO owner_info (user_id, name, phone, email) VALUES (1, 'TAK Admin', '+8801700000000', 'admin@tak.com');
INSERT INTO flats (owner_id, location, rent, size, bedroom, washroom, status) VALUES (1, 'Dhanmondi', 25000, 1200, 3, 2, 'Available');