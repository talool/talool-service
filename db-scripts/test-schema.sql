
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


CREATE DATABASE talool-test WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US' LC_CTYPE = 'en_US';

ALTER DATABASE talool-test OWNER TO talool;

\connect talooltest

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

CREATE EXTENSION "uuid-ossp";

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

CREATE FUNCTION update_dt_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	BEGIN
	   NEW.update_dt = now(); 
	   RETURN NEW;
	END;
	$$;
	

ALTER FUNCTION public.update_dt_column() OWNER TO talool;

CREATE FUNCTION deal_offer_purchase() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  rec deal%rowtype;
BEGIN
  FOR rec IN SELECT * FROM deal WHERE deal_offer_id = NEW.deal_offer_id
  LOOP
    INSERT INTO deal_acquire(deal_id,acquire_status_id,customer_id) 
       VALUES( rec.deal_id,(select acquire_status_id from acquire_status where status='PURCHASED'),NEW.customer_id);
  end loop;
  return NEW;
END;
$$;


ALTER FUNCTION public.deal_offer_purchase() OWNER TO talool;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE address (
    address_id bigint NOT NULL,
    address1 character varying(64),
    address2 character varying(64),
    city character varying(64) NOT NULL,
    state_province_county character varying(64) NOT NULL,
    zip character varying(64),
    country character varying(4),
    create_dt timestamp NOT NULL DEFAULT NOW(),
    update_dt timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY(address_id)
);


ALTER TABLE public.address OWNER TO talool;

CREATE SEQUENCE address_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.address_address_id_seq OWNER TO talool;
ALTER SEQUENCE address_address_id_seq OWNED BY address.address_id;
ALTER TABLE ONLY address ALTER COLUMN address_id SET DEFAULT nextval('address_address_id_seq'::regclass);
CREATE UNIQUE INDEX address_idx ON address (address1,address2,city,state_province_county,zip,country);

CREATE TYPE sex_type AS ENUM ('M', 'F');

CREATE TABLE customer (
  	customer_id character varying (36) NOT NULL DEFAULT uuid_generate_v4()::character(36),
    --customer_id bigint NOT NULL,
    email character varying(128) NOT NULL,
    password character varying(32) NOT NULL,
    first_name character varying(64)  NULL,
    last_name character varying(64)  NULL,
    sex_t sex_type NULL,
    birth_date date NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY( customer_id )
);

ALTER TABLE public.customer OWNER TO talool;

CREATE UNIQUE INDEX customer_email_idx ON customer (email);

CREATE TYPE relationship_status AS ENUM ('PENDING', 'FRIEND','BLOCKED');

CREATE TABLE deal_offer_auth (
   deal_offer_auth_id bigint NOT NULL,  
   request_by_merchant_id bigint NOT NULL,
   request_for_merchant_id bigint NOT NULL,
   auth_status_id smallint NOT NULL, 
   request_by_account_id bigint,
   received_by_account_id bigint,
   allow_group_deal_create bool DEFAULT false,
   allow_deal_create bool DEFAULT false,
   create_dt timestamp without time zone NOT NULL,
   update_dt timestamp without time zone NOT NULL,
   PRIMARY KEY(deal_offer_auth_id)
);

CREATE TABLE friend_request (
    friend_request_id bigint NOT NULL,
    customer_id character varying (36) NOT NULL,
    friend_facebook_id character varying(32),
    friend_email character varying(128),
    deal_id bigint,
    create_dt timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY(friend_request_id),
    UNIQUE(customer_id,friend_facebook_id,friend_email)
);

ALTER TABLE public.friend_request OWNER TO talool;

CREATE SEQUENCE friend_request_friend_request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.friend_request_friend_request_id_seq OWNER TO talool;
ALTER SEQUENCE friend_request_friend_request_id_seq OWNED BY friend_request.friend_request_id;
ALTER TABLE ONLY friend_request ALTER COLUMN friend_request_id SET DEFAULT nextval('friend_request_friend_request_id_seq'::regclass);
ALTER TABLE ONLY friend_request ADD CONSTRAINT "FK_FriendRequest_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
CREATE INDEX friend_request_customer_idx ON friend_request (customer_id);


CREATE TABLE relationship (
	relationship_id bigint NOT NULL,
    from_customer_id character varying (36) NOT NULL,
    to_customer_id character varying (36) NOT NULL,
    status relationship_status NOT NULL,
    create_dt timestamp NOT NULL DEFAULT NOW(),
    update_dt timestamp NOT NULL DEFAULT NOW(),
    PRIMARY KEY(relationship_id)
);

