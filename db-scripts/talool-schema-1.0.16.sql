-- DB alters supporting messaging/jobs

BEGIN;

CREATE TYPE job_state AS ENUM ('STOPPED', 'STARTED', 'FINISHED', 'FAILED');

CREATE TYPE delivery_status AS ENUM ('PENDING','SUCCESS', 'FAILURE');

CREATE TABLE messaging_job (
    messaging_job_id bigserial NOT NULL,
    created_by_merchant_account_id bigint NOT NULL,
    from_customer_id UUID NOT NULL,
    job_type character(2) NOT NULL,
    job_state job_state NOT NULL,
    deal_id UUID,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    scheduled_start_dt timestamp without time zone NOT NULL,
    running_update_dt timestamp without time zone,
    job_notes character varying(128),
    users_targeted bigint DEFAULT 0 NOT NULL,
    sends bigint DEFAULT 0 NOT NULL,
    email_opens bigint DEFAULT 0 NOT NULL,
    gift_opens bigint DEFAULT 0 NOT NULL, 
    PRIMARY KEY(messaging_job_id)
 );
 
ALTER TABLE public.messaging_job OWNER TO talool;
ALTER TABLE ONLY messaging_job ADD CONSTRAINT "FK_MessagingJob_MerchantAccount" FOREIGN KEY (created_by_merchant_account_id) 
  REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY messaging_job ADD CONSTRAINT "FK_MessagingJob_Customer" FOREIGN KEY (from_customer_id) 
  REFERENCES customer(customer_id);
ALTER TABLE ONLY messaging_job ADD CONSTRAINT "FK_MessagingJob_Deal" FOREIGN KEY (deal_id) 
  REFERENCES deal(deal_id);

CREATE INDEX messaging_job_merchant_account_id_idx ON messaging_job (merchant_account_id);
CREATE INDEX messaging_job_deal_id_idx ON messaging_job (deal_id);

CREATE TABLE recipient_status (
    recipient_status_id bigserial NOT NULL,
    messaging_job_id bigint NOT NULL,
    customer_id UUID NOT NULL,
    delivery_status delivery_status NOT NULL,
    PRIMARY KEY(recipient_status_id)
 );
 
ALTER TABLE public.recipient_status OWNER TO talool;
 ALTER TABLE ONLY recipient_status ADD CONSTRAINT "FK_RecipientStatus_MessagingJob" FOREIGN KEY (messaging_job_id) 
  REFERENCES messaging_job(messaging_job_id);
ALTER TABLE ONLY recipient_status ADD CONSTRAINT "FK_RecipientStatus_Customer" FOREIGN KEY (customer_id) 
  REFERENCES customer(customer_id);
CREATE INDEX recipient_status_messaging_job_id_idx ON recipient_status (messaging_job_id);
CREATE INDEX recipient_status_customer_id_id_idx ON recipient_status (customer_id);
 
 COMMIT;