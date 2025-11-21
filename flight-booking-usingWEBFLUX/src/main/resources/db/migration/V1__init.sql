CREATE TABLE airline (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    logo_url VARCHAR(255)
);

CREATE TABLE flight (
    id SERIAL PRIMARY KEY,
    airline_id INTEGER NOT NULL REFERENCES airline(id),
    flight_number VARCHAR(50) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    depart_datetime TIMESTAMP NOT NULL,
    arrive_datetime TIMESTAMP NOT NULL,
    duration_min INTEGER NOT NULL,
    price NUMERIC NOT NULL,
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    version BIGINT
);

CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)
);

CREATE TABLE booking (
    id SERIAL PRIMARY KEY,
    pnr VARCHAR(50) UNIQUE NOT NULL,
    user_id INTEGER REFERENCES app_user(id),
    flight_id INTEGER NOT NULL REFERENCES flight(id),
    booking_time TIMESTAMP,
    total_price NUMERIC,
    status VARCHAR(50),
    seats_json TEXT
);

CREATE TABLE passenger (
    id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES booking(id),
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50),
    age INTEGER,
    meal_pref VARCHAR(50),
    seat_number VARCHAR(50)
);