ALTER TABLE public.relationship OWNER TO talool;

CREATE SEQUENCE relationship_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.relationship_relationship_id_seq OWNER TO talool;
ALTER SEQUENCE relationship_relationship_id_seq OWNED BY relationship.relationship_id;
ALTER TABLE ONLY relationship ALTER COLUMN relationship_id SET DEFAULT nextval('relationship_relationship_id_seq'::regclass);
ALTER TABLE ONLY relationship ADD CONSTRAINT "FK_Relationship_FromCustomer" FOREIGN KEY (from_customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY relationship ADD CONSTRAINT "FK_Relationship_ToCustomer" FOREIGN KEY (to_customer_id) REFERENCES customer(customer_id);
CREATE INDEX relationship_from_customer_id_idx ON relationship (from_customer_id);
CREATE INDEX relationship_to_customer_id_idx ON relationship (to_customer_id);

CREATE SEQUENCE tag_tag_id_seq 
 	START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    CACHE 1;
    
CREATE TABLE tag (
    tag_id smallint NOT NULL,
    name character varying(32) NOT NULL,
    PRIMARY KEY (tag_id)
);

ALTER TABLE public.tag OWNER TO talool;
ALTER TABLE public.tag_tag_id_seq OWNER TO talool;
ALTER SEQUENCE tag_tag_id_seq OWNED BY tag.tag_id;
ALTER TABLE ONLY tag ALTER COLUMN tag_id SET DEFAULT nextval('tag_tag_id_seq'::regclass);
CREATE UNIQUE INDEX tag_name_idx ON tag (name);

CREATE SEQUENCE social_network_id_seq 
 	START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

REVOKE ALL ON SEQUENCE social_network_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE social_network_id_seq FROM talool;
GRANT ALL ON SEQUENCE social_network_id_seq TO talool;
   
CREATE TYPE account_type AS ENUM ('MER', 'CUS');

CREATE TABLE social_network (
    social_network_id bigint NOT NULL,
    name character varying(32) NOT NULL,
    website character varying(64) NOT NULL,
    api_url character varying(64) NOT NULL,
    PRIMARY KEY (social_network_id)
);

ALTER TABLE public.social_network OWNER TO talool;

CREATE UNIQUE INDEX social_network_idx ON social_network (name);

CREATE TABLE social_account (
    user_id character varying(36) NOT NULL,
    account_t account_type NOT NULL,
    social_network_id bigint NOT NULL,
    login_id character varying(32) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (user_id,account_t,social_network_id)
);

ALTER TABLE public.social_account OWNER TO talool;
ALTER TABLE ONLY social_account ADD CONSTRAINT "FK_CustomerSocialAccount_SocialNetwork" FOREIGN KEY (social_network_id) REFERENCES social_network(social_network_id);
CREATE UNIQUE INDEX social_user_id_account_idx ON social_account (user_id,account_t);

ALTER TABLE public.social_network_id_seq OWNER TO talool;
ALTER SEQUENCE social_network_id_seq OWNED BY social_network.social_network_id;
ALTER TABLE ONLY social_network ALTER COLUMN social_network_id SET DEFAULT nextval('social_network_id_seq'::regclass);

CREATE TABLE merchant_location (
    merchant_location_id bigint NOT NULL,
    merchant_location_name character varying(64),
    email character varying(128) NOT NULL,
    website_url character varying(128),
    logo_url character varying(64) NOT NULL,
    phone character varying(48),
    latitude double precision,
    longitude double precision,
    address_id bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_location_id),
    UNIQUE (merchant_location_name,address_id)
);

ALTER TABLE public.merchant_location OWNER TO talool;

CREATE SEQUENCE merchant_location_merchant_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public. merchant_location_merchant_location_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_location_merchant_location_id_seq OWNED BY merchant_location.merchant_location_id;
ALTER TABLE ONLY merchant_location ALTER COLUMN merchant_location_id SET DEFAULT nextval('merchant_location_merchant_location_id_seq'::regclass);

ALTER TABLE ONLY merchant_location ADD CONSTRAINT "FK_MerchantLocation_Address" FOREIGN KEY (address_id) REFERENCES address(address_id);
CREATE INDEX merchant_location_name_idx ON merchant_location (merchant_location_name);
CREATE INDEX merchant_location_latitude_idx ON merchant_location (latitude);
CREATE INDEX merchant_location_longitude_idx ON merchant_location (longitude);

CREATE TABLE merchant (
    merchant_id bigint NOT NULL,
    merchant_parent_id bigint,
    primary_location_id bigint NOT NULL,
    merchant_name character varying(64) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_id),
    UNIQUE (merchant_name,primary_location_id)
);

