CREATE TYPE IF NOT EXISTS status_enum AS ENUM
    ('WAITING', 'APPROVED', 'REJECTED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS users
(
    user_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(255),
    email character varying(255) UNIQUE,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(255),
    description character varying(255),
    available boolean,
    owner_id bigint,
    request_id bigint,
    CONSTRAINT items_pkey PRIMARY KEY (item_id),
    CONSTRAINT items_owner_id_fkey FOREIGN KEY (owner_id)
        REFERENCES users (user_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    item_id bigint,
    booker_id bigint,
    status STATUS_ENUM,
    CONSTRAINT bookings_pkey PRIMARY KEY (booking_id),
    CONSTRAINT bookings_booker_id_fkey FOREIGN KEY (booker_id)
        REFERENCES users (user_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT bookings_item_id_fkey FOREIGN KEY (item_id)
        REFERENCES items (item_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    text character varying(255),
    item_id bigint,
    author_id bigint,
    created timestamp without time zone,
    CONSTRAINT comments_pkey PRIMARY KEY (comment_id),
    CONSTRAINT comments_author_id_fkey FOREIGN KEY (author_id)
        REFERENCES users (user_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT comments_item_id_fkey FOREIGN KEY (item_id)
        REFERENCES items (item_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    description character varying(255),
    requestor_id bigint,
    created timestamp without time zone,
    CONSTRAINT requests_pkey PRIMARY KEY (request_id),
    CONSTRAINT requests_requestor_id_fkey FOREIGN KEY (requestor_id)
        REFERENCES users (user_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)


