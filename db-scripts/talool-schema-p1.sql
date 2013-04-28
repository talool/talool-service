

\connect talool


DROP INDEX merchant_location_longitude_idx;
DROP INDEX merchant_location_latitude_idx;

ALTER TABLE merchant_location DROP COLUMN latitude;
ALTER TABLE merchant_location DROP COLUMN longitude;

ALTER TABLE merchant_location ADD COLUMN geom geometry(POINT,4326);

CREATE INDEX merchant_location_geom_idx ON merchant_location USING GIST (geom);

INSERT INTO address (address1,city,state_province_county,zip,country)
       VALUES ('920 Tuscon St','Tuscon','AZ','90011','US');
       
INSERT INTO merchant_location (email,website_url,logo_url,phone,address_id,geom)
        VALUES( 'info2@centrolatinkitchen.com','http://www.centrolatinkitchen.com','','303.442.7771',
        (select address_id from address where address1='920 Tuscon St'),
        'SRID=4326;POINT(110.9258 32.2217)');
        
INSERT INTO merchant_managed_location (merchant_id,merchant_location_id)
       VALUES ((select merchant_id from merchant where merchant_name='Centro Latin Kitchen'),
               (select merchant_location_id from merchant_location where email='info2@centrolatinkitchen.com'));
        