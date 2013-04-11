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
      

INSERT INTO acquire_status(status) VALUES ('PURCHASE');
INSERT INTO acquire_status(status) VALUES ('MERCHANT_SHARE');
INSERT INTO acquire_status(status) VALUES ('CUSTOMER_SHARE');