ALTER TABLE public.merchant OWNER TO talool;

CREATE SEQUENCE merchant_merchant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.merchant_merchant_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_merchant_id_seq OWNED BY merchant.merchant_id;
ALTER TABLE ONLY merchant ALTER COLUMN merchant_id SET DEFAULT nextval('merchant_merchant_id_seq'::regclass);
ALTER TABLE ONLY merchant ADD CONSTRAINT "FK_Merchant_Merchant" FOREIGN KEY (merchant_parent_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant ADD CONSTRAINT "FK_Merchant_MechantLocation" FOREIGN KEY (primary_location_id) REFERENCES merchant_location(merchant_location_id);
CREATE INDEX merchant_name_idx ON merchant (merchant_name);

CREATE TABLE property_type (
    property_type_id smallint NOT NULL,
    name character varying(64),
    type character varying(64),
    PRIMARY KEY(property_type_id),
    UNIQUE(name)
);

CREATE TABLE merchant_property (
	merchant_property_id bigint NOT NULL,
	merchant_id bigint NOT NULL,
    property_type_id smallint NOT NULL,
    property_value character varying(128),
    PRIMARY KEY(merchant_property_id)
);

CREATE TYPE request_status AS ENUM ('PENDING', 'APPROVED');

CREATE TABLE merchant_request (
   merchant_request_id bigint NOT NULL,  
   request_by_merchant_id bigint NOT NULL,
   request_for_merchant_id bigint NOT NULL,
   request_property_id smallint NOT NULL,
   status request_status NOT NULL, 
   request_by_account_id bigint,
   response_by_account_id bigint,
   create_dt timestamp without time zone NOT NULL,
   update_dt timestamp without time zone NOT NULL,
   PRIMARY KEY(merchant_request_id)
);


CREATE SEQUENCE merchant_managed_location_merchant_managed_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
CREATE TABLE merchant_managed_location (
    merchant_managed_location_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    merchant_location_id bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_managed_location_id)
);

ALTER TABLE public.merchant_managed_location OWNER TO talool;
ALTER TABLE public.merchant_managed_location_merchant_managed_location_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_managed_location_merchant_managed_location_id_seq OWNED BY merchant_managed_location.merchant_managed_location_id;
ALTER TABLE ONLY merchant_managed_location ALTER COLUMN merchant_managed_location_id SET DEFAULT nextval('merchant_managed_location_merchant_managed_location_id_seq'::regclass);
ALTER TABLE ONLY merchant_managed_location ADD CONSTRAINT "FK_MerchantManagedLocation_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_managed_location ADD CONSTRAINT "FK_MerchantManagedLocation_MerchantLocation" FOREIGN KEY (merchant_location_id) REFERENCES merchant_location(merchant_location_id);

CREATE TABLE merchant_account (
 	merchant_account_id bigint NOT NULL,
 	merchant_id bigint NOT NULL,
    email character varying(128) NOT NULL,
    password character varying(32) NOT NULL,
    role_title character varying(64) NOT NULL,
    allow_deal_creation bool NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_account_id)
);

ALTER TABLE public.merchant_account OWNER TO talool;

CREATE SEQUENCE merchant_account_merchant_account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.merchant_account_merchant_account_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_account_merchant_account_id_seq OWNED BY merchant_account.merchant_account_id;
ALTER TABLE ONLY merchant_account ALTER COLUMN merchant_account_id SET DEFAULT nextval('merchant_account_merchant_account_id_seq'::regclass);
ALTER TABLE ONLY merchant_account ADD CONSTRAINT "FK_MerchantAccount_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);

CREATE UNIQUE INDEX merchant_account_email_idx ON merchant_account (email);
CREATE TYPE deal_type AS ENUM ('PAID_BOOK','FREE_BOOK','PAID_DEAL','FREE_DEAL');

CREATE TABLE deal_offer (
    deal_offer_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    updated_by_merchant_account_id bigint NOT NULL,
    deal_type deal_type NOT NULL,
    title character varying(256) NOT NULL,
    summary character varying(256),
    code character varying(128), 
    image_url character varying(128), 
    expires timestamp without time zone,
    price numeric(10,2) NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_offer_id)
);

ALTER TABLE public.deal_offer OWNER TO talool;

CREATE SEQUENCE deal_offer_deal_offer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_offer_deal_offer_id_seq OWNER TO talool;
ALTER SEQUENCE deal_offer_deal_offer_id_seq OWNED BY deal_offer.deal_offer_id;
ALTER TABLE ONLY deal_offer ALTER COLUMN deal_offer_id SET DEFAULT nextval('deal_offer_deal_offer_id_seq'::regclass);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_CreatedMerchantAccount" FOREIGN KEY (created_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_UpdatedMerchantAccount" FOREIGN KEY (updated_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);

