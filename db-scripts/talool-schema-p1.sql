
alter table merchant_location alter column country SET DEFAULT 'US';


-- adding chris@talool customer
INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('chris@talool.com', (select md5('pass123')), 'Chris', 'Lintz', 'M');

-- add Vince to talool
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'vince@talool.com',(select md5('pass123')),'VP Ops',true);

ALTER table deal_offer add column location_name character varying(64);
