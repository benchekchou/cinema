CREATE TABLE ville (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION
);

CREATE TABLE cinema (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    longitude DOUBLE PRECISION,
    atitude DOUBLE PRECISION,
    nombre_salles INTEGER NOT NULL CHECK (nombre_salles > 0),
    ville_id BIGINT NOT NULL,
    CONSTRAINT fk_cinema_ville FOREIGN KEY (ville_id) REFERENCES ville (id)
);

CREATE TABLE salle (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nombre_place INTEGER NOT NULL CHECK (nombre_place > 0),
    cinema_id BIGINT NOT NULL,
    CONSTRAINT fk_salle_cinema FOREIGN KEY (cinema_id) REFERENCES cinema (id),
    CONSTRAINT uq_salle_cinema_name UNIQUE (cinema_id, name)
);

CREATE TABLE place (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL CHECK (numero > 0),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,
    altitude DOUBLE PRECISION,
    salle_id BIGINT NOT NULL,
    CONSTRAINT fk_place_salle FOREIGN KEY (salle_id) REFERENCES salle (id),
    CONSTRAINT uq_place_salle_numero UNIQUE (salle_id, numero)
);

CREATE TABLE seance (
    id BIGSERIAL PRIMARY KEY,
    heure_debut TIMESTAMP NOT NULL
);

CREATE TABLE categorie (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE film (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description VARCHAR(2048),
    duree DOUBLE PRECISION NOT NULL CHECK (duree > 0),
    realisateur VARCHAR(255),
    photo VARCHAR(255),
    date_sortie TIMESTAMP,
    categorie_id BIGINT NOT NULL,
    CONSTRAINT fk_film_categorie FOREIGN KEY (categorie_id) REFERENCES categorie (id)
);

CREATE TABLE projection_film (
    id BIGSERIAL PRIMARY KEY,
    date_projection TIMESTAMP NOT NULL,
    prix DOUBLE PRECISION NOT NULL CHECK (prix >= 0),
    film_id BIGINT NOT NULL,
    salle_id BIGINT NOT NULL,
    seance_id BIGINT NOT NULL,
    CONSTRAINT fk_projection_film_film FOREIGN KEY (film_id) REFERENCES film (id),
    CONSTRAINT fk_projection_film_salle FOREIGN KEY (salle_id) REFERENCES salle (id),
    CONSTRAINT fk_projection_film_seance FOREIGN KEY (seance_id) REFERENCES seance (id)
);

CREATE TABLE ticket (
    id BIGSERIAL PRIMARY KEY,
    nom_client VARCHAR(255),
    prix DOUBLE PRECISION NOT NULL CHECK (prix >= 0),
    code_payement INTEGER NOT NULL,
    reservee BOOLEAN NOT NULL DEFAULT FALSE,
    place_id BIGINT NOT NULL,
    projection_id BIGINT NOT NULL,
    CONSTRAINT fk_ticket_place FOREIGN KEY (place_id) REFERENCES place (id),
    CONSTRAINT fk_ticket_projection FOREIGN KEY (projection_id) REFERENCES projection_film (id),
    CONSTRAINT uq_ticket_place_projection UNIQUE (place_id, projection_id)
);

CREATE INDEX idx_ville_nom ON ville (nom);
CREATE INDEX idx_cinema_ville_id ON cinema (ville_id);
CREATE INDEX idx_film_titre ON film (titre);
CREATE INDEX idx_film_categorie_id ON film (categorie_id);
CREATE INDEX idx_projection_film_film_id ON projection_film (film_id);
CREATE INDEX idx_projection_film_date_projection ON projection_film (date_projection);
CREATE INDEX idx_projection_film_salle_id ON projection_film (salle_id);
CREATE INDEX idx_projection_film_seance_id ON projection_film (seance_id);
CREATE INDEX idx_ticket_projection_reservee ON ticket (projection_id, reservee);
