


alter table customer add column valid_email bool;

alter table merchant_location add column valid_email bool;

alter table merchant_location alter column name TYPE character varying(256);


alter table deal_offer add column properties HSTORE;

CREATE INDEX deal_offer_properties_idx ON deal_offer USING BTREE (properties);

CREATE INDEX deal_offer_properties_gist_idx ON deal_offer USING GIST (properties);

alter table merchant add column properties HSTORE;

alter table merchant_account add column properties HSTORE;

CREATE INDEX deal_offer_properties_idx ON deal_offer USING BTREE (h);


-- http://www.youlikeprogramming.com/2011/11/working-with-the-hstore-data-type-in-postgresql-9-0/

https://github.com/jamesward/spring_hibernate_hstore_demo

-- Setting values
UPDATE deal_offer SET properties = '"zone"=>"7748","subscription"=>"true","rank"=>"148","sold_last_year"="78412"'::hstore;

-- adding calues to exisiting hstore
UPDATE deal_offer SET properties =  properties || '"rating"=>4'::hstore;

-- how to sum or average 
select SUM( CAST( properties->'rating' AS bigint )) as rating from deal_offer;



SELECT key, count(*) FROM
  (SELECT (each(properties)).key FROM deal_offer) AS stat
  GROUP BY key
  ORDER BY count DESC, key;