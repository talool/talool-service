--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: talool; Type: DATABASE; Schema: -; Owner: talool
--


CREATE DATABASE talool-test WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US' LC_CTYPE = 'en_US';


ALTER DATABASE talool-test OWNER TO talool;

\connect talool_test

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: update_dt_column(); Type: FUNCTION; Schema: public; Owner: talool
--

CREATE FUNCTION update_dt_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	BEGIN
	   NEW.update_dt = now(); 
	   RETURN NEW;
	END;
	$$;


ALTER FUNCTION public.update_dt_column() OWNER TO talool;

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
    update_dt timestamp NOT NULL DEFAULT NOW()
);


ALTER TABLE public.address OWNER TO talool;

CREATE TYPE sex_type AS ENUM ('M', 'F');

CREATE TABLE customer (
    customer_id bigint NOT NULL,
    email character varying(64) NOT NULL,
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

CREATE SEQUENCE customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.customer_id_seq OWNER TO talool;
ALTER SEQUENCE customer_id_seq OWNED BY customer.customer_id;

CREATE SEQUENCE address_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.address_address_id_seq OWNER TO talool;
ALTER SEQUENCE address_address_id_seq OWNED BY address.address_id;

CREATE UNIQUE INDEX customer_email_idx ON customer (email);

CREATE SEQUENCE customer_customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_customer_id_seq OWNER TO talool;

ALTER SEQUENCE customer_customer_id_seq OWNED BY customer.customer_id;

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
    user_id bigint NOT NULL,
    account_t account_type NOT NULL,
    social_network_id bigint NOT NULL,
    login_id character varying(32) NOT NULL,
    token character varying(64),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (user_id,account_t,social_network_id)
);

ALTER TABLE public.social_account OWNER TO talool;

ALTER TABLE ONLY social_account
    ADD CONSTRAINT "FK_CustomerSocialAccount_SocialNetwork" FOREIGN KEY (social_network_id) REFERENCES social_network(social_network_id);

ALTER TABLE public.social_network_id_seq OWNER TO talool;
ALTER SEQUENCE social_network_id_seq OWNED BY social_network.social_network_id;
ALTER TABLE ONLY social_network ALTER COLUMN social_network_id SET DEFAULT nextval('social_network_id_seq'::regclass);

CREATE TABLE merchant (
    merchant_id bigint NOT NULL,
    merchant_parent_id bigint,
    merchant_name character varying(64) NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(32) NOT NULL,
    website_url character varying(128),
    logo_url character varying(64) NOT NULL,
    phone character varying(48),
    latitude double precision,
    longitude double precision,
    address_id bigint NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_id),
    UNIQUE (merchant_name,address_id)
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

CREATE INDEX merchant_latitude_idx ON merchant (latitude);
CREATE INDEX merchant_name_idx ON merchant (merchant_name);
CREATE INDEX merchant_longitude_idx ON merchant (longitude);
CREATE UNIQUE INDEX merchant_email_idx ON merchant (email);

CREATE TABLE merchant_deal (
    merchant_deal_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    title character varying(256) NOT NULL,
    summary character varying(256) NOT NULL,
    details character varying(256) NOT NULL,
    code character varying(128), 
    image_url character varying(128), 
    expires timestamp without time zone,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_deal_id)
);

ALTER TABLE public.merchant_deal OWNER TO talool;

CREATE SEQUENCE merchant_deal_merchant_deal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.merchant_deal_merchant_deal_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_deal_merchant_deal_id_seq OWNED BY merchant_deal.merchant_deal_id;
ALTER TABLE ONLY merchant_deal ALTER COLUMN merchant_deal_id SET DEFAULT nextval('merchant_deal_merchant_deal_id_seq'::regclass);
ALTER TABLE ONLY merchant_deal ADD CONSTRAINT "FK_MerchantDeal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
CREATE INDEX merchant_deal_merchant_id_idx ON merchant_deal (merchant_id);

CREATE TABLE deal_book (
    deal_book_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    latitude double precision,
    longitude double precision,
    title character varying(256) NOT NULL,
    summary character varying(256) NOT NULL,
    details character varying(256) NOT NULL,
    code character varying(128), 
    image_url character varying(128), 
    cost numeric(10,2) NOT NULL,
    expires timestamp without time zone,
    is_active boolean DEFAULT true NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (deal_book_id)
);

ALTER TABLE public.deal_book OWNER TO talool;
CREATE SEQUENCE deal_book_deal_book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_book_deal_book_id_seq OWNER TO talool;
ALTER SEQUENCE deal_book_deal_book_id_seq OWNED BY deal_book.deal_book_id;
ALTER TABLE ONLY deal_book ALTER COLUMN deal_book_id SET DEFAULT nextval('deal_book_deal_book_id_seq'::regclass);
ALTER TABLE ONLY deal_book ADD CONSTRAINT "FK_DealBook_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);
CREATE INDEX deal_book_merchant_id_idx ON deal_book (merchant_id);
CREATE INDEX deal_book_latitude_idx ON deal_book (latitude);
CREATE INDEX deal_book_longitude_idx ON deal_book (longitude);

CREATE TABLE deal_book_content (
	deal_book_content_id bigint NOT NULL,
    deal_book_id bigint NOT NULL,
    merchant_deal_id bigint NOT NULL,
    page_number int,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (deal_book_content_id)
);

ALTER TABLE public.deal_book_content OWNER TO talool;

CREATE SEQUENCE deal_book_content_deal_book_content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.deal_book_content_deal_book_content_id_seq OWNER TO talool;
ALTER SEQUENCE deal_book_content_deal_book_content_id_seq OWNED BY deal_book_content.deal_book_content_id;

ALTER TABLE ONLY deal_book_content ALTER COLUMN deal_book_content_id SET DEFAULT nextval('deal_book_content_deal_book_content_id_seq'::regclass);
ALTER TABLE ONLY deal_book_content ADD CONSTRAINT "FK_DealBookContent_DealBook" FOREIGN KEY (deal_book_id) REFERENCES deal_book(deal_book_id);
CREATE INDEX deal_book_content_deal_book_id_idx ON deal_book_content (deal_book_id);

ALTER TABLE ONLY deal_book_content ADD CONSTRAINT "FK_DealBookContent_MerchantDeal" FOREIGN KEY (merchant_deal_id) 
REFERENCES merchant_deal(merchant_deal_id);
CREATE INDEX deal_book_content_merchant_deal_id_idx ON deal_book_content (merchant_deal_id);

CREATE TABLE deal_book_purchase (
  	deal_book_purchase_id bigint NOT NULL,
    deal_book_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (deal_book_purchase_id)
);

ALTER TABLE public.deal_book_purchase OWNER TO talool;
CREATE SEQUENCE deal_book_purchase_deal_book_purchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.deal_book_purchase_deal_book_purchase_id_seq OWNER TO talool;
ALTER SEQUENCE deal_book_purchase_deal_book_purchase_id_seq OWNED BY deal_book_purchase.deal_book_purchase_id;
ALTER TABLE ONLY deal_book_purchase ALTER COLUMN deal_book_purchase_id SET DEFAULT nextval('deal_book_purchase_deal_book_purchase_id_seq'::regclass);
ALTER TABLE ONLY deal_book_content ADD CONSTRAINT "FK_DealBookPurchase_DealBook" FOREIGN KEY (deal_book_id) REFERENCES deal_book(deal_book_id);
CREATE INDEX deal_book_purchase_deal_book_id_seq ON deal_book_purchase (deal_book_id);
ALTER TABLE ONLY deal_book_purchase ADD CONSTRAINT "FK_DealBookPurchase_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);
CREATE INDEX deal_book_purchase_customer_id_seq ON deal_book_purchase (customer_id);

CREATE TABLE merchant_deal_redeemed (
	merchant_deal_redeemed_id bigint NOT NULL,
    merchant_deal_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    latitude double precision,
    longitude double precision,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (merchant_deal_redeemed_id)
);

ALTER TABLE public.merchant_deal_redeemed OWNER TO talool;
CREATE SEQUENCE merchant_deal_redeemed_merchant_deal_redeemed_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.merchant_deal_redeemed_merchant_deal_redeemed_id_seq OWNER TO talool;
ALTER SEQUENCE merchant_deal_redeemed_merchant_deal_redeemed_id_seq OWNED BY merchant_deal_redeemed.merchant_deal_redeemed_id;


ALTER TABLE ONLY merchant_deal_redeemed ALTER COLUMN merchant_deal_redeemed_id SET DEFAULT nextval('merchant_deal_redeemed_merchant_deal_redeemed_id_seq'::regclass);
ALTER TABLE ONLY merchant_deal_redeemed ADD CONSTRAINT "FK_MerchantDealRedeemed_MerchantDeal" FOREIGN KEY (merchant_deal_id) 
   REFERENCES merchant_deal(merchant_deal_id);
ALTER TABLE ONLY merchant_deal_redeemed ADD CONSTRAINT "FK_MerchantDealRedeemed_Customer" FOREIGN KEY (customer_id) 
   REFERENCES customer(customer_id);
CREATE INDEX merchant_deal_redeemed_merchant_deal_id_idx ON merchant_deal_redeemed (merchant_deal_id);
CREATE INDEX merchant_deal_redeemed_cutomer_id_idx ON merchant_deal_redeemed (customer_id);
CREATE INDEX merchant_deal_latitude_idx ON merchant_deal_redeemed (latitude);
CREATE INDEX merchant_deal_longitude_idx ON merchant_deal_redeemed (longitude);

ALTER TABLE ONLY address ALTER COLUMN address_id SET DEFAULT nextval('address_address_id_seq'::regclass);

CREATE UNIQUE INDEX address_idx ON address (address1,address2,city,state_province_county,zip,country);

ALTER TABLE ONLY customer ALTER COLUMN customer_id SET DEFAULT nextval('customer_customer_id_seq'::regclass);

ALTER TABLE ONLY address  ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);

CREATE TRIGGER update_merchant_update_dt BEFORE UPDATE ON merchant FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_customer_update_dt BEFORE UPDATE ON customer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_address_update_dt BEFORE UPDATE ON address FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_socal_account_update_dt BEFORE UPDATE ON social_account FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_deal_book_update_dt BEFORE UPDATE ON deal_book FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_deal_book_content_dt BEFORE UPDATE ON deal_book_content FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_deal_book_purchase_update_dt BEFORE UPDATE ON deal_book_purchase FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_merchant_deal_update_dt BEFORE UPDATE ON merchant_deal FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_merchant_deal_redeemed_update_dt BEFORE UPDATE ON merchant_deal_redeemed FOR EACH ROW EXECUTE PROCEDURE update_dt_column();



REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO talool;


REVOKE ALL ON FUNCTION update_dt_column() FROM PUBLIC;
REVOKE ALL ON FUNCTION update_dt_column() FROM talool;
GRANT ALL ON FUNCTION update_dt_column() TO talool;


REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM talool;
GRANT ALL ON TABLE address TO talool;


REVOKE ALL ON SEQUENCE address_address_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE address_address_id_seq FROM talool;
GRANT ALL ON SEQUENCE address_address_id_seq TO talool;



REVOKE ALL ON TABLE customer FROM PUBLIC;
REVOKE ALL ON TABLE customer FROM talool;
GRANT ALL ON TABLE customer TO talool;


REVOKE ALL ON SEQUENCE customer_customer_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE customer_customer_id_seq FROM talool;
GRANT ALL ON SEQUENCE customer_customer_id_seq TO talool;




REVOKE ALL ON TABLE merchant FROM PUBLIC;
REVOKE ALL ON TABLE merchant FROM talool;
GRANT ALL ON TABLE merchant TO talool;



REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM talool;
GRANT ALL ON SEQUENCE merchant_merchant_id_seq TO talool;



