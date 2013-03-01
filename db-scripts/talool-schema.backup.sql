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


CREATE DATABASE talool WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US' LC_CTYPE = 'en_US';


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

--
-- Name: update_insert_customer_loyalty(); Type: FUNCTION; Schema: public; Owner: talool
--

CREATE FUNCTION update_insert_customer_loyalty() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  loyalty_summary customer_loyalty_summary%ROWTYPE;
BEGIN
  SELECT INTO loyalty_summary * FROM customer_loyalty_summary WHERE loyalty_program_id= NEW.loyalty_program_id 
      AND customer_id=NEW.customer_id;

        IF loyalty_summary IS NULL THEN
          INSERT INTO public.customer_loyalty_summary(loyalty_program_id, customer_id, lifetime_spent, spent, visits, lifetime_visits)
	         VALUES(NEW.loyalty_program_id, NEW.customer_id, NEW.amount, NEW.amount, 1, 1) ;
        ELSE
             UPDATE public.customer_loyalty_summary SET visits=visits+1, lifetime_visits=lifetime_visits+1,
                      spent=spent+NEW.amount, lifetime_spent=lifetime_spent+NEW.amount;
 
         END IF;
  RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_insert_customer_loyalty() OWNER TO talool;

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
    create_dt timestamp NOT NULL DEFAULT NOW(),
    update_dt timestamp NOT NULL DEFAULT NOW()
);


ALTER TABLE public.address OWNER TO talool;

--
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

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


--
-- Name: customer; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE customer (
    customer_id bigint NOT NULL,
    first_name character varying(64) NOT NULL,
    last_name character varying(64) NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(32) NOT NULL,
    address_id bigint NULL,
    facebook character varying(64),
    twitter character varying(64),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.customer OWNER TO talool;

CREATE UNIQUE INDEX customer_email_idx ON customer (email);

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
-- Name: customer_loyalty_action; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE customer_loyalty_action (
    customer_loyalty_action_id bigint NOT NULL,
    loyalty_program_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    amount numeric(10,2),
    action_type character varying(25) NOT NULL,
    notes character varying(4),
    create_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.customer_loyalty_action OWNER TO talool;

--
-- Name: customer_loyalty_action_customer_loyalty_action_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE customer_loyalty_action_customer_loyalty_action_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_loyalty_action_customer_loyalty_action_id_seq OWNER TO talool;

--
-- Name: customer_loyalty_action_customer_loyalty_action_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE customer_loyalty_action_customer_loyalty_action_id_seq OWNED BY customer_loyalty_action.customer_loyalty_action_id;


--
-- Name: customer_loyalty_summary; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE customer_loyalty_summary (
    loyalty_program_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    lifetime_spent numeric(10,2),
    spent numeric(10,2),
    visits bigint NOT NULL,
    lifetime_visits bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.customer_loyalty_summary OWNER TO talool;

--
-- Name: customer_property; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE customer_property (
    customer_property_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    property_id bigint NOT NULL,
    property_value character varying(128) NOT NULL,
    is_active boolean NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.customer_property OWNER TO talool;

--
-- Name: customer_property_customer_property_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE customer_property_customer_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_property_customer_property_id_seq OWNER TO talool;

--
-- Name: customer_property_customer_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE customer_property_customer_property_id_seq OWNED BY customer_property.customer_property_id;


--
-- Name: loyalty_action_type; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE loyalty_action_type (
    loyalty_action_type_id bigint NOT NULL,
    type character varying(64) NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.loyalty_action_type OWNER TO talool;

--
-- Name: loyalty_action_type_loyalty_action_type_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE loyalty_action_type_loyalty_action_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.loyalty_action_type_loyalty_action_type_id_seq OWNER TO talool;

--
-- Name: loyalty_action_type_loyalty_action_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE loyalty_action_type_loyalty_action_type_id_seq OWNED BY loyalty_action_type.loyalty_action_type_id;


--
-- Name: loyalty_program; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE loyalty_program (
    loyalty_program_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    loyalty_type_id bigint NOT NULL,
    name character varying(128),
    description character varying(256),
    start_dt timestamp without time zone,
    end_dt timestamp without time zone,
    notes character varying(256),
    rules character varying(256),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.loyalty_program OWNER TO talool;

--
-- Name: loyalty_program_loyalty_program_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE loyalty_program_loyalty_program_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.loyalty_program_loyalty_program_id_seq OWNER TO talool;

--
-- Name: loyalty_program_loyalty_program_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE loyalty_program_loyalty_program_id_seq OWNED BY loyalty_program.loyalty_program_id;


--
-- Name: loyalty_type; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE loyalty_type (
    loyalty_type_id bigint NOT NULL,
    type character varying(25) NOT NULL,
    notes character varying(4),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.loyalty_type OWNER TO talool;

--
-- Name: loyalty_type_loyalty_type_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE loyalty_type_loyalty_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.loyalty_type_loyalty_type_id_seq OWNER TO talool;

--
-- Name: loyalty_type_loyalty_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE loyalty_type_loyalty_type_id_seq OWNED BY loyalty_type.loyalty_type_id;


--
-- Name: merchant; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE merchant (
    merchant_id bigint NOT NULL,
    name character varying(64) NOT NULL,
    is_active boolean NOT NULL,
    talool_user_id bigint NOT NULL,
    facebook character varying(64),
    twitter character varying(64),
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant OWNER TO talool;

--
-- Name: merchant_location; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE merchant_location (
    merchant_id bigint NOT NULL,
    address_id bigint NOT NULL,
    is_active boolean NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant_location OWNER TO talool;

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
-- Name: merchant_phone; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE merchant_phone (
    merchant_id bigint NOT NULL,
    phone_id bigint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant_phone OWNER TO talool;

--
-- Name: merchant_property; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE merchant_property (
    merchant_property_id bigint NOT NULL,
    merchant_id bigint NOT NULL,
    property_id bigint NOT NULL,
    property_value character varying(128),
    is_active boolean NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.merchant_property OWNER TO talool;

--
-- Name: merchant_property_merchant_property_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE merchant_property_merchant_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.merchant_property_merchant_property_id_seq OWNER TO talool;

--
-- Name: merchant_property_merchant_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE merchant_property_merchant_property_id_seq OWNED BY merchant_property.merchant_property_id;


--
-- Name: phone; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE phone (
    phone_id bigint NOT NULL,
    phone_type character varying(64) NOT NULL,
    phone_number timestamp without time zone NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    update_dt timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.phone OWNER TO talool;

--
-- Name: phone_phone_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE phone_phone_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.phone_phone_id_seq OWNER TO talool;

--
-- Name: phone_phone_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE phone_phone_id_seq OWNED BY phone.phone_id;


--
-- Name: property; Type: TABLE; Schema: public; Owner: talool; Tablespace: 
--

CREATE TABLE property (
    property_id bigint NOT NULL,
    property_type character varying(25) NOT NULL,
    property_name character varying(25) NOT NULL,
    is_active boolean NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    update_dt timestamp without time zone NOT NULL
);


ALTER TABLE public.property OWNER TO talool;

--
-- Name: property_property_id_seq; Type: SEQUENCE; Schema: public; Owner: talool
--

CREATE SEQUENCE property_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_property_id_seq OWNER TO talool;

--
-- Name: property_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: talool
--

ALTER SEQUENCE property_property_id_seq OWNED BY property.property_id;



--
-- Name: address_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY address ALTER COLUMN address_id SET DEFAULT nextval('address_address_id_seq'::regclass);

CREATE UNIQUE INDEX address_idx ON address
(address1,address2,city,state_province_county,zip,country);


--
-- Name: customer_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer ALTER COLUMN customer_id SET DEFAULT nextval('customer_customer_id_seq'::regclass);


--
-- Name: customer_loyalty_action_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_loyalty_action ALTER COLUMN customer_loyalty_action_id SET DEFAULT nextval('customer_loyalty_action_customer_loyalty_action_id_seq'::regclass);


--
-- Name: customer_property_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_property ALTER COLUMN customer_property_id SET DEFAULT nextval('customer_property_customer_property_id_seq'::regclass);


--
-- Name: loyalty_action_type_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY loyalty_action_type ALTER COLUMN loyalty_action_type_id SET DEFAULT nextval('loyalty_action_type_loyalty_action_type_id_seq'::regclass);


--
-- Name: loyalty_program_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY loyalty_program ALTER COLUMN loyalty_program_id SET DEFAULT nextval('loyalty_program_loyalty_program_id_seq'::regclass);


--
-- Name: loyalty_type_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY loyalty_type ALTER COLUMN loyalty_type_id SET DEFAULT nextval('loyalty_type_loyalty_type_id_seq'::regclass);


--
-- Name: merchant_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant ALTER COLUMN merchant_id SET DEFAULT nextval('merchant_merchant_id_seq'::regclass);


--
-- Name: merchant_property_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_property ALTER COLUMN merchant_property_id SET DEFAULT nextval('merchant_property_merchant_property_id_seq'::regclass);


--
-- Name: phone_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY phone ALTER COLUMN phone_id SET DEFAULT nextval('phone_phone_id_seq'::regclass);


--
-- Name: property_id; Type: DEFAULT; Schema: public; Owner: talool
--

ALTER TABLE ONLY property ALTER COLUMN property_id SET DEFAULT nextval('property_property_id_seq'::regclass);


--
-- Name: talool_user_id; Type: DEFAULT; Schema: public; Owner: talool
--

--
-- Name: address_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);



--
-- Name: customer_loyalty_action_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY customer_loyalty_action
    ADD CONSTRAINT customer_loyalty_action_pkey PRIMARY KEY (customer_loyalty_action_id);


--
-- Name: customer_loyalty_summary_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY customer_loyalty_summary
    ADD CONSTRAINT customer_loyalty_summary_pkey PRIMARY KEY (loyalty_program_id, customer_id);


--
-- Name: customer_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);


--
-- Name: customer_property_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY customer_property
    ADD CONSTRAINT customer_property_pkey PRIMARY KEY (customer_property_id);


--
-- Name: loyalty_program_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY loyalty_program
    ADD CONSTRAINT loyalty_program_pkey PRIMARY KEY (loyalty_program_id);


--
-- Name: loyalty_type_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY loyalty_type
    ADD CONSTRAINT loyalty_type_pkey PRIMARY KEY (loyalty_type_id);


--
-- Name: merchant_address_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant_location
    ADD CONSTRAINT merchant_address_pkey PRIMARY KEY (merchant_id, address_id);


--
-- Name: merchant_phone_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant_phone
    ADD CONSTRAINT merchant_phone_pkey PRIMARY KEY (merchant_id, phone_id);


--
-- Name: merchant_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant
    ADD CONSTRAINT merchant_pkey PRIMARY KEY (merchant_id);


--
-- Name: merchant_property_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY merchant_property
    ADD CONSTRAINT merchant_property_pkey PRIMARY KEY (merchant_property_id);


--
-- Name: phone_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY phone
    ADD CONSTRAINT phone_pkey PRIMARY KEY (phone_id);


--
-- Name: property_pkey; Type: CONSTRAINT; Schema: public; Owner: talool; Tablespace: 
--

ALTER TABLE ONLY property
    ADD CONSTRAINT property_pkey PRIMARY KEY (property_id);


--
-- Name: customer_loyalty_action_insert; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER customer_loyalty_action_insert AFTER INSERT ON customer_loyalty_action FOR EACH ROW EXECUTE PROCEDURE update_insert_customer_loyalty();


--
-- Name: update_merchant_update_dt; Type: TRIGGER; Schema: public; Owner: talool
--

CREATE TRIGGER update_merchant_update_dt BEFORE UPDATE ON merchant FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_customer_update_dt BEFORE UPDATE ON customer FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_merchant_location_update_dt BEFORE UPDATE ON merchant_location FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_address_update_dt BEFORE UPDATE ON address FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_phone_update_dt BEFORE UPDATE ON phone FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
CREATE TRIGGER update_merchant_phone_update_dt BEFORE UPDATE ON merchant_phone FOR EACH ROW EXECUTE PROCEDURE update_dt_column();

--
-- Name: FK_CustomerLoyaltySummary_LoyaltyProgram; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_loyalty_summary
    ADD CONSTRAINT "FK_CustomerLoyaltySummary_LoyaltyProgram" FOREIGN KEY (loyalty_program_id) REFERENCES loyalty_program(loyalty_program_id);


ALTER TABLE ONLY customer
    ADD CONSTRAINT "FK_Customer_Address" FOREIGN KEY (address_id)
REFERENCES address(address_id);

--
-- Name: FK_CustomerLoyalty_ActionCustomer; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_loyalty_action
    ADD CONSTRAINT "FK_CustomerLoyalty_ActionCustomer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: FK_CustomerLoyalty_ActionLoyaltyProgram; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_loyalty_action
    ADD CONSTRAINT "FK_CustomerLoyalty_ActionLoyaltyProgram" FOREIGN KEY (loyalty_program_id) REFERENCES loyalty_program(loyalty_program_id);


--
-- Name: FK_CustomerProperty_Customer; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_property
    ADD CONSTRAINT "FK_CustomerProperty_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: FK_CustomerProperty_Property; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_property
    ADD CONSTRAINT "FK_CustomerProperty_Property" FOREIGN KEY (property_id) REFERENCES property(property_id);


--
-- Name: FK_Customer_CustomerAddress; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

--
-- Name: FK_Customer_CustomerLoyaltySummary; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY customer_loyalty_summary
    ADD CONSTRAINT "FK_Customer_CustomerLoyaltySummary" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: FK_LoyaltyProgram_LoyaltyType; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY loyalty_program
    ADD CONSTRAINT "FK_LoyaltyProgram_LoyaltyType" FOREIGN KEY (loyalty_type_id) REFERENCES loyalty_type(loyalty_type_id);


--
-- Name: FK_LoyaltyProgram_Merchant; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY loyalty_program
    ADD CONSTRAINT "FK_LoyaltyProgram_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);


--
-- Name: FK_MerchantPhone_Merchant; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_phone
    ADD CONSTRAINT "FK_MerchantPhone_Merchant" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);


--
-- Name: FK_MerchantProperty_Property; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_property
    ADD CONSTRAINT "FK_MerchantProperty_Property" FOREIGN KEY (property_id) REFERENCES property(property_id);


--
-- Name: FK_Merchant_Address; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_location
    ADD CONSTRAINT "FK_Merchant_Address" FOREIGN KEY (address_id) REFERENCES address(address_id);


--
-- Name: FK_Merchant_MerchantAddress; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_location
    ADD CONSTRAINT "FK_Merchant_MerchantAddress" FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);


--
-- Name: FK_Merchant_MerchantProperty; Type: FK CONSTRAINT; Schema: public; Owner: talool
--

ALTER TABLE ONLY merchant_property
    ADD CONSTRAINT "FK_Merchant_MerchantProperty" FOREIGN KEY (merchant_property_id) REFERENCES merchant(merchant_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: clintz
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM clintz;
GRANT ALL ON SCHEMA public TO clintz;
GRANT ALL ON SCHEMA public TO talool;


--
-- Name: update_dt_column(); Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON FUNCTION update_dt_column() FROM PUBLIC;
REVOKE ALL ON FUNCTION update_dt_column() FROM talool;
GRANT ALL ON FUNCTION update_dt_column() TO talool;


--
-- Name: update_insert_customer_loyalty(); Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON FUNCTION update_insert_customer_loyalty() FROM PUBLIC;
REVOKE ALL ON FUNCTION update_insert_customer_loyalty() FROM talool;
GRANT ALL ON FUNCTION update_insert_customer_loyalty() TO talool;


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
-- Name: customer_loyalty_action; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE customer_loyalty_action FROM PUBLIC;
REVOKE ALL ON TABLE customer_loyalty_action FROM talool;
GRANT ALL ON TABLE customer_loyalty_action TO talool;


--
-- Name: customer_loyalty_action_customer_loyalty_action_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE customer_loyalty_action_customer_loyalty_action_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE customer_loyalty_action_customer_loyalty_action_id_seq FROM talool;
GRANT ALL ON SEQUENCE customer_loyalty_action_customer_loyalty_action_id_seq TO talool;


--
-- Name: customer_loyalty_summary; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE customer_loyalty_summary FROM PUBLIC;
REVOKE ALL ON TABLE customer_loyalty_summary FROM talool;
GRANT ALL ON TABLE customer_loyalty_summary TO talool;


--
-- Name: customer_property; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE customer_property FROM PUBLIC;
REVOKE ALL ON TABLE customer_property FROM talool;
GRANT ALL ON TABLE customer_property TO talool;


--
-- Name: customer_property_customer_property_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE customer_property_customer_property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE customer_property_customer_property_id_seq FROM talool;
GRANT ALL ON SEQUENCE customer_property_customer_property_id_seq TO talool;


--
-- Name: loyalty_action_type; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE loyalty_action_type FROM PUBLIC;
REVOKE ALL ON TABLE loyalty_action_type FROM talool;
GRANT ALL ON TABLE loyalty_action_type TO talool;


--
-- Name: loyalty_action_type_loyalty_action_type_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE loyalty_action_type_loyalty_action_type_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE loyalty_action_type_loyalty_action_type_id_seq FROM talool;
GRANT ALL ON SEQUENCE loyalty_action_type_loyalty_action_type_id_seq TO talool;


--
-- Name: loyalty_program; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE loyalty_program FROM PUBLIC;
REVOKE ALL ON TABLE loyalty_program FROM talool;
GRANT ALL ON TABLE loyalty_program TO talool;


--
-- Name: loyalty_program_loyalty_program_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE loyalty_program_loyalty_program_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE loyalty_program_loyalty_program_id_seq FROM talool;
GRANT ALL ON SEQUENCE loyalty_program_loyalty_program_id_seq TO talool;


--
-- Name: loyalty_type; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE loyalty_type FROM PUBLIC;
REVOKE ALL ON TABLE loyalty_type FROM talool;
GRANT ALL ON TABLE loyalty_type TO talool;


--
-- Name: loyalty_type_loyalty_type_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE loyalty_type_loyalty_type_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE loyalty_type_loyalty_type_id_seq FROM talool;
GRANT ALL ON SEQUENCE loyalty_type_loyalty_type_id_seq TO talool;


--
-- Name: merchant; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE merchant FROM PUBLIC;
REVOKE ALL ON TABLE merchant FROM talool;
GRANT ALL ON TABLE merchant TO talool;


--
-- Name: merchant_location; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE merchant_location FROM PUBLIC;
REVOKE ALL ON TABLE merchant_location FROM talool;
GRANT ALL ON TABLE merchant_location TO talool;


--
-- Name: merchant_merchant_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE merchant_merchant_id_seq FROM talool;
GRANT ALL ON SEQUENCE merchant_merchant_id_seq TO talool;


--
-- Name: merchant_phone; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE merchant_phone FROM PUBLIC;
REVOKE ALL ON TABLE merchant_phone FROM talool;
GRANT ALL ON TABLE merchant_phone TO talool;


--
-- Name: merchant_property; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE merchant_property FROM PUBLIC;
REVOKE ALL ON TABLE merchant_property FROM talool;
GRANT ALL ON TABLE merchant_property TO talool;


--
-- Name: merchant_property_merchant_property_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE merchant_property_merchant_property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE merchant_property_merchant_property_id_seq FROM talool;
GRANT ALL ON SEQUENCE merchant_property_merchant_property_id_seq TO talool;


--
-- Name: phone; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE phone FROM PUBLIC;
REVOKE ALL ON TABLE phone FROM talool;
GRANT ALL ON TABLE phone TO talool;


--
-- Name: phone_phone_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE phone_phone_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE phone_phone_id_seq FROM talool;
GRANT ALL ON SEQUENCE phone_phone_id_seq TO talool;


--
-- Name: property; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON TABLE property FROM PUBLIC;
REVOKE ALL ON TABLE property FROM talool;
GRANT ALL ON TABLE property TO talool;


--
-- Name: property_property_id_seq; Type: ACL; Schema: public; Owner: talool
--

REVOKE ALL ON SEQUENCE property_property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_property_id_seq FROM talool;
GRANT ALL ON SEQUENCE property_property_id_seq TO talool;



