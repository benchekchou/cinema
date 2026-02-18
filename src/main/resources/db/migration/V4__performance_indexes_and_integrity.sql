-- Performance indexes (PostgreSQL production)
CREATE INDEX IF NOT EXISTS idx_projection_film_date_projection ON projection_film (date_projection);
CREATE INDEX IF NOT EXISTS idx_film_categorie_id ON film (categorie_id);
CREATE INDEX IF NOT EXISTS idx_ticket_projection_id ON ticket (projection_id);

-- Business integrity for reservations
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_ticket_nom_client_not_blank'
    ) THEN
        ALTER TABLE ticket
            ADD CONSTRAINT ck_ticket_nom_client_not_blank
            CHECK (nom_client IS NULL OR length(btrim(nom_client)) > 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_ticket_reservation_payload'
    ) THEN
        ALTER TABLE ticket
            ADD CONSTRAINT ck_ticket_reservation_payload
            CHECK (
                (reservee = false AND code_payement = 0 AND nom_client IS NULL)
                OR
                (reservee = true AND code_payement > 0 AND nom_client IS NOT NULL AND length(btrim(nom_client)) > 0)
            );
    END IF;
END $$;

-- Unique payment code for reserved tickets only
CREATE UNIQUE INDEX IF NOT EXISTS uq_ticket_code_payement_reserved
    ON ticket (code_payement)
    WHERE reservee = true AND code_payement > 0;
