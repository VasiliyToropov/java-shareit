--DROP TABLE IF EXISTS users CASCADE;
--DROP TABLE IF EXISTS requests CASCADE;
--DROP TABLE IF EXISTS items CASCADE;
--DROP TABLE If EXISTS bookings CASCADE;
--DROP TABLE IF EXISTS comments CASCADE;


CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    CONSTRAINT unique_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(250),
    requester_id BIGINT REFERENCES users(user_id),
    created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE,
    description VARCHAR(250),
    owner_id BIGINT REFERENCES users(user_id),
    request_id BIGINT REFERENCES requests(request_id),
    is_available BOOLEAN
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT REFERENCES items(item_id),
    booker_id BIGINT REFERENCES users(user_id),
    status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(250),
    created TIMESTAMP WITHOUT TIME ZONE,
    author_name VARCHAR(50),
    item_id BIGINT REFERENCES items(item_id)
);