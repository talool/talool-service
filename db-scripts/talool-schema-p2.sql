

-- cleaning up deleted users activities
DELETE
FROM  activity a where a.customer_id IN (
SELECT distinct a.customer_id
FROM    activity a
LEFT JOIN customer c
ON      c.customer_id = a.customer_id
WHERE   c.customer_id IS NULL);


-- now we can properly introduce a constraint that will cleanup activities when customer is deleted

BEGIN;
ALTER TABLE activity ADD CONSTRAINT "FK_Activity_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE customer ADD COLUMN reset_pw_code character varying(16);
ALTER TABLE customer ADD COLUMN reset_pw_expires timestamp;
COMMIT;

