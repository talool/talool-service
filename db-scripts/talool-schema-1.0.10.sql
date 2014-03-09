
create extension HSTORE;

ALTER TABLE deal_offer add column properties HSTORE;

CREATE INDEX deal_offer_properties_idx ON deal_offer USING BTREE (properties);

CREATE INDEX deal_offer_properties_gist_idx ON deal_offer USING GIST (properties);

ALTER TABLE merchant add column properties HSTORE;

CREATE INDEX merchant_properties_gist_idx ON merchant USING GIST (properties);

CREATE INDEX merchant_properties_idx ON merchant USING BTREE (properties);

ALTER TABLE merchant_account add column properties HSTORE;

CREATE INDEX merchant_account_properties_gist_idx ON merchant_account USING GIST (properties);

CREATE INDEX merchant_account_properties_idx ON merchant_account USING BTREE (properties);

ALTER TABLE merchant_location add column properties HSTORE;

CREATE INDEX merchant_location_properties_gist_idx ON merchant_location USING GIST (properties);

CREATE INDEX merchant_location_properties_idx ON merchant_location USING BTREE (properties);

-- scheduling a deal changes
ALTER TABLE deal_offer add column scheduled_start_dt timestamp without time zone;

ALTER TABLE deal_offer add column scheduled_end_dt timestamp without time zone;

UPDATE deal_offer set scheduled_end_dt=expires;

UPDATE deal_offer set scheduled_start_dt=create_dt;

UPDATE deal_offer set scheduled_end_dt = '2016-12-31 00:00:00' where scheduled_end_dt is null;



-- http://www.youlikeprogramming.com/2011/11/working-with-the-hstore-data-type-in-postgresql-9-0/

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