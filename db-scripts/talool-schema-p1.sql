-- owner must be POSTGRES!  Be sure you run this script as postgres!!!!!!!!
alter table merchant_location alter column country SET DEFAULT 'US';


-- adding chris@talool customer
INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('chris@talool.com', (select md5('pass123')), 'Chris', 'Lintz', 'M');

-- add Vince to talool
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'vince@talool.com',(select md5('pass123')),'VP Ops',true);

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