

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

