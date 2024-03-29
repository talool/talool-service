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
INSERT INTO merchant (merchant_name,is_discoverable) VALUES ('Talool',false);

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address1,address2,city,state_province_county,zip,country)
        VALUES( (select merchant_id from merchant where merchant_name='Talool'),
                'team@talool.com','http://www.talool.com','720-446-6075','1267 Lafayette St.','Unit 504','Denver','CO','80218','US');
          
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'doug@talool.com',(select md5('pass123')),'CEO',true);     
       
INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Talool'),'chris@talool.com',(select md5('pass123')),'CTO',true);     
       
--------------- --PaybackBook Merchant/Address/Accounts ----------------
INSERT INTO merchant (merchant_name,is_discoverable) VALUES ('Payback Book',false);

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address1,address2,city,state_province_county,zip,country,geom)
        VALUES(  (select merchant_id from merchant where merchant_name='Payback Book'),
          		  'paybackbook@aol.com','http://www.paybackbook.com','1.360.699.1252',
          		  '6715 NE 63rd St.','PO Box 195','Vancouver','WA','98661','US',
          		  ST_GeomFromText('POINT(-122.602628 45.667817)', 4326));

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='Payback Book'),'paybackbook@aol.com',(select md5('pass123')),'Owner',true);     
       
---------------------The Kitche Merchant/Address/Accounts ----------------
INSERT INTO merchant (merchant_name,category_id) 
       VALUES ('The Kitchen', (select category_id from category where category_name='Food'));

INSERT INTO merchant_location (merchant_id,email,website_url,phone,address1,city,state_province_county,zip,country,geom)
        VALUES(  (select merchant_id from merchant where merchant_name='The Kitchen'),
			      'info@thekitchencafe.com','http://thekitchencommunity.com','303.544.5973',
       	 		 '1039 Pearl St','Boulder','CO','80302','US',
       	 		  ST_GeomFromText('POINT(-105.281686 40.017663)', 4326));
      

INSERT INTO merchant_account (merchant_id,email,password,role_title,allow_deal_creation)
       VALUES ((select merchant_id from merchant where merchant_name='The Kitchen'),'merle@thekitchencafe.com',(select md5('pass123')),'CFO',true);     

--------------------- Centro Merchant/Address/Accounts ----------------
INSERT INTO merchant (merchant_name,category_id) 
       VALUES ('Centro Latin Kitchen',(select category_id from category where category_name='Food'));
             
INSERT INTO merchant_location (merchant_id,email,website_url,phone,address1,city,state_province_county,zip,country,geom)
        VALUES( (select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),
        		'info@centrolatinkitchen.com','http://www.centrolatinkitchen.com','303.442.7771',
        		'950 Pearl St','Boulder','CO','80302','US',
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
               
               
               
-------- Additional Categories --------------
select add_category_tag('Food','BBQ');
select add_category_tag('Food','Family Dining');
select add_category_tag('Food','Fast Food');
select add_category_tag('Food','Fine Dining');
select add_category_tag('Food','Casual Dining');
select add_category_tag('Food','Pub');
select add_category_tag('Food','Snack Shop');
select add_category_tag('Food','Donut');
select add_category_tag('Food','Bakery');
select add_category_tag('Food','Dessert');
select add_category_tag('Food','Salad');
select add_category_tag('Food','American');
select add_category_tag('Food','Latin American');
select add_category_tag('Food','Asian');
select add_category_tag('Food','Argentinian');
select add_category_tag('Food','Brazilian');
select add_category_tag('Food','Italian');
select add_category_tag('Food','Mexican');
select add_category_tag('Food','Cajun');
select add_category_tag('Food','Caribbean');
select add_category_tag('Food','Chinese');
select add_category_tag('Food','Deli');
select add_category_tag('Food','Diner');
select add_category_tag('Food','Dim Sum');
select add_category_tag('Food','Europeen');
select add_category_tag('Food','Ethiopian');
select add_category_tag('Food','Japanese');
select add_category_tag('Food','French');
select add_category_tag('Food','German');
select add_category_tag('Food','Greek');
select add_category_tag('Food','Indian');
select add_category_tag('Food','Korean');
select add_category_tag('Food','Mediterranean');
select add_category_tag('Food','Middle Eastern');
select add_category_tag('Food','Mongolian');
select add_category_tag('Food','Moroccan');
select add_category_tag('Food','Spanish');
select add_category_tag('Food','Thai');
select add_category_tag('Food','Vietnamese');

select add_category_tag('Shopping Services','Camera Store');
select add_category_tag('Shopping Services','Candy Store');
select add_category_tag('Shopping Services','Car Wash');
select add_category_tag('Shopping Services','Clothing Store');
select add_category_tag('Shopping Services','Accessories Store');
select add_category_tag('Shopping Services','Boutique');
select add_category_tag('Shopping Services','Kids Store');
select add_category_tag('Shopping Services','Lingerie Store');
select add_category_tag('Shopping Services','Men’s Store');
select add_category_tag('Shopping Services','Shoe Store');
select add_category_tag('Shopping Services','Women’s Store');
select add_category_tag('Shopping Services','Convenience Store');
select add_category_tag('Shopping Services','Cosmetics Shop');
select add_category_tag('Shopping Services','Department Store');
select add_category_tag('Shopping Services','Design Studio');
select add_category_tag('Shopping Services','Drugstore / Pharmacy');
select add_category_tag('Shopping Services','Electronics Store');
select add_category_tag('Shopping Services','Financial Services');
select add_category_tag('Shopping Services','Legal Services');
select add_category_tag('Shopping Services','Flower Shop');
select add_category_tag('Shopping Services','Butcher');
select add_category_tag('Shopping Services','Cheese Shop');
select add_category_tag('Shopping Services','Farmers Market');
select add_category_tag('Shopping Services','Fish Market');
select add_category_tag('Shopping Services','Gourmet Shop');
select add_category_tag('Shopping Services','Grocery Store');
select add_category_tag('Shopping Services','Liquor Store');
select add_category_tag('Shopping Services','Wine Shop');
select add_category_tag('Shopping Services','Furniture / Home Store');
select add_category_tag('Shopping Services','Garden Center');
select add_category_tag('Shopping Services','Gas Station / Garage');
select add_category_tag('Shopping Services','Gift Shop');
select add_category_tag('Shopping Services','Hardware Store');
select add_category_tag('Shopping Services','Hobby Shop');
select add_category_tag('Shopping Services','Jewelry Store');
select add_category_tag('Shopping Services','Laundry Service');
select add_category_tag('Shopping Services','Music Store');
select add_category_tag('Shopping Services','Nail Salon');
select add_category_tag('Shopping Services','Paper / Office Supplies Store');
select add_category_tag('Shopping Services','Pet Services');
select add_category_tag('Shopping Services','Pet Store');
select add_category_tag('Shopping Services','Photography Lab');
select add_category_tag('Shopping Services','Record Shop');
select add_category_tag('Shopping Services','Barbershop');
select add_category_tag('Shopping Services','Smoke Shop');
select add_category_tag('Shopping Services','Spas / Massages');
select add_category_tag('Shopping Services','Sporting Goods Shop');
select add_category_tag('Shopping Services','Tailor Shops');
select add_category_tag('Shopping Services','Tanning Salon');
select add_category_tag('Shopping Services','Tattoo Parlor');
select add_category_tag('Shopping Services','Thrift / Vintage Store');
select add_category_tag('Shopping Services','Toy / Game Store');
select add_category_tag('Shopping Services','Travel Agency');
select add_category_tag('Shopping Services','Video Game Store');
select add_category_tag('Shopping Services','Video Store');
select add_category_tag('Shopping Services','Resale / Consignment');
select add_category_tag('Shopping Services','Health & Beauty');
select add_category_tag('Shopping Services','Appliance Store');
select add_category_tag('Shopping Services','Arts & Crafts');
select add_category_tag('Shopping Services','Costumes & Party Supplies');
select add_category_tag('Shopping Services','Home Services');
select add_category_tag('Shopping Services','Specialty Food');
select add_category_tag('Shopping Services','Auto Parts');
select add_category_tag('Shopping Services','Auto Service');
select add_category_tag('Shopping Services','Carpet Cleaning');
select add_category_tag('Shopping Services','Child Care');
select add_category_tag('Shopping Services','Limo Service');
select add_category_tag('Shopping Services','Oil Change');
select add_category_tag('Shopping Services','Self Storage');

select add_category_tag('Fun','Skiing / Boarding');
select add_category_tag('Fun','Gym');
select add_category_tag('Fun','Martial Arts Dojo');
select add_category_tag('Fun','Climbing');
select add_category_tag('Fun','Athletic Club / Health Club');
select add_category_tag('Fun','Pool');
select add_category_tag('Fun','Swimming');
select add_category_tag('Fun','Bounce House');
select add_category_tag('Fun','Community / Rec Center');
select add_category_tag('Fun','Yoga');
select add_category_tag('Fun','Skating');
select add_category_tag('Fun','Theater');
select add_category_tag('Fun','Movies');
select add_category_tag('Fun','Bowling');
select add_category_tag('Fun','Museum');
select add_category_tag('Fun','Miniature Golf');
select add_category_tag('Fun','Sporting Events');
select add_category_tag('Fun','Water Sports');
select add_category_tag('Fun','Zoo');
select add_category_tag('Fun','Aquarium');
select add_category_tag('Fun','Tours');
select add_category_tag('Fun','Lessons');
			
select add_category_tag('Nightlife','Brewery');
select add_category_tag('Nightlife','Wine');
select add_category_tag('Nightlife','Karaoke');
select add_category_tag('Nightlife','Lounge');
			
