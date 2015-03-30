

BEGIN;

ALTER TABLE device_presence DROP CONSTRAINT "FK_DevicePresence_Customer";
ALTER TABLE device_presence ADD CONSTRAINT "FK_DevicePresence_Customer" 
      FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE;
      
COMMIT;
