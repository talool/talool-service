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

CREATE DATABASE talool WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'C';


ALTER DATABASE talool OWNER TO talool;

\connect talool

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
-- Name: account_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE account_type AS ENUM (
    'MER',
    'CUS'
);


ALTER TYPE public.account_type OWNER TO postgres;

--
-- Name: sex_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE sex_type AS ENUM (
    'M',
    'F'
);


ALTER TYPE public.sex_type OWNER TO postgres;

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

--
-- Name: address; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE address (
    address_id bigint NOT NULL,
    address1 character varying(64),
    address2 character varying(64),
    city character varying(64) NOT NULL,
    state_province_county character varying(64) NOT NULL,
    zip character varying(64),
    country character varying(4),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.address OWNER TO talool;

--
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE address_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.address_address_id_seq OWNER TO talool;

--
-- Name: address_address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE address_address_id_seq OWNED BY address.address_id;


--
-- Name: customer; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE customer (
    customer_id bigint NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(32) NOT NULL,
    first_name character varying(64),
    last_name character varying(64),
    sex_t sex_type,
    birth_date date,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.customer OWNER TO talool;

--
-- Name: customer_customer_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE customer_customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_customer_id_seq OWNER TO talool;

--
-- Name: customer_customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE customer_customer_id_seq OWNED BY customer.customer_id;


--
-- Name: deal_book; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

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
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.deal_book OWNER TO talool;

--
-- Name: deal_book_content; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE deal_book_content (
    deal_book_content_id bigint NOT NULL,
    deal_book_id bigint NOT NULL,
    merchant_deal_id bigint NOT NULL,
    page_number integer,
    update_dt timestamp without time zone DEFAULT now() NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.deal_book_content OWNER TO talool;

--
-- Name: deal_book_content_deal_book_content_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE deal_book_content_deal_book_content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.deal_book_content_deal_book_content_id_seq OWNER TO talool;

--
-- Name: deal_book_content_deal_book_content_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE deal_book_content_deal_book_content_id_seq OWNED BY deal_book_content.deal_book_content_id;


--
-- Name: deal_book_deal_book_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE deal_book_deal_book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.deal_book_deal_book_id_seq OWNER TO talool;

--
-- Name: deal_book_deal_book_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE deal_book_deal_book_id_seq OWNED BY deal_book.deal_book_id;


--
-- Name: deal_book_purchase; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE deal_book_purchase (
    deal_book_purchase_id bigint NOT NULL,
    deal_book_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.deal_book_purchase OWNER TO talool;

--
-- Name: deal_book_purchase_deal_book_purchase_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE deal_book_purchase_deal_book_purchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.deal_book_purchase_deal_book_purchase_id_seq OWNER TO talool;

--
-- Name: deal_book_purchase_deal_book_purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE deal_book_purchase_deal_book_purchase_id_seq OWNED BY deal_book_purchase.deal_book_purchase_id;


--
-- Name: merchant; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

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
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant OWNER TO talool;

--
-- Name: merchant_deal; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

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
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant_deal OWNER TO talool;

--
-- Name: merchant_deal_merchant_deal_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE merchant_deal_merchant_deal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.merchant_deal_merchant_deal_id_seq OWNER TO talool;

--
-- Name: merchant_deal_merchant_deal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE merchant_deal_merchant_deal_id_seq OWNED BY merchant_deal.merchant_deal_id;


--
-- Name: merchant_deal_redeemed; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE merchant_deal_redeemed (
    merchant_deal_redeemed_id bigint NOT NULL,
    merchant_deal_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    latitude double precision,
    longitude double precision,
    create_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant_deal_redeemed OWNER TO talool;

--
-- Name: merchant_deal_redeemed_merchant_deal_redeemed_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE merchant_deal_redeemed_merchant_deal_redeemed_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.merchant_deal_redeemed_merchant_deal_redeemed_id_seq OWNER TO talool;

--
-- Name: merchant_deal_redeemed_merchant_deal_redeemed_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE merchant_deal_redeemed_merchant_deal_redeemed_id_seq OWNED BY merchant_deal_redeemed.merchant_deal_redeemed_id;


--
-- Name: merchant_merchant_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE merchant_merchant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.merchant_merchant_id_seq OWNER TO talool;

--
-- Name: merchant_merchant_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE merchant_merchant_id_seq OWNED BY merchant.merchant_id;


--
-- Name: social_account; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE social_account (
    user_id bigint NOT NULL,
    account_t account_type NOT NULL,
    social_network_id bigint NOT NULL,
    login_id character varying(32) NOT NULL,
    token character varying(64),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.social_account OWNER TO talool;

--
-- Name: social_network; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE social_network (
    social_network_id bigint NOT NULL,
    name character varying(32) NOT NULL,
    website character varying(64) NOT NULL,
    api_url character varying(64) NOT NULL
);


ALTER TABLE public.social_network OWNER TO talool;

--
-- Name: social_network_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE social_network_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.social_network_id_seq OWNER TO talool;

--
-- Name: social_network_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE social_network_id_seq OWNED BY social_network.social_network_id;


--
-- Name: address_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY address ALTER COLUMN address_id SET DEFAULT nextval('address_address_id_seq'::regclass);


--
-- Name: customer_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer ALTER COLUMN customer_id SET DEFAULT nextval('customer_customer_id_seq'::regclass);


--
-- Name: deal_book_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book ALTER COLUMN deal_book_id SET DEFAULT nextval('deal_book_deal_book_id_seq'::regclass);


--
-- Name: deal_book_content_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_content ALTER COLUMN deal_book_content_id SET DEFAULT nextval('deal_book_content_deal_book_content_id_seq'::regclass);


--
-- Name: deal_book_purchase_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_purchase ALTER COLUMN deal_book_purchase_id SET DEFAULT nextval('deal_book_purchase_deal_book_purchase_id_seq'::regclass);


--
-- Name: merchant_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant ALTER COLUMN merchant_id SET DEFAULT nextval('merchant_merchant_id_seq'::regclass);


--
-- Name: merchant_deal_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_deal ALTER COLUMN merchant_deal_id SET DEFAULT nextval('merchant_deal_merchant_deal_id_seq'::regclass);


--
-- Name: merchant_deal_redeemed_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_deal_redeemed ALTER COLUMN merchant_deal_redeemed_id SET DEFAULT nextval('merchant_deal_redeemed_merchant_deal_redeemed_id_seq'::regclass);


--
-- Name: social_network_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY social_network ALTER COLUMN social_network_id SET DEFAULT nextval('social_network_id_seq'::regclass);


--
-- Name: address_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- Name: customer_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);


--
-- Name: deal_book_content_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY deal_book_content
    ADD CONSTRAINT deal_book_content_pkey PRIMARY KEY (deal_book_content_id);


--
-- Name: deal_book_merchant_id_title_is_active_key; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY deal_book
    ADD CONSTRAINT deal_book_merchant_id_title_is_active_key UNIQUE (merchant_id, title, is_active);


--
-- Name: deal_book_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY deal_book
    ADD CONSTRAINT deal_book_pkey PRIMARY KEY (deal_book_id);


--
-- Name: deal_book_purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY deal_book_purchase
    ADD CONSTRAINT deal_book_purchase_pkey PRIMARY KEY (deal_book_purchase_id);


--
-- Name: merchant_deal_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant_deal
    ADD CONSTRAINT merchant_deal_pkey PRIMARY KEY (merchant_deal_id);


--
-- Name: merchant_deal_redeemed_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant_deal_redeemed
    ADD CONSTRAINT merchant_deal_redeemed_pkey PRIMARY KEY (merchant_deal_redeemed_id);


--
-- Name: merchant_merchant_name_address_id_key; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant
    ADD CONSTRAINT merchant_merchant_name_address_id_key UNIQUE (merchant_name, address_id);


--
-- Name: merchant_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant
    ADD CONSTRAINT merchant_pkey PRIMARY KEY (merchant_id);


--
-- Name: social_account_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY social_account
    ADD CONSTRAINT social_account_pkey PRIMARY KEY (user_id, account_t, social_network_id);


--
-- Name: social_network_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY social_network
    ADD CONSTRAINT social_network_pkey PRIMARY KEY (social_network_id);


--
-- Name: address_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE UNIQUE INDEX address_idx ON address USING btree (address1, address2, city, state_province_county, zip, country);


--
-- Name: customer_email_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE UNIQUE INDEX customer_email_idx ON customer USING btree (email);


--
-- Name: deal_book_content_deal_book_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_content_deal_book_id_idx ON deal_book_content USING btree (deal_book_id);


--
-- Name: deal_book_content_merchant_deal_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_content_merchant_deal_id_idx ON deal_book_content USING btree (merchant_deal_id);


--
-- Name: deal_book_latitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_latitude_idx ON deal_book USING btree (latitude);


--
-- Name: deal_book_longitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_longitude_idx ON deal_book USING btree (longitude);


--
-- Name: deal_book_merchant_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_merchant_id_idx ON deal_book USING btree (merchant_id);


--
-- Name: deal_book_purchase_customer_id_seq; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_purchase_customer_id_seq ON deal_book_purchase USING btree (customer_id);


--
-- Name: deal_book_purchase_deal_book_id_seq; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX deal_book_purchase_deal_book_id_seq ON deal_book_purchase USING btree (deal_book_id);


--
-- Name: merchant_deal_latitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_deal_latitude_idx ON merchant_deal_redeemed USING btree (latitude);


--
-- Name: merchant_deal_longitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_deal_longitude_idx ON merchant_deal_redeemed USING btree (longitude);


--
-- Name: merchant_deal_merchant_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_deal_merchant_id_idx ON merchant_deal USING btree (merchant_id);


--
-- Name: merchant_deal_redeemed_cutomer_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_deal_redeemed_cutomer_id_idx ON merchant_deal_redeemed USING btree (customer_id);


--
-- Name: merchant_deal_redeemed_merchant_deal_id_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_deal_redeemed_merchant_deal_id_idx ON merchant_deal_redeemed USING btree (merchant_deal_id);


--
-- Name: merchant_email_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE UNIQUE INDEX merchant_email_idx ON merchant USING btree (email);


--
-- Name: merchant_latitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_latitude_idx ON merchant USING btree (latitude);


--
-- Name: merchant_longitude_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_longitude_idx ON merchant USING btree (longitude);


--
-- Name: merchant_name_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE INDEX merchant_name_idx ON merchant USING btree (merchant_name);


--
-- Name: social_network_idx; Type: INDEX; Schema: public; Owner: talool; Tablespace: 
--

CREATE UNIQUE INDEX social_network_idx ON social_network USING btree (name);


--
-- Name: update_address_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_address_update_dt BEFORE UPDATE ON address FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_customer_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_customer_update_dt BEFORE UPDATE ON customer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_deal_book_content_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_deal_book_content_dt BEFORE UPDATE ON deal_book_content FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_deal_book_purchase_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_deal_book_purchase_update_dt BEFORE UPDATE ON deal_book_purchase FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_deal_book_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_deal_book_update_dt BEFORE UPDATE ON deal_book FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_merchant_deal_redeemed_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_merchant_deal_redeemed_update_dt BEFORE UPDATE ON merchant_deal_redeemed FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_merchant_deal_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_merchant_deal_update_dt BEFORE UPDATE ON merchant_deal FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_merchant_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_merchant_update_dt BEFORE UPDATE ON merchant FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: update_socal_account_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_socal_account_update_dt BEFORE UPDATE ON social_account FOR EACH ROW EXECUTE PROCEDURE update_dt_column();


--
-- Name: FK_CustomerSocialAccount_SocialNetwork; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY social_account
    ADD CONSTRAINT "FK_CustomerSocialAccount_SocialNetwork" FOREIGN KEY (social_network_id) REFERENCES social_network(social_network_id);


--
-- Name: FK_DealBookContent_DealBook; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_content
    ADD CONSTRAINT "FK_DealBookContent_DealBook" FOREIGN KEY (deal_book_id) REFERENCES deal_book(deal_book_id);


--
-- Name: FK_DealBookContent_MerchantDeal; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_content
    ADD CONSTRAINT "FK_DealBookContent_MerchantDeal" FOREIGN KEY (merchant_deal_id) REFERENCES merchant_deal(merchant_deal_id);


--
-- Name: FK_DealBookPurchase_Customer; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_purchase
    ADD CONSTRAINT "FK_DealBookPurchase_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: FK_DealBookPurchase_DealBook; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book_content
    ADD CONSTRAINT "FK_DealBookPurchase_DealBook" FOREIGN KEY (deal_book_id) REFERENCES deal_book(deal_book_id);


--
-- Name: FK_DealBook_Merchant; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY deal_book
    ADD CONSTRAINT "FK_DealBook_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);


--
-- Name: FK_MerchantDealRedeemed_Customer; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_deal_redeemed
    ADD CONSTRAINT "FK_MerchantDealRedeemed_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: FK_MerchantDealRedeemed_MerchantDeal; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_deal_redeemed
    ADD CONSTRAINT "FK_MerchantDealRedeemed_MerchantDeal" FOREIGN KEY (merchant_deal_id) REFERENCES merchant_deal(merchant_deal_id);


--
-- Name: FK_MerchantDeal_Merchant; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_deal
    ADD CONSTRAINT "FK_MerchantDeal_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);


--
-- Name: FK_Merchant_Merchant; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant
    ADD CONSTRAINT "FK_Merchant_Merchant" FOREIGN KEY (merchant_parent_id) REFERENCES merchant(merchant_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO talool;


--
-- Name: update_dt_column(); Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON FUNCTION update_dt_column() FROM PUBLIC;
REVOKE ALL ON FUNCTION update_dt_column() FROM talool;
GRANT ALL ON FUNCTION update_dt_column() TO talool;


--
-- Name: address; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM talool;
GRANT ALL ON TABLE address TO talool;


--
-- Name: address_address_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE address_address_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE address_address_id_seq FROM talool;
GRANT ALL ON SEQUENCE address_address_id_seq TO talool;


--
-- Name: customer; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE customer FROM PUBLIC;
REVOKE ALL ON TABLE customer FROM talool;
GRANT ALL ON TABLE customer TO talool;


--
-- Name: customer_customer_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE customer_customer_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE customer_customer_id_seq FROM talool;
GRANT ALL ON SEQUENCE customer_customer_id_seq TO talool;


--
-- Name: merchant; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE merchant FROM PUBLIC;
REVOKE ALL ON TABLE merchant FROM talool;
GRANT ALL ON TABLE merchant TO talool;


--
-- Name: merchant_merchant_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM talool;
GRANT ALL ON SEQUENCE merchant_merchant_id_seq TO talool;


--
-- Name: social_network_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE social_network_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE social_network_id_seq FROM talool;
GRANT ALL ON SEQUENCE social_network_id_seq TO talool;


--
-- PostgreSQL database dump complete
--

