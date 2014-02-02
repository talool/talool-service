
\connect talooltest

BEGIN;

alter table merchant_location add column created_by_merchant_account_id bigint;

alter table merchant_location add column created_by_merchant_id UUID;

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='chris@talool.com');

update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='chris@talool.com');

alter table merchant_location alter column created_by_merchant_account_id set not null;

alter table merchant_location alter column created_by_merchant_id set not null;

-- these updates go in pairs 
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='chris@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='!No Que No!');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='chris@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name='!No Que No!');
 
----- 
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='A Hair Better');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name='A Hair Better');

--
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='"Big" Mama''s');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name='"Big" Mama''s');
   
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='"DEA" Music and Art School');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '"DEA" Music and Art School');
     
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='"Jacks" Are Wild');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '"Jacks" Are Wild'); 
   
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='Just Myrna');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= 'Just Myrna'); 

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='Let''s Paint - Fun & Easy Art');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= 'Let''s Paint - Fun & Easy Art');  

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='"Time-Out!" Dance');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '"Time-Out!" Dance');    

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='#1 Nails');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '#1 Nails');    
   
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='$1 $tore');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '$1 $tore');    
   
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='$5 Crunch');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '$5 Crunch');    

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='$5 Car Wash');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name= '$5 Car Wash');      
   
update deal_offer set geom=ST_GeomFromText('POINT(-105.2797 40.0176)', 4326) 
  where title='Entertainment Book';
    
ALTER TABLE merchant DROP CONSTRAINT merchant_merchant_name_key;

ALTER TYPE deal_type ADD VALUE 'KIRKE_BOOK' AFTER 'FREE_DEAL';

delete from customer where email='jonesnaoko@gmail.com';

delete from customer where email='nathan.stowe@gmail.com';

delete from customer where email='aundrea.greenhill@gmail.com';

delete from customer where email='kiehl_c@yahoo.com';

delete from customer where email='wolfmlad@msn.com';

update customer set email=lower(email);

CREATE UNIQUE INDEX customer_email_lower_idx ON customer (lower(email));

COMMIT;

--select m.merchant_id from merchant_location where created_by_merchant_id=:myMerchantId

