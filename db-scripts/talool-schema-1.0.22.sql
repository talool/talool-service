
BEGIN;

CREATE TABLE device_presence (
    device_presence_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    device_id character varying(36) NOT NULL,
    device_type character varying(16),
    device_os_version character varying(16),
    device_token character varying(216),
    talool_version character varying(16),
    user_agent character varying(96) NOT NULL,
    location geometry(POINT,4326),
	city character varying(32),
	state character(2),
	zip character varying(16),
	country character(2),
	ip character varying(45),
	update_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(device_presence_id),
    UNIQUE (customer_id,device_id)
);

ALTER TABLE public.device_presence OWNER TO talool;
ALTER TABLE ONLY device_presence ADD CONSTRAINT "FK_DevicePresence_Customer" FOREIGN KEY (customer_id) REFERENCES customer(customer_id);

CREATE TRIGGER device_presence_update_dt BEFORE UPDATE ON device_presence FOR EACH ROW EXECUTE PROCEDURE update_dt_column();
COMMIT;

