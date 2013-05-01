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
INSERT INTO acquire_status(status) VALUES ('REDEEMED');
INSERT INTO acquire_status(status) VALUES ('REJECTED_CUSTOMER_SHARE');
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
   	
INSERT INTO merchant (merchant_name) VALUES ('Talool');

INSERT INTO merchant_location (merchant_id,is_primary,email,website_url,logo_url,phone,address_id)
        VALUES( (select merchant_id from merchant where merchant_name='Talool'),true,
                'team@talool.com','http://www.talool.com','','720-446-6075',(select max(address_id) from address));
          
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'doug@talool.com',(select md5('pass123')),'CEO',true);     
       
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'chris@talool.com',(select md5('pass123')),'CTO',true);     
       
--------------- --PaybackBook Merchant/Address/Accounts ----------------
INSERT INTO address (address1,address2,city,state_province_county,zip,country)
       VALUES ('6715 NE 63rd St.','PO Box 195','Vancouver','WA','98661','US');

INSERT INTO merchant (merchant_name) VALUES ('Payback Book');

INSERT INTO merchant_location (merchant_id,is_primary,email,website_url,logo_url,phone,address_id)
        VALUES(  (select merchant_id from merchant where merchant_name='Payback Book'),true,
          		  'paybackbook@aol.com','http://www.paybackbook.com','','1.360.699.1252',
          		  (select address_id from address where address1='6715 NE 63rd St.'));

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Payback Book'),'paybackbook@aol.com',(select md5('pass123')),'Owner',true);     
       
---------------------The Kitche Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('1039 Pearl St','Boulder','CO','80302','US');

INSERT INTO merchant (merchant_name) VALUES ('The Kitchen');

INSERT INTO merchant_location (merchant_id,is_primary,email,website_url,logo_url,phone,address_id)
        VALUES(  (select merchant_id from merchant where merchant_name='The Kitchen'),true,
			      'info@thekitchencafe.com','http://thekitchencommunity.com','','303.544.5973',
       	 		  (select address_id from address where address1='1039 Pearl St'));
      

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='The Kitchen'),'merle@thekitchencafe.com',(select md5('pass123')),'CFO',true);     

--------------------- Centro Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('950 Pearl St','Boulder','CO','80302','US');

INSERT INTO merchant (merchant_name) VALUES ('Centro Latin Kitchen');
             
INSERT INTO merchant_location (merchant_id,is_primary,email,website_url,logo_url,phone,address_id)
        VALUES( (select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),true,
        		'info@centrolatinkitchen.com','http://www.centrolatinkitchen.com','','303.442.7771',
        		(select address_id from address where address1='950 Pearl St'));
       
---------Test Deal Offers & Deals -------------------------------   

INSERT INTO deal_offer( merchant_id, created_by_merchant_account_id,updated_by_merchant_account_id, 
                        deal_type,title,summary,code,price,is_active )
        VALUES( (select merchant_id from merchant where merchant_name='Payback Book'),
                (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
                (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
                'PAID_BOOK','Payback Book Test Book #1','Payback Book Test Book #1','payback-code12345','20.00',true);
                
INSERT INTO deal_offer( merchant_id, created_by_merchant_account_id,updated_by_merchant_account_id, 
                        deal_type,title,summary,code,price,is_active )
        VALUES( (select merchant_id from merchant where merchant_name='The Kitchen'),
                (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
                (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
                'FREE_BOOK','The Kitchen Test Book #1','The Kitchen Test Book #1','kitchen-code12345','0.00',true);
             
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),
               (select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               '1/2 Off Drinks', '1/2 Off Drinks', '1/2 Off Drinks','centro-12345','2013/12/29',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),
               (select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               '10 Bottles of Wine for $1', '10 Bottles of Wine for $1', '10 Bottles of Wine for $1','centro-baga','2013/12/29',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               'Free Wine Tasting', 'Free Wine Tasting', 'Free Wine Tasting','kitchen-asdas','2015/11/20',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               (select merchant_account_id from merchant_account where email='paybackbook@aol.com'),
               '2 for 1 Lunch', '2 for 1 Lunch', '2 for 1 Lunch','kitchen-bdhdf','2015/11/20',true);    

-------- Kitchen deal offers ----
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               'Free Garlic Fries', 'Free Garlic Fries with purchase of lunch or dinner', 'Free Garlic Fries with purchase of lunch or dinner',
               'kitchen-881373','2013/12/29',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               '1/2 Off Wine', '1/2 Off Wine', '1/2 Off Wine',
               'kitchen-87s1373','2013/12/29',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               'Free Drinks at Community Event','Free Drinks at Community Event', 'Free Drinks at Community Event',
               'kitchen-651373','2013/12/29',true);
               
INSERT INTO deal (deal_offer_id,merchant_id,created_by_merchant_account_id,updated_by_merchant_account_id,
                  title, summary, details, code, expires, is_active)
       VALUES( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),
               (select merchant_id from merchant where merchant_name='The Kitchen'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               (select merchant_account_id from merchant_account where email='merle@thekitchencafe.com'),
               'Buy 1 Get 1 Free Lunch', 'Buy 1 Get 1 Free Lunch', 'Buy 1 Get 1 Free Lunch (M-F only)',
               'kitchen-a1373','2013/12/29',true);
                  

------ Purchase Deals ------------

INSERT INTO deal_offer_purchase (deal_offer_id,customer_id) 
       VALUES ( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),  
                (select customer_id from customer where email='christopher.justin@gmail.com'));

INSERT INTO deal_offer_purchase (deal_offer_id,customer_id) 
       VALUES ( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),  
                (select customer_id from customer where email='christopher.justin@gmail.com'));               

INSERT INTO deal_offer_purchase (deal_offer_id,customer_id) 
       VALUES ( (select deal_offer_id from deal_offer where title='The Kitchen Test Book #1'),  
                (select customer_id from customer where email='douglasmccuen@gmail.com'));

INSERT INTO deal_offer_purchase (deal_offer_id,customer_id)         
        VALUES ( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),  
                 (select customer_id from customer where email='douglasmccuen@gmail.com')); 


