
\connect talooltest

BEGIN;

alter table merchant_location add column created_by_merchant_account_id bigint;

alter table merchant_location add column created_by_merchant_id UUID;

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='chris@talool.com');

update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='chris@talool.com');

alter table merchant_location alter column created_by_merchant_account_id set not null;

alter table merchant_location alter column created_by_merchant_id set not null;

update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='chris@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='!No Que No!');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='chris@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name='!No Que No!');
 
update merchant_location set created_by_merchant_account_id=(select merchant_account_id from merchant_account where email='doug@entertainment.com')
 where merchant_id=(select merchant_id from merchant where merchant_name='A Hair Better');
 
update merchant_location set created_by_merchant_id=(select merchant_id from merchant_account where email='doug@entertainment.com')
   where merchant_id=(select merchant_id from merchant where merchant_name='A Hair Better');

COMMIT;

--select m.merchant_id from merchant_location where created_by_merchant_id=:myMerchantId

