

-- get all customerIds in the activity table, which no longer have a customer

DELETE
FROM  activity a where a.customer_id IN (
SELECT distinct a.customer_id
FROM    activity a
LEFT JOIN customer c
ON      c.customer_id = a.customer_id
WHERE   c.customer_id IS NULL;

SELECT count(distinct a.customer_id)
FROM    activity a
LEFT JOIN customer c
ON      c.customer_id = a.customer_id
WHERE   c.customer_id is null;


-- returning gifts that are dups in the activity table (investigate)
-- shows a subquery having
select c.email, gift.gift_id,gift.cnt
from customer as c
inner join 
( select customer_id,gift_id,count(gift_id) as cnt 
 from activity 
 group by gift_id,customer_id 
 having count(gift_id) > 1
 order by cnt desc 
 ) as gift
on c.customer_id=gift.customer_id;

-- show all labels
SELECT e.enumlabel
  FROM pg_enum e
  JOIN pg_type t ON e.enumtypid = t.oid
  WHERE t.typname = 'media_type';
  
  -- view customer payment transactions by most recent
select dof.create_dt, dof.customer_id,c.email, dof.processor_transaction_id 
from deal_offer_purchase as dof,customer as c
where c.customer_id=dof.customer_id and processor_transaction_id is not null 
order by create_dt desc;

-- show customers deal acquires by most recent, with title, merchant, status and times
select dac.deal_acquire_id,dac.update_dt,dac.redemption_dt,
dac.acquire_status,m.merchant_name, d.title 
from merchant as m, deal_acquire as dac,deal as d 
where m.merchant_id=d.merchant_id and d.deal_id=dac.deal_id 
and customer_id=(select customer_id from customer where email='christopher.justin@gmail.com')
order by dac.update_dt desc;
