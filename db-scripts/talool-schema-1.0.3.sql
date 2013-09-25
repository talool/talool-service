
\connect talool

BEGIN;
CREATE TYPE payment_processor_type AS ENUM ('BRAINTREE');
ALTER TABLE deal_offer_purchase ADD COLUMN  payment_processor_t payment_processor_type;
ALTER TABLE deal_offer_purchase ADD COLUMN  processor_transaction_id character varying(32);
COMMIT;


BEGIN;
ALTER TABLE deal_offer_purchase ALTER COLUMN customer_id DROP NOT NULL;
ALTER TABLE deal_offer_purchase DROP CONSTRAINT "FK_DealOfferPurchase_Customer";
ALTER TABLE deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE SET NULL;
COMMIT;