
\connect talool

BEGIN;
ALTER TABLE deal_offer ADD COLUMN geom geometry(POINT,4326);
CREATE INDEX deal_offer_geom_idx ON deal_offer USING GIST (geom);

update deal_offer set geom=ST_GeomFromText('POINT(-105.2797 40.0176)', 4326) 
  where title='The Kitchen Test Book #1' or title='Welcome to Boulder' or title='Boulder Payback Book'
    or title='Test - Boulder Payback Book';

update deal_offer set geom=ST_GeomFromText('POINT(-122.6028 45.6336)', 4326) 
  where title='Test - Vancouver Payback Book' or title='Vancouver Payback Book';
  
update deal_offer set geom=ST_GeomFromText('POINT(-75.98 42.23)', 4326) 
  where title='Broome County' or title='Re/Max Welcome Home';
  
COMMIT;


