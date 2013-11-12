
\connect talool

-- as owned postgres

BEGIN;
ALTER TYPE sex_type ADD VALUE 'U'  AFTER 'F';
COMMIT;