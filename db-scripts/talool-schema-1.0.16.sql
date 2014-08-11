-- DB alters supporting messaging/jobs

BEGIN;

CREATE TYPE job_state AS ENUM ('STOPPED', 'STARTED', 'FINISHED', 'FAILED');

CREATE TABLE messaging_job (
    messaging_job_id bigserial NOT NULL,
    merchant_account_id bigint NOT NULL,
    customer_id UUID NOT NULL,
    job_state job_state NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    scheduled_start_dt timestamp without time zone NOT NULL,
    job_notes character varying(128),
    PRIMARY KEY(messaging_job_id)
 );
 
ALTER TABLE public.messaging_job OWNER TO talool;
ALTER TABLE ONLY messaging_job ADD CONSTRAINT "FK_MessagingJob_MerchantAccount" FOREIGN KEY (merchant_account_id) 
  REFERENCES merchant_account(merchant_account_id);
ALTER TABLE ONLY messaging_job ADD CONSTRAINT "FK_MessagingJob_Customer" FOREIGN KEY (customer_id) 
  REFERENCES customer(customer_id);

CREATE INDEX messaging_job_merchant_account_id_idx ON messaging_job (merchant_account_id);
 
CREATE TABLE messaging_job_stats (
    messaging_job_stats_id bigserial NOT NULL,
    messaging_job_id bigint NOT NULL,
    users_targeted bigint DEFAULT 0 NOT NULL,
    sends bigint DEFAULT 0 NOT NULL,
    email_opens bigint DEFAULT 0 NOT NULL,
    gift_opens bigint DEFAULT 0 NOT NULL, 
    PRIMARY KEY(messaging_job_stats_id)
 );
 
 ALTER TABLE public.messaging_job_stats OWNER TO talool;
 ALTER TABLE ONLY messaging_job_stats ADD CONSTRAINT "FK_MessagingJobStats_MessagingJob" FOREIGN KEY (messaging_job_id) 
  REFERENCES messaging_job(messaging_job_id);
 CREATE INDEX messaging_job_stats_messaging_job_id_idx ON messaging_job_stats (messaging_job_id);
 
CREATE TYPE delivery_status AS ENUM ('PENDING','SUCCESS', 'FAILURE');

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