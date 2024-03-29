
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE DATABASE talooltest WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US' LC_CTYPE = 'en_US';

\set ON_ERROR_STOP on;

ALTER DATABASE talooltest OWNER TO talool;

\connect talooltest

ALTER TABLE spatial_ref_sys OWNER TO talool;
ALTER VIEW geography_columns OWNER TO talool;
ALTER VIEW geometry_columns OWNER TO talool;

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
    INSERT INTO deal_acquire(deal_id,acquire_status,customer_id) VALUES( rec.deal_id,'PURCHASED',NEW.customer_id );
  END LOOP;
  return NEW;
END;
$$;

CREATE FUNCTION deal_acquire_update() RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO deal_acquire_history(deal_acquire_id,acquire_status,customer_id,gift_id,update_dt) 
    VALUES( OLD.deal_acquire_id,OLD.acquire_status,OLD.customer_id,OLD.gift_id,OLD.update_dt);
  return NEW;
END;
$$;
    
ALTER FUNCTION public.deal_offer_purchase() OWNER TO talool;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TYPE sex_type AS ENUM ('M', 'F');

CREATE TABLE customer (
  	customer_id UUID NOT NULL DEFAULT uuid_generate_v4(),
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

CREATE TABLE friend_request (
    friend_request_id bigint NOT NULL,
    customer_id UUID NOT NULL,
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
    from_customer_id UUID NOT NULL,
    to_customer_id UUID NOT NULL,
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
CREATE UNIQUE INDEX tag_name_lower_idx ON tag (lower(name));

CREATE TABLE category (
    category_id smallint NOT NULL,
    category_name character varying(32) NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE SEQUENCE category_category_id_seq 
 	START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    CACHE 1;

ALTER TABLE public.category OWNER TO talool;
ALTER TABLE public.category_category_id_seq OWNER TO talool;
ALTER SEQUENCE category_category_id_seq OWNED BY category.category_id;
ALTER TABLE ONLY category ALTER COLUMN category_id SET DEFAULT nextval('category_category_id_seq'::regclass);
CREATE UNIQUE INDEX category_name_lower_idx ON category (lower(category_name));

CREATE TABLE category_tag (
    category_id smallint NOT NULL,
    tag_id smallint NOT NULL,
    PRIMARY KEY (category_id,tag_id)
);

ALTER TABLE public.category_tag OWNER TO talool;
ALTER TABLE ONLY category_tag ADD CONSTRAINT "FK_CategoryTag_Category" FOREIGN KEY (category_id) REFERENCES category(category_id);
ALTER TABLE ONLY category_tag ADD CONSTRAINT "FK_CategoryTag_Tag" FOREIGN KEY (tag_id) REFERENCES tag(tag_id);

CREATE SEQUENCE social_network_id_seq 
 	START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

REVOKE ALL ON SEQUENCE social_network_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE social_network_id_seq FROM talool;
GRANT ALL ON SEQUENCE social_network_id_seq TO talool;

CREATE TABLE social_network (
    social_network_id bigint NOT NULL,
    name character varying(32) NOT NULL,
    website character varying(64) NOT NULL,
    api_url character varying(64) NOT NULL,
    PRIMARY KEY (social_network_id)
);

ALTER TABLE public.social_network OWNER TO talool;

CREATE UNIQUE INDEX social_network_idx ON social_network (name);


ALTER TABLE public.social_network_id_seq OWNER TO talool;
ALTER SEQUENCE social_network_id_seq OWNED BY social_network.social_network_id;
ALTER TABLE ONLY social_network ALTER COLUMN social_network_id SET DEFAULT nextval('social_network_id_seq'::regclass);

CREATE TABLE merchant (
	merchant_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    merchant_parent_id UUID,
    merchant_name character varying(64) NOT NULL,
    is_discoverable boolean DEFAULT true NOT NULL,
    category_id bigint,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_id),
    UNIQUE (merchant_name)
);

ALTER TABLE public.merchant OWNER TO talool;

ALTER TABLE ONLY merchant ADD CONSTRAINT "FK_Merchant_Merchant" FOREIGN KEY (merchant_parent_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant ADD CONSTRAINT "FK_Merchant_Category" FOREIGN KEY (category_id) REFERENCES category(category_id);
CREATE INDEX merchant_name_idx ON merchant (merchant_name);
CREATE INDEX merchant_category_id_idx ON merchant (category_id);

CREATE TABLE favorite_merchant (
	favorite_merchant_id bigint NOT NULL,
	customer_id UUID NOT NULL,
    merchant_id UUID NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (favorite_merchant_id),
    UNIQUE (customer_id,merchant_id)
);

ALTER TABLE public.favorite_merchant OWNER TO talool;

CREATE SEQUENCE favorite_merchant_favorite_merchant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.favorite_merchant_favorite_merchant_id_seq OWNER TO talool;
ALTER SEQUENCE favorite_merchant_favorite_merchant_id_seq OWNED BY favorite_merchant.favorite_merchant_id;
ALTER TABLE ONLY favorite_merchant ALTER COLUMN favorite_merchant_id SET DEFAULT nextval('favorite_merchant_favorite_merchant_id_seq'::regclass);

CREATE TYPE media_type AS ENUM ('DEAL_IMAGE','MERCHANT_IMAGE', 'MERCHANT_LOGO', 'DEAL_OFFER_LOGO');

CREATE TABLE merchant_media (
    merchant_media_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    merchant_id UUID NOT NULL,
	media_type media_type NOT NULL,
    media_url character varying(128) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_media_id),
    UNIQUE (merchant_id,media_url)
);

ALTER TABLE public.merchant_media OWNER TO talool;
ALTER TABLE ONLY merchant_media ADD CONSTRAINT "FK_MerchantMedia_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
CREATE INDEX merchant_media_merchant_media_type_idx ON merchant_media (merchant_id,media_type);


CREATE TABLE merchant_location (
    merchant_location_id bigint NOT NULL,
    merchant_id UUID NOT NULL,
    geom geometry(POINT,4326),
    merchant_location_name character varying(64),
    email character varying(128) NOT NULL,
    website_url character varying(128),
    logo_url_id UUID,
    merchant_image_id UUID,
    phone character varying(48),
    address1 character varying(64),
    address2 character varying(64),
    city character varying(64) NOT NULL,
    state_province_county character varying(64) NOT NULL,
    zip character varying(64),
    country character varying(4),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_location_id)
);

ALTER TABLE public.merchant_location OWNER TO talool;

CREATE SEQUENCE merchant_location_merchant_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.merchant_location_merchant_location_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_location_merchant_location_id_seq OWNED BY merchant_location.merchant_location_id;
ALTER TABLE ONLY merchant_location ALTER COLUMN merchant_location_id SET DEFAULT nextval('merchant_location_merchant_location_id_seq'::regclass);

ALTER TABLE ONLY merchant_location ADD CONSTRAINT "FK_MerchantLocation_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_location ADD CONSTRAINT "FK_MerchantLocation_Logo" FOREIGN KEY (logo_url_id) REFERENCES merchant_media(merchant_media_id);
ALTER TABLE ONLY merchant_location ADD CONSTRAINT "FK_MerchantLocation_MerchantImage" FOREIGN KEY (merchant_image_id) REFERENCES merchant_media(merchant_media_id);

CREATE INDEX merchant_location_name_idx ON merchant_location (merchant_location_name);
CREATE INDEX merchant_location_merchant_idx ON merchant_location (merchant_id);
CREATE INDEX merchant_location_geom_idx ON merchant_location USING GIST (geom);

CREATE TABLE property_type (
    property_type_id smallint NOT NULL,
    name character varying(64),
    type character varying(64),
    PRIMARY KEY(property_type_id),
    UNIQUE(name)
);

CREATE TABLE merchant_property (
	merchant_property_id bigint NOT NULL,
	merchant_id UUID NOT NULL,
    property_type_id smallint NOT NULL,
    property_value character varying(128),
    PRIMARY KEY(merchant_property_id)
);

CREATE TABLE merchant_account (
 	merchant_account_id bigint NOT NULL,
 	merchant_id UUID NOT NULL,
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
    deal_offer_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    merchant_id UUID NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    updated_by_merchant_account_id bigint NOT NULL,
    deal_type deal_type NOT NULL,
    title character varying(256) NOT NULL,
    summary character varying(256),
    code character varying(128), 
    image_id UUID, 
    expires timestamp without time zone,
    price numeric(10,2) NOT NULL,
    auto_acquire_new_deals boolean DEFAULT true NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_offer_id)
);

ALTER TABLE public.deal_offer OWNER TO talool;

ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_CreatedMerchantAccount" FOREIGN KEY (created_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_UpdatedMerchantAccount" FOREIGN KEY (updated_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY deal_offer ADD CONSTRAINT "FK_Deal_Image" FOREIGN KEY (image_id) REFERENCES merchant_media(merchant_media_id);


CREATE INDEX deal_offer_created_by_merchant_account_id_idx ON deal_offer (created_by_merchant_account_id);
CREATE INDEX deal_offer_updated_by_merchant_account_id_idx ON deal_offer (updated_by_merchant_account_id);
CREATE INDEX deal_offer_merchant_id_idx ON deal_offer (merchant_id);

CREATE TABLE deal_offer_purchase (
	deal_offer_purchase_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    deal_offer_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    latitude double precision,
    longitude double precision,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_offer_purchase_id)
);

ALTER TABLE public.deal_offer_purchase OWNER TO talool;

ALTER TABLE ONLY deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_DealOffer" FOREIGN KEY (deal_offer_id) REFERENCES deal_offer(deal_offer_id);
ALTER TABLE ONLY deal_offer_purchase ADD CONSTRAINT "FK_DealOfferPurchase_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
CREATE INDEX deal_offer_purchase_deal_offer_idx ON deal_offer_purchase (deal_offer_id);
CREATE INDEX deal_offer_purchase_customer_idx ON deal_offer_purchase (customer_id);
CREATE INDEX deal_offer_purchase_latitude_idx ON deal_offer_purchase (latitude);
CREATE INDEX deal_offer_purchase_longitude_idx ON deal_offer_purchase (longitude);

CREATE TABLE deal (
    deal_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    deal_offer_id UUID NOT NULL,
    merchant_id UUID NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    updated_by_merchant_account_id bigint NOT NULL,
    deal_index int,
    title character varying(256) NOT NULL,
    summary character varying(256) NOT NULL,
    details character varying(256),
    code character varying(128), 
    image_id UUID, 
    expires timestamp without time zone,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_id)
);

ALTER TABLE public.deal OWNER TO talool;

ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_DealOffer" FOREIGN KEY (deal_offer_id) REFERENCES deal_offer(deal_offer_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_CreatedByMerchant" FOREIGN KEY (created_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_UpdatedByMerchant" FOREIGN KEY (updated_by_merchant_account_id) REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY deal ADD CONSTRAINT "FK_Deal_Image" FOREIGN KEY (image_id) REFERENCES merchant_media(merchant_media_id);

CREATE INDEX deal_created_by_merchant_account_id_idx ON deal (created_by_merchant_account_id);
CREATE INDEX deal_updated_by_merchant_account_id_idx ON deal (updated_by_merchant_account_id);
CREATE INDEX deal_merchant_id_idx ON deal (merchant_id);

CREATE TABLE deal_tag (
    deal_id UUID NOT NULL,
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
    merchant_id UUID NOT NULL,
    tag_id smallint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_id,tag_id)
);

