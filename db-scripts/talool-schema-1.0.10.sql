
-- cleanup old unused tables
drop table merchant_property;
drop table property_type;

-- create HSTORE / props
create extension HSTORE;

ALTER TABLE deal_offer_purchase add column properties HSTORE;

CREATE INDEX deal_offer_purchase_properties_idx ON deal_offer_purchase USING BTREE (properties);

CREATE INDEX deal_offer_purchase_propertie_gist_idx ON deal_offer_purchase USING GIST (properties);

ALTER TABLE deal_offer add column properties HSTORE;

CREATE INDEX deal_offer_properties_idx ON deal_offer USING BTREE (properties);

CREATE INDEX deal_offer_properties_gist_idx ON deal_offer USING GIST (properties);

ALTER TABLE merchant add column properties HSTORE;

CREATE INDEX merchant_properties_gist_idx ON merchant USING GIST (properties);

CREATE INDEX merchant_properties_idx ON merchant USING BTREE (properties);

ALTER TABLE merchant_account add column properties HSTORE;

CREATE INDEX merchant_account_properties_gist_idx ON merchant_account USING GIST (properties);

CREATE INDEX merchant_account_properties_idx ON merchant_account USING BTREE (properties);

ALTER TABLE merchant_location add column properties HSTORE;

CREATE INDEX merchant_location_properties_gist_idx ON merchant_location USING GIST (properties);

CREATE INDEX merchant_location_properties_idx ON merchant_location USING BTREE (properties);

-- scheduling a deal changes
ALTER TABLE deal_offer add column scheduled_start_dt timestamp without time zone;

ALTER TABLE deal_offer add column scheduled_end_dt timestamp without time zone;

UPDATE deal_offer set scheduled_end_dt=expires;

UPDATE deal_offer set scheduled_start_dt=create_dt;

UPDATE deal_offer set scheduled_end_dt = '2016-12-31 00:00:00' where scheduled_end_dt is null;

-- create school codes

CREATE TABLE merchant_code_group (
    merchant_code_group_id bigserial NOT NULL,
    merchant_id UUID NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    publisher_id UUID NOT NULL,
    code_group_title character varying(64) NOT NULL,
    code_group_notes character varying(128),
    total_codes smallint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_code_group_id)
);

ALTER TABLE public.merchant_code_group OWNER TO talool;
ALTER TABLE ONLY merchant_code_group ADD CONSTRAINT "FK_MerchantCodeGroup_Merchant" FOREIGN KEY (merchant_id) 
  REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_code_group ADD CONSTRAINT "FK_MerchantCodeGroup_Publisher" FOREIGN KEY (publisher_id) 
  REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_code_group ADD CONSTRAINT "FK_MerchantCodeGroup_CreatedByMerchantAccount" FOREIGN KEY (created_by_merchant_account_id) 
  REFERENCES merchant_account(merchant_account_id);
CREATE INDEX merchant_code_group_merchant_id_idx ON merchant_code_group (merchant_id);
CREATE INDEX merchant_code_publisher_id_idx ON merchant_code_group (publisher_id);

CREATE TABLE merchant_code (
    merchant_code_id bigserial NOT NULL,
    merchant_code_group_id bigint NOT NULL,
    code character varying(10) NOT NULL,
    deal_offer_purchase_id UUID,
    PRIMARY KEY(merchant_code_id)
);

ALTER TABLE public.merchant_code OWNER TO talool;
ALTER TABLE ONLY merchant_code ADD CONSTRAINT "FK_MerchantCode_MerchantCodeGroup" FOREIGN KEY (merchant_code_group_id) 
  REFERENCES merchant_code_group(merchant_code_group_id);
  ALTER TABLE ONLY merchant_code ADD CONSTRAINT "FK_MerchantCode_DealOfferPurchase" FOREIGN KEY (deal_offer_purchase_id) 
  REFERENCES deal_offer_purchase(deal_offer_purchase_id);
CREATE UNIQUE INDEX merchant_code_uniq_idx ON merchant_code (merchant_code_id,code);
CREATE INDEX merchant_code_code_idx ON merchant_code (code);
CREATE INDEX merchant_code_deal_offer_purchase_id_idx ON merchant_code (deal_offer_purchase_id);

