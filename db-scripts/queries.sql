

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