ALTER TABLE public.merchant_tag OWNER TO talool;
ALTER TABLE ONLY merchant_tag ADD CONSTRAINT "FK_MerchantTag_Deal" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
ALTER TABLE ONLY merchant_tag ADD CONSTRAINT "FK_MerchantTag_Tag" FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
CREATE INDEX merchant_tag_merchant_id_idx ON merchant_tag (merchant_id);
CREATE INDEX merchant_tag_tag_id_idx ON merchant_tag (tag_id);

CREATE TYPE gift_status AS ENUM ('PENDING', 'ACCEPTED','REJECTED');

CREATE TABLE gift (
    gift_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    request_type char(1) NOT NULL,
    from_customer_id UUID NOT NULL,
    deal_acquire_id UUID NOT NULL,
    gift_status gift_status NOT NULL,
    to_facebook_id character varying(32),
    receipient_name character varying(32),
    to_email character varying(128),
    to_customer_id UUID,
    original_to_facebook_id character varying(32),
    original_to_email character varying(128),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(gift_id)
);

CREATE TYPE acquire_status AS ENUM ('PURCHASED', 'REDEEMED','REJECTED_CUSTOMER_SHARE','REJECTED_MERCHANT_SHARE',
'ACCEPTED_MERCHANT_SHARE','ACCEPTED_CUSTOMER_SHARE','PENDING_ACCEPT_MERCHANT_SHARE','PENDING_ACCEPT_CUSTOMER_SHARE');

CREATE TABLE deal_acquire (
    deal_acquire_id UUID NOT NULL DEFAULT uuid_generate_v4(),   
    deal_id UUID NOT NULL,
    acquire_status acquire_status NOT NULL, 
    customer_id UUID NOT NULL,
    gift_id UUID,
    redeemed_at_geom geometry(POINT,4326),
    redemption_code character(6),
    redemption_dt timestamp without time zone,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(deal_acquire_id),
    UNIQUE (redemption_code,deal_id)
);

ALTER TABLE public.deal_acquire OWNER TO talool;

ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_DealDetail" FOREIGN KEY (deal_id) REFERENCES deal(deal_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire ADD CONSTRAINT "FK_Dealacquire_Gift" FOREIGN KEY (gift_id) REFERENCES gift(gift_id);
CREATE INDEX deal_acquire_deal_id_idx ON deal_acquire (deal_id);
CREATE INDEX deal_acquire_customer_id_idx ON deal_acquire (customer_id);
CREATE INDEX deal_acquire_gift_id_idx ON deal_acquire (gift_id);

CREATE TABLE deal_acquire_history (
    deal_acquire_id UUID NOT NULL,
    acquire_status acquire_status NOT NULL, 
    customer_id UUID NOT NULL,
    gift_id UUID,
    update_dt timestamp without time zone NOT NULL,
    PRIMARY KEY(deal_acquire_id,update_dt)
);

ALTER TABLE public.deal_acquire_history OWNER TO talool;

ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Dealacquire" FOREIGN KEY (deal_acquire_id) REFERENCES deal_acquire(deal_acquire_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY deal_acquire_history ADD CONSTRAINT "FK_DealacquireHistory_Gift" FOREIGN KEY (gift_id) REFERENCES gift(gift_id);
CREATE INDEX deal_acquire_history_customer_id_idx ON deal_acquire_history (customer_id);
CREATE INDEX deal_acquire_history_deal_acquire_id_idx ON deal_acquire_history (deal_acquire_id);
CREATE INDEX deal_acquire_history_gift_id_idx ON deal_acquire_history (gift_id);

