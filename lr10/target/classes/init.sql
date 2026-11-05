
SET COLLATION UKRAINIAN STRENGTH PRIMARY;

CREATE TABLE IF NOT EXISTS passengers (
    id   BIGINT       PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    age  INT          NOT NULL,
    CONSTRAINT ck_passenger_age CHECK (age > 0 AND age < 150)
);

CREATE TABLE IF NOT EXISTS tickets (
    id           BIGINT        PRIMARY KEY,
    name         VARCHAR(200)  NOT NULL,
    ticket_type  VARCHAR(10)   NOT NULL,
    price        NUMERIC(10,2) NOT NULL,
    place        INT           NOT NULL,
    city_from    VARCHAR(100)  NOT NULL,
    city_to      VARCHAR(100)  NOT NULL,
    status       VARCHAR(10)   NOT NULL DEFAULT 'BOOKED',
    carriage     INT,
    platform     INT,
    upper_berth  BOOLEAN,
    passenger_id BIGINT,
    CONSTRAINT ck_ticket_type  CHECK (ticket_type IN ('TRAIN', 'SLEEPER', 'BUS')),
    CONSTRAINT ck_ticket_price CHECK (price > 0),
    CONSTRAINT ck_ticket_place CHECK (place > 0),
    CONSTRAINT ck_ticket_status CHECK (status IN ('BOOKED', 'PAID', 'CANCELLED')),
    CONSTRAINT uq_ticket_seat UNIQUE (name, place),
    CONSTRAINT fk_ticket_passenger FOREIGN KEY (passenger_id)
        REFERENCES passengers (id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_tickets_passenger  ON tickets (passenger_id);
CREATE INDEX IF NOT EXISTS idx_tickets_name_price ON tickets (name, price);
