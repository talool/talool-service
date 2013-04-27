

\connect talool


DROP INDEX merchant_location_longitude_idx;
DROP INDEX merchant_location_latitude_idx;

ALTER TABLE merchant_location DROP COLUMN latitude;
ALTER TABLE merchant_location DROP COLUMN longitude;

ALTER TABLE merchant_location ADD COLUMN geom geometry(POINT,4326);

CREATE INDEX merchant_location_geom_idx ON merchant_location USING GIST (geom);