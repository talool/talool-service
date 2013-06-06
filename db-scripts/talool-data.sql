--------------------------------------------------------------
--  USED AS A SEED ONLY FOR DEVELOPMENT. 
--  THE BELOW IS FRAGILE, PAY SPECIAL ATTENTION TO THE SEEDs
--------------------------------------------------------------
\set ON_ERROR_STOP on;

-- insert socials
INSERT INTO social_network(name, website, api_url)
    VALUES ( 'Facebook', 'http://www.facebook.com', 'https://api.facebook.com');

INSERT INTO social_network(name, website, api_url)

    VALUES ( 'Twitter', 'http://www.twitter.com', 'https://api.twitter.com');
 INSERT INTO social_network(name, website, api_url)

    VALUES ( 'Pinterest', 'http://www.pinterest.com', 'https://api.pinterest.com');

-- categories

select add_category_tag('Food','Breakfast');
select add_category_tag('Food','Lunch');
select add_category_tag('Food','Dinner');
select add_category_tag('Food','Burgers');
select add_category_tag('Food','Coffee');
select add_category_tag('Food','Bagels');
select add_category_tag('Food','Pizza');
select add_category_tag('Food','Pasta');
select add_category_tag('Food','Burritos');
select add_category_tag('Food','Cafe');
select add_category_tag('Food','Seafood');
select add_category_tag('Food','Sushi');
select add_category_tag('Food','Fish');
select add_category_tag('Food','Gluten-free');
select add_category_tag('Food','Ice Cream');
select add_category_tag('Food','Yogurt');
select add_category_tag('Food','Juice Bar');
select add_category_tag('Food','Gelato');
select add_category_tag('Food','Salad');
select add_category_tag('Food','Ramen / Noodel House');
select add_category_tag('Food','Steakhouse');
select add_category_tag('Food','Taco');
select add_category_tag('Food','Tapas');
select add_category_tag('Food','Tea');
select add_category_tag('Food','Vegetarian / Vegan');
select add_category_tag('Food','Wings');
select add_category_tag('Food','Wine');
select add_category_tag('Food','Cocktails');
select add_category_tag('Food','Brewery');

select add_category_tag('Shopping Services','Antique Shop');
select add_category_tag('Shopping Services','Arts & Crafts Store');
select add_category_tag('Shopping Services','Bike Shop');
select add_category_tag('Shopping Services','Board Shop');
select add_category_tag('Shopping Services','Bookstore');
select add_category_tag('Shopping Services','Bridal Shop');
select add_category_tag('Shopping Services','Salon');
select add_category_tag('Shopping Services','Dry Cleaning');

select add_category_tag('Fun','Arcade');
select add_category_tag('Fun','Games');
select add_category_tag('Fun','Amusement Center');
select add_category_tag('Fun','Kids / Children');
select add_category_tag('Fun','Pets');
select add_category_tag('Fun','Dogs');
select add_category_tag('Fun','Cats');
select add_category_tag('Fun','Golf');

select add_category_tag('Nightlife','Bar');
select add_category_tag('Nightlife','Beer');
select add_category_tag('Nightlife','Cocktails');
select add_category_tag('Nightlife','Sports Bar');
select add_category_tag('Nightlife','Dance Club');
select add_category_tag('Nightlife','Live Music');
select add_category_tag('Nightlife','Nightclub');
select add_category_tag('Nightlife','Pub');
select add_category_tag('Nightlife','Sake');
select add_category_tag('Nightlife','Speakeasy');
select add_category_tag('Nightlife','Dancing');

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('christopher.justin@gmail.com', (select md5('pass123')), 'Chris', 'Lintz', 'M');

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('doug@talool.com', (select md5('pass123')), 'Doug', 'Mccuen', 'M');      

-------------- Talool Merchant/Address/Accounts ------------------
INSERT INTO address (address1,address2,city,state_province_county,zip,country)
       VALUES ('1267 Lafayette St.','Unit 504','Denver','CO','80218','US');
   	
INSERT INTO merchant (merchant_name) VALUES ('Talool');

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address_id)
        VALUES( (select merchant_id from merchant where merchant_name='Talool'),
                'team@talool.com','http://www.talool.com','720-446-6075',(select max(address_id) from address));
          
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'doug@talool.com',(select md5('pass123')),'CEO',true);     
       
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'chris@talool.com',(select md5('pass123')),'CTO',true);     
       
--------------- --PaybackBook Merchant/Address/Accounts ----------------
INSERT INTO address (address1,address2,city,state_province_county,zip,country)
       VALUES ('6715 NE 63rd St.','PO Box 195','Vancouver','WA','98661','US');

INSERT INTO merchant (merchant_name) VALUES ('Payback Book');

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address_id,geom)
        VALUES(  (select merchant_id from merchant where merchant_name='Payback Book'),
          		  'paybackbook@aol.com','http://www.paybackbook.com','1.360.699.1252',
          		  (select address_id from address where address1='6715 NE 63rd St.'),
          		  ST_GeomFromText('POINT(-122.602628 45.667817)', 4326));

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Payback Book'),'paybackbook@aol.com',(select md5('pass123')),'Owner',true);     
       
---------------------The Kitche Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('1039 Pearl St','Boulder','CO','80302','US');

INSERT INTO merchant (merchant_name,category_id) 
       VALUES ('The Kitchen', (select category_id from category where category_name='Food'));

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address_id,geom)
        VALUES(  (select merchant_id from merchant where merchant_name='The Kitchen'),
			      'info@thekitchencafe.com','http://thekitchencommunity.com','303.544.5973',
       	 		  (select address_id from address where address1='1039 Pearl St'),
       	 		  ST_GeomFromText('POINT(-105.281686 40.017663)', 4326));
      

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='The Kitchen'),'merle@thekitchencafe.com',(select md5('pass123')),'CFO',true);     

--------------------- Centro Merchant/Address/Accounts ----------------
INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('950 Pearl St','Boulder','CO','80302','US');

INSERT INTO merchant (merchant_name,category_id) 
       VALUES ('Centro Latin Kitchen',(select category_id from category where category_name='Food'));
             
INSERT INTO merchant_location (merchant_id,email,website_url,phone,address_id,geom)
        VALUES( (select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),
        		'info@centrolatinkitchen.com','http://www.centrolatinkitchen.com','303.442.7771',
        		(select address_id from address where address1='950 Pearl St'),
        		ST_GeomFromText('POINT(-105.2841748 40.0169992)', 4326));
       
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
                (select customer_id from customer where email='doug@talool.com'));

INSERT INTO deal_offer_purchase (deal_offer_id,customer_id)         
        VALUES ( (select deal_offer_id from deal_offer where title='Payback Book Test Book #1'),  
                 (select customer_id from customer where email='doug@talool.com')); 
              

-- select * from category c, tag as t, tag as cat_t  where c.category_tag_id=cat_t.tag_id and c.tag_id=t.tag_id order by c.category_tag_id;           
               