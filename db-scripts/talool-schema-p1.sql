-- owner must be POSTGRES!  Be sure you run this script as postgres!!!!!!!!
alter table merchant_location alter column country SET DEFAULT 'US';


-- adding chris@talool customer
INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('chris@talool.com', (select md5('pass123')), 'Chris', 'Lintz', 'M');

-- add Vince to talool
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'vince@talool.com',(select md5('pass123')),'VP Ops',true);
       
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'cory@talool.com',(select md5('pass123')),'Engineer',true);

ALTER table deal_offer add column location_name character varying(64);


CREATE TABLE activity (
	activity_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    gift_id UUID,
    activity_type smallint NOT NULL,
    activity_version character varying(36),
    activity_data bytea NOT NULL,
    activity_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (activity_id)
);

-- owner must be POSTGRES!  Be sure you run this script as postgres!!!!!!!!
-- owner must be POSTGRES!  Be sure you run this script as postgres!!!!!!!!
ALTER TABLE public.activity OWNER TO talool;

CREATE INDEX activity_activity_id_idx ON activity (customer_id);
CREATE INDEX activity_gift_id_idx ON activity (gift_id);

ALTER TYPE gift_status OWNER TO talool;

ALTER TYPE gift_status ADD VALUE 'INVALIDATED' AFTER 'REJECTED';

CREATE TABLE activation_code (
    activation_code_id UUID NOT NULL DEFAULT uuid_generate_v4(),
	deal_offer_id UUID NOT NULL,
    code character(7) NOT NULL,
    customer_id UUID,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    activated_dt timestamp without time zone,
    PRIMARY KEY (activation_code_id),
    UNIQUE (deal_offer_id,code)
);

ALTER TABLE public.activation_code OWNER TO talool;

CREATE INDEX activation_code_code_idx ON activation_code (code);

CREATE INDEX activation_code_code_customer_id_idx ON activation_code (customer_id);

BEGIN;
ALTER TABLE customer_social_account DROP CONSTRAINT "FK_CustomerSocialAccount_Customer";
ALTER TABLE customer_social_account ADD CONSTRAINT "FK_CustomerSocialAccount_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE deal_offer_purchase ALTER COLUMN customer_id DROP NOT NULL;
ALTER TABLE deal_offer_purchase DROP CONSTRAINT "FK_DealOfferPurchase_Customer";
ALTER TABLE deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE SET NULL;
COMMIT;

BEGIN;
ALTER TABLE deal_acquire DROP CONSTRAINT "FK_Dealacquire_Customer";
ALTER TABLE deal_acquire ADD CONSTRAINT "FK_Dealacquire_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE deal_acquire_history DROP CONSTRAINT "FK_DealacquireHistory_Customer";
ALTER TABLE deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Customer"
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
COMMIT;

BEGIN;

ALTER TABLE gift DROP CONSTRAINT "FK_Gift_Customer";
ALTER TABLE gift ADD CONSTRAINT "FK_Gift_Customer"
      FOREIGN KEY (from_customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE deal_acquire_history DROP CONSTRAINT "FK_DealacquireHistory_Dealacquire";
ALTER TABLE deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Dealacquire"
      FOREIGN KEY (deal_acquire_id) REFERENCES deal_acquire(deal_acquire_id) ON DELETE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE gift DROP CONSTRAINT "FK_Gift_DealAcquire";
ALTER TABLE gift ADD CONSTRAINT "FK_Gift_DealAcquire"
      FOREIGN KEY (deal_acquire_id) REFERENCES deal_acquire(deal_acquire_id) ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE deal_acquire DROP CONSTRAINT "FK_Dealacquire_Gift" ;
ALTER TABLE deal_acquire ADD CONSTRAINT "FK_Dealacquire_Gift" 
      FOREIGN KEY (gift_id) REFERENCES gift(gift_id) ON DELETE CASCADE;
COMMIT;

BEGIN;
ALTER TABLE deal_acquire_history DROP CONSTRAINT "FK_DealacquireHistory_Gift" ;
ALTER TABLE deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Gift" 
      FOREIGN KEY (gift_id) REFERENCES gift(gift_id) ON DELETE CASCADE;
COMMIT;

DROP TABLE friend_request;

DROP TABLE relationship;


