

ALTER TABLE deal add column properties HSTORE;
CREATE INDEX deal_properties_idx ON deal USING BTREE (properties);
CREATE INDEX deal_properties_gist_idx ON deal USING GIST (properties);