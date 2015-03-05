-- Adding white label association to a customer

BEGIN;

ALTER TABLE public.customer ADD COLUMN white_label_merchant_id UUID;

CREATE INDEX customer_white_label_merchant_id_idx ON customer USING BTREE (white_label_merchant_id);

ALTER TABLE public.customer ADD CONSTRAINT "FK_Customer_WhiteLabelMerchant" 
      FOREIGN KEY (white_label_merchant_id) REFERENCES merchant(merchant_id);
      
COMMIT;