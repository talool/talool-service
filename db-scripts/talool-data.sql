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

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('christopher.justin@gmail.com', '32250170a0dca92d53ec9624f336ca24', 'Chris', 'Lintz', 'M');

INSERT INTO customer(email, password, first_name, last_name, sex_t)
       VALUES ('douglasmccuen@gmail.com', '32250170a0dca92d53ec9624f336ca24', 'Doug', 'Mccuen', 'M');      
       
-- insert merchant deal    
INSERT INTO address(address1, address2, city, state_province_county, zip, country)
            VALUES ('1401 WYNKOOP ST', 'STE 500', 'Denver', 'CO', '80202', 'US');
            
INSERT INTO address(address1, address2, city, state_province_county, zip, country)
            VALUES ('1750 30th St.', 'PO Box 195', 'Boulder', 'CO', '80301', 'US');

-- insert payBackbook
INSERT INTO merchant(merchant_name, email, website_url, logo_url, phone, latitude, longitude, 
                     address_id, is_active)
    VALUES ('Payback Book', 'paybackbook@aol.com','http://www.paybackbook.com', 'http://www.paybackbook.com', 
                    '303.447.1252', null, null, (select address_id from address where address1='1750 30th St.'),'true');

INSERT INTO merchant(merchant_name, email, website_url, logo_url, phone, latitude, longitude, 
                     address_id, is_active)
    VALUES ('Chipotle', 'management@chipotle.com', 'http://www.chipotle.com', 'http://talk.onevietnam.org/assets/2011/10/chipotle.png', 
                    '303.333.3333', null, null, 1,'true');
                    
INSERT INTO merchant_deal(merchant_id, title, summary, details, code, 
            image_url, expires, is_active)
    VALUES ( (select merchant_id from merchant where merchant_name='Chipotle'), 'Buy 1 Get 1 Free Burrito',
     'Get the tastiest burrito you can imagine only valid Sun-Thurs', '','code123', null, null, 'true');
     
     
INSERT INTO merchant_deal(merchant_id, title, summary, details, code, 
            image_url, expires, is_active)
    VALUES ( (select merchant_id from merchant where merchant_name='Chipotle'), 'Free Chips & Guac',
     'Free Chips and Guac by simply stopping by Sun-Thurs', '','code123', null, null, 'true');
    
                    
-- insert merchant deal    
INSERT INTO address(address1, address2, city, state_province_county, zip, country)
            VALUES ('900 Walnut Street', null, 'Boulder', 'CO', '80202', 'US');


INSERT INTO merchant(merchant_name, email,  website_url, logo_url, phone, latitude, longitude, 
                     address_id, is_active)
   		 VALUES ('St Julien Hotel & Spa', 'team@', 'http://www.stjulien.com', 'http://www.stjulien.com/images/stjulien-logo.png', 
                 '1.877.303.0900', null, null, (select address_id from address where address1='900 Walnut Street'),'true');
                    
                    
INSERT INTO merchant_deal(merchant_id, title, summary, details, code, image_url, expires, is_active)
    		VALUES ( (select merchant_id from merchant where merchant_name='St Julien Hotel & Spa'), 'Free Room Upgrade',
     		'Stay at the best hotel and spa in Colorado.  Free room upgrade any day of the week.  No black outs', '','code123', null, null, 'true');
     		
INSERT INTO merchant_deal(merchant_id, title, summary, details, code, image_url, expires, is_active)
    		VALUES ( (select merchant_id from merchant where merchant_name='St Julien Hotel & Spa'), '1/2 off Dinner & Drinks',
     		'Receive 1/2 Dinner and Drinks when staying at the best hotel and spa in Colorado.','','code123', null, null, 'true');
     		


-- create a deal book (Ted)       
INSERT INTO deal_book( merchant_id, latitude, longitude, title, summary, details, code, image_url, cost, expires, is_active)
       VALUES ( (select merchant_id from merchant where merchant_name='Payback Book'), null, null, 
                 'Payback Book', 'Fabulous merchants of all categories','Over 500 merchants', 'code123', 
                  null, 25.00, null, 'true');
                    

INSERT INTO deal_book_content( deal_book_id, merchant_deal_id  )
       VALUES ( (select deal_book_id from deal_book where title='Payback Book'),
                (select merchant_deal_id from merchant_deal where title='Free Room Upgrade'));
                

INSERT INTO deal_book_content( deal_book_id, merchant_deal_id  )
       VALUES ( (select deal_book_id from deal_book where title='Payback Book'),
                (select merchant_deal_id from merchant_deal where title='1/2 off Dinner & Drinks'));
                
                              
INSERT INTO deal_book_content( deal_book_id, merchant_deal_id  )
       VALUES ( (select deal_book_id from deal_book where title='Payback Book'),
                (select merchant_deal_id from merchant_deal where title='Free Chips & Guac'));
                
                
INSERT INTO deal_book_purchase( deal_book_id, customer_id)
    VALUES ( (select deal_book_id from deal_book where title='Payback Book'), 
             (select customer_id from customer where email='christopher.justin@gmail.com') );
                
INSERT INTO deal_book_purchase( deal_book_id, customer_id)
    VALUES ( (select deal_book_id from deal_book where title='Payback Book'), 
             (select customer_id from customer where email='douglasmccuen@gmail.com') );                
                

       
