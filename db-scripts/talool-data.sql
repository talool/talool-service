--------------------------------------------------------------
--  USED AS A SEED ONLY FOR DEVELOPMENT. 
--  THE BELOW IS FRAGILE, PAY SPECIAL ATTENTION TO THE SEEDs
--------------------------------------------------------------

-- insert socials
INSERT INTO social_network(name, website, api_url)
    VALUES ( 'Facebook', 'http://www.facebook.com', 'https://api.facebook.com');

INSERT INTO social_network(name, website, api_url)

    VALUES ( 'Twitter', 'http://www.twitter.com', 'https://api.twitter.com');
 INSERT INTO social_network(name, website, api_url)

    VALUES ( 'Pinterest', 'http://www.pinterest.com', 'https://api.pinterest.com');

INSERT INTO acquire_status(status) VALUES ('PURCHASED');
INSERT INTO acquire_status(status) VALUES ('ACCEPTED_MERCHANT_SHARE');
INSERT INTO acquire_status(status) VALUES ('ACCEPTED_CUSTOMER_SHARE');
INSERT INTO acquire_status(status) VALUES ('PENDING_ACCEPT_MERCHANT_SHARE');
INSERT INTO acquire_status(status) VALUES ('PENDING_ACCEPT_CUSTOMER_SHARE');

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('christopher.justin@gmail.com', (select md5('pass123')), 'Chris', 'Lintz', 'M');

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('douglasmccuen@gmail.com', (select md5('pass123')), 'Doug', 'Mccuen', 'M');      

-------------- Talool Merchant/Address/Accounts ------------------
INSERT INTO address (address1,address2,city,state_province_county,zip,country)
       VALUES ('1267 Lafayette St.','Unit 504','Denver','CO','80218','US');
   		
INSERT INTO merchant_location (email,website_url,logo_url,phone,address_id)
        VALUES( 'team@talool.com','http://www.talool.com','','720-446-6075',(select max(address_id) from address));
       
INSERT INTO merchant (primary_location_id,merchant_name)
       VALUES ((select max(merchant_location_id) from merchant_location),'Talool');

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select max(merchant_id) from merchant),'doug@talool.com',(select md5('pass123')),'CEO',true);     
       
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select max(merchant_id) from merchant),'chris@talool.com',(select md5('pass123')),'CTO',true);     
       
--------------- --PaybackBook Merchant/Address/Accounts ----------------
INSERT INTO address (address1,address2,city,state_province_county,zip,country)
       VALUES ('6715 NE 63rd St.','PO Box 195','Vancouver','WA','98661','US');
   		
INSERT INTO merchant_location (email,website_url,logo_url,phone,address_id)
        VALUES( 'paybackbook@aol.com','http://www.paybackbook.com','','1.360.699.1252',(select max(address_id) from address));
       
INSERT INTO merchant (primary_location_id,merchant_name)
       VALUES ((select max(merchant_location_id) from merchant_location),'Payback Book');

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select max(merchant_id) from merchant),'paybackbook@aol.com',(select md5('pass123')),'Owner',true);     
       
---------------------The Kitche Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('1039 Pearl St ','Boulder','CO','80302','US');
      
INSERT INTO merchant_location (email,website_url,logo_url,phone,address_id)
        VALUES( 'info@thekitchencafe.com','http://thekitchencommunity.com','','303.544.5973',(select max(address_id) from address));
       
INSERT INTO merchant (primary_location_id,merchant_name)
       VALUES ((select max(merchant_location_id) from merchant_location),'The Kitchen Boulder & [Upstairs]');

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select max(merchant_id) from merchant),'merle@thekitchencafe.com',(select md5('pass123')),'CFO',true);     

--------------------- Centro Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('950 Pearl St','Boulder','CO','80302','US');
      
INSERT INTO merchant_location (email,website_url,logo_url,phone,address_id)
        VALUES( 'info@centrolatinkitchen.com','http://www.centrolatinkitchen.com','','303.442.7771',(select max(address_id) from address));
       
INSERT INTO merchant (primary_location_id,merchant_name)
       VALUES ((select max(merchant_location_id) from merchant_location),'Centro Latin Kitchen');
-----------------------------------------------------------------------   





