
\connect talool

BEGIN;
CREATE TYPE payment_processor_type AS ENUM ('BRAINTREE');
ALTER TABLE deal_offer_purchase ADD COLUMN  payment_processor_t payment_processor_type;
ALTER TABLE deal_offer_purchase ADD COLUMN  processor_transaction_id character varying(32);
COMMIT;