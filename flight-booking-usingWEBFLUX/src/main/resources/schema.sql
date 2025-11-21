CREATE TABLE airline (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(50),
    logo_url TEXT
);

CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE flight (
    id SERIAL PRIMARY KEY,
    airline_id BIGINT REFERENCES airline(id),
    flight_number VARCHAR(100),
    origin VARCHAR(100),
    destination VARCHAR(100),
    depart_datetime TIMESTAMP,
    arrive_datetime TIMESTAMP,
    duration_min INT,
    price NUMERIC(10,2),
    total_seats INT,
    available_seats INT,
    version BIGINT
);

CREATE TABLE booking (
    id SERIAL PRIMARY KEY,
    pnr VARCHAR(20) UNIQUE NOT NULL,
    user_id BIGINT REFERENCES app_user(id),
    flight_id BIGINT REFERENCES flight(id),
    booking_time TIMESTAMP,
    total_price NUMERIC(10,2),
    status VARCHAR(50),
    seats_json TEXT
);

CREATE TABLE passenger (
    id SERIAL PRIMARY KEY,
    booking_id BIGINT REFERENCES booking(id),
    name VARCHAR(255),
    gender VARCHAR(50),
    age INT,
    meal_pref VARCHAR(100),
    seat_number VARCHAR(20)
);