CREATE INDEX deal_offer_created_by_merchant_account_id_idx ON deal_offer (created_by_merchant_account_id);
CREATE INDEX deal_offer_updated_by_merchant_account_id_idx ON deal_offer (updated_by_merchant_account_id);
CREATE INDEX deal_offer_merchant_id_idx ON deal_offer (merchant_id);

CREATE TABLE deal_offer_purchase (
    deal_offer_purchase_id bigint NOT NULL,   
    deal_offer_id bigint NOT NULL,
    customer_id character varying(36) NOT NULL,
    latitude double precision,
    longitude double precision,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_offer_purchase_id)
);

ALTER TABLE public.deal_offer_purchase OWNER TO talool;

CREATE SEQUENCE deal_offer_purchase_deal_offer_purchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_offer_purchase_deal_offer_purchase_id_seq OWNER TO talool;
ALTER SEQUENCE deal_offer_purchase_deal_offer_purchase_id_seq OWNED BY deal_offer_purchase.deal_offer_purchase_id;
ALTER TABLE ONLY deal_offer_purchase ALTER COLUMN deal_offer_purchase_id SET DEFAULT nextval('deal_offer_purchase_deal_offer_purchase_id_seq'::regclass);

ALTER TABLE ONLY deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_DealOffer" FOREIGN KEY (deal_offer_id) REFERENCES deal_offer(deal_offer_id);
ALTER TABLE ONLY deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
CREATE INDEX deal_offer_purchase_deal_offer_idx ON deal_offer_purchase (deal_offer_id);
CREATE INDEX deal_offer_purchase_customer_idx ON deal_offer_purchase (customer_id);
CREATE INDEX deal_offer_purchase_latitude_idx ON deal_offer_purchase (latitude);
CREATE INDEX deal_offer_purchase_longitude_idx ON deal_offer_purchase (longitude);

CREATE TABLE deal (
    deal_id bigint NOT NULL,   
    deal_offer_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    updated_by_merchant_account_id bigint NOT NULL,
    deal_illndex int,
    title character varying(256) NOT NULL,
    summary character varying(256) NOT NULL,
    details character varying(256) NOT NULL,
    code character varying(128), 
    image_url character varying(128), 
    expires timestamp without time zone,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_id)
);

ALTER TABLE public.deal OWNER TO talool;

CREATE SEQUENCE deal_deal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_deal_id_seq OWNER TO talool;
ALTER SEQUENCE deal_deal_id_seq OWNED BY deal.deal_id;
ALTER TABLE ONLY deal ALTER COLUMN deal_id SET DEFAULT nextval('deal_deal_id_seq'::regclass);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_DealOffer" FOREIGN KEY (deal_offer_id) REFERENCES deal_offer(deal_offer_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_CreatedByMerchant" FOREIGN KEY (created_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_UpdatedByMerchant" FOREIGN KEY (updated_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);

CREATE INDEX deal_created_by_merchant_account_id_idx ON deal (created_by_merchant_account_id);
CREATE INDEX deal_updated_by_merchant_account_id_idx ON deal (updated_by_merchant_account_id);
CREATE INDEX deal_merchant_id_idx ON deal (merchant_id);

CREATE TABLE deal_tag (
    deal_id bigint NOT NULL,
    tag_id smallint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_id,tag_id)
);

ALTER TABLE public.deal_tag OWNER TO talool;
ALTER TABLE ONLY deal_tag ADD CONSTRAINT "FK_DealTag_Deal" FOREIGN KEY (deal_id) REFERENCES deal(deal_id);
ALTER TABLE ONLY deal_tag ADD CONSTRAINT "FK_DealTag_Tag" FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
CREATE INDEX deal_tag_deal_id_idx ON deal_tag (deal_id);
CREATE INDEX deal_tag_tag_id_idx ON deal_tag (tag_id);
    
CREATE TABLE merchant_tag (
    merchant_id bigint NOT NULL,
    tag_id smallint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_id,tag_id)
);

ALTER TABLE public.merchant_tag OWNER TO talool;
ALTER TABLE ONLY merchant_tag ADD CONSTRAINT "FK_MerchantTag_Deal" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_tag ADD CONSTRAINT "FK_MerchantTag_Tag" FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
CREATE INDEX merchant_tag_merchant_id_idx ON merchant_tag (merchant_id);
CREATE INDEX merchant_tag_tag_id_idx ON merchant_tag (tag_id);