ALTER TABLE public.gift OWNER TO talool;
ALTER TABLE ONLY gift ADD CONSTRAINT "FK_Gift_Customer" FOREIGN KEY (from_customer_id) REFERENCES customer(customer_id);
ALTER TABLE ONLY gift ADD CONSTRAINT "FK_Gift_DealAcquire" FOREIGN KEY (deal_acquire_id) REFERENCES deal_acquire(deal_acquire_id);
CREATE INDEX gift_customer_id_idx ON gift (from_customer_id);
CREATE INDEX gift_deal_acquire_id_idx ON gift (deal_acquire_id);
CREATE INDEX gift_to_facebook_id_idx ON gift (to_facebook_id);
CREATE INDEX gift_to_email_idx ON gift (to_email);

CREATE TABLE customer_social_account (
	customer_social_account_id bigserial NOT NULL,
    customer_id UUID NOT NULL,
    social_network_id bigint NOT NULL,
    login_id character varying(32) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (customer_social_account_id),
    UNIQUE ( customer_id, social_network_id )
);

CREATE TABLE merchant_social_account (
	merchant_social_account_id bigserial NOT NULL,
    merchant_id UUID,
    social_network_id bigint NOT NULL,
    login_id character varying(32) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_social_account_id)
);


ALTER TABLE public.customer_social_account OWNER TO talool;
ALTER TABLE ONLY customer_social_account ADD CONSTRAINT "FK_CustomerSocialAccount_SocialNetwork" FOREIGN KEY (social_network_id) REFERENCES social_network(social_network_id);
ALTER TABLE ONLY customer_social_account ADD CONSTRAINT "FK_CustomerSocialAccount_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
CREATE INDEX customer_social_account_customer_id_idx ON customer_social_account (customer_id);
CREATE INDEX customer_social_account_sn_id_idx ON customer_social_account (social_network_id);

ALTER TABLE public.merchant_social_account OWNER TO talool;
ALTER TABLE ONLY merchant_social_account ADD CONSTRAINT "FK_MerchantSocialAccount_SocialNetwork" FOREIGN KEY (social_network_id) REFERENCES social_network(social_network_id);
ALTER TABLE ONLY merchant_social_account ADD CONSTRAINT "FK_MerchantSocialAccount_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
CREATE INDEX merchant_social_account_merchant_id_idx ON merchant_social_account (merchant_id);
CREATE INDEX merchant_social_account_sn_id_idx ON merchant_social_account (social_network_id);


CREATE TRIGGER update_merchant_update_dt BEFORE UPDATE ON merchant FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_customer_update_dt BEFORE UPDATE ON customer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER deal_update_update_dt BEFORE UPDATE ON deal FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER deal_offer_update_dt BEFORE UPDATE ON deal_offer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER deal_acquire_update_dt BEFORE UPDATE ON deal_acquire FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER gift_update_dt BEFORE UPDATE ON gift FOR EACH ROW EXECUTE PROCEDURE update_dt_column();

CREATE TRIGGER deal_offer_purchase_insert BEFORE INSERT ON deal_offer_purchase FOR EACH ROW EXECUTE PROCEDURE deal_offer_purchase();
CREATE TRIGGER deal_acquire_update BEFORE UPDATE ON deal_acquire FOR EACH ROW EXECUTE PROCEDURE deal_acquire_update();


CREATE FUNCTION add_category_tag(text,text) RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
	cat_id smallint;
	tg_id smallint;
	cat_tg_id smallint;
BEGIN
  SELECT category_id INTO cat_id FROM category WHERE category_name = $1;
  IF NOT FOUND THEN
      INSERT INTO category(category_name) VALUES($1);
      SELECT category_id INTO cat_id FROM category WHERE category_name = $1;
  END IF;
  
  SELECT tag_id INTO tg_id FROM tag WHERE name = $2;
  IF NOT FOUND THEN
      INSERT INTO tag(name) VALUES($2);
      SELECT tag_id INTO tg_id FROM tag WHERE name = $2;
  END IF;

  INSERT INTO category_tag(category_id,tag_id) VALUES(cat_id,tg_id);
END;
$$