CREATE TABLE acquire_status (
    acquire_status_id smallint NOT NULL,   
    status character varying(64),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(acquire_status_id)
);

ALTER TABLE public.acquire_status OWNER TO talool;

CREATE SEQUENCE acquire_status_acquire_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 100
    CACHE 1;
    
ALTER TABLE public.acquire_status_acquire_status_id_seq OWNER TO talool;
ALTER SEQUENCE acquire_status_acquire_status_id_seq OWNED BY acquire_status.acquire_status_id;
ALTER TABLE ONLY acquire_status ALTER COLUMN acquire_status_id SET DEFAULT nextval('acquire_status_acquire_status_id_seq'::regclass);
CREATE UNIQUE INDEX acquire_status_status_idx ON acquire_status (status);


CREATE TABLE deal_acquire (
    deal_acquire_id bigint NOT NULL,   
    deal_id bigint NOT NULL,
    acquire_status_id smallint NOT NULL, 
    customer_id character varying(36) NOT NULL,
    shared_by_merchant_id bigint,
    shared_by_customer_id character (36),
    share_cnt int NOT NULL DEFAULT 0,
    latitude double precision,
    longitude double precision,
    redemption_dt timestamp without time zone,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_acquire_id)
);

ALTER TABLE public.deal_acquire OWNER TO talool;

CREATE SEQUENCE deal_acquire_deal_acquire_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_acquire_deal_acquire_id_seq OWNER TO talool;
ALTER SEQUENCE deal_acquire_deal_acquire_id_seq OWNED BY deal_acquire.deal_acquire_id;
ALTER TABLE ONLY deal_acquire ALTER COLUMN deal_acquire_id SET DEFAULT nextval('deal_acquire_deal_acquire_id_seq'::regclass);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_DealDetail" FOREIGN KEY (deal_id) REFERENCES deal(deal_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_SharedByMerchant" FOREIGN KEY (shared_by_merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_SharedByCustomer" FOREIGN KEY (shared_by_customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_acquireStatus" FOREIGN KEY (acquire_status_id) REFERENCES acquire_status(acquire_status_id);
CREATE INDEX deal_acquire_deal_id_idx ON deal_acquire (deal_id);
CREATE INDEX deal_acquire_customer_id_idx ON deal_acquire (customer_id);
CREATE INDEX deal_acquire_shared_by_customer_id_idx ON deal_acquire (shared_by_customer_id);
CREATE INDEX deal_acquire_shared_by_merchant_id_idx ON deal_acquire (shared_by_merchant_id);

CREATE TABLE deal_acquire_history (
    deal_acquire_history_id bigint NOT NULL,  
    deal_acquire_id bigint NOT NULL,
    acquire_status_id smallint NOT NULL, 
    customer_id character varying(36) NOT NULL,
    shared_by_merchant_id bigint,
    shared_by_customer_id character varying (36),
    share_cnt int NOT NULL DEFAULT 0,
    update_dt timestamp without time zone NOT NULL,
    PRIMARY KEY(deal_acquire_history_id)
);

ALTER TABLE public.deal_acquire_history OWNER TO talool;

CREATE SEQUENCE deal_acquire_history_deal_acquire_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_acquire_history_deal_acquire_history_id_seq OWNER TO talool;
ALTER SEQUENCE deal_acquire_history_deal_acquire_history_id_seq OWNED BY deal_acquire_history.deal_acquire_id;
ALTER TABLE ONLY deal_acquire_history ALTER COLUMN deal_acquire_id SET DEFAULT nextval('deal_acquire_history_deal_acquire_history_id_seq'::regclass);

ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Dealacquire" FOREIGN KEY (deal_acquire_id) REFERENCES deal_acquire(deal_acquire_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_SharedByMerchant" FOREIGN KEY (shared_by_merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_SharedByCustomer" FOREIGN KEY (shared_by_customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_acquireStatus" FOREIGN KEY (acquire_status_id) REFERENCES acquire_status(acquire_status_id);




CREATE TRIGGER update_merchant_update_dt BEFORE UPDATE ON merchant FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_customer_update_dt BEFORE UPDATE ON customer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_address_update_dt BEFORE UPDATE ON address FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_socal_account_update_dt BEFORE UPDATE ON social_account FOR EACH ROW EXECUTE PROCEDURE update_dt_column();

CREATE TRIGGER deal_offer_purchase_insert BEFORE INSERT ON deal_offer_purchase FOR EACH ROW EXECUTE PROCEDURE deal_offer_purchase();




