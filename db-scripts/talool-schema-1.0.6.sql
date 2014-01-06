
\connect talool

-- Add this for already created columns
ALTER TABLE deal_offer RENAME COLUMN deal_offer_merchant_logo_id TO deal_offer_icon_id;
ALTER TABLE deal_offer RENAME COLUMN deal_offer_background_image_id TO deal_offer_background_id;
ALTER TABLE deal_offer RENAME COLUMN image_id TO deal_offer_logo_id;

-- else do this fresg

BEGIN;

ALTER TABLE deal_offer ADD COLUMN geom geometry(POINT,4326);
ALTER TABLE deal_offer ADD COLUMN deal_offer_merchant_logo_id UUID;
ALTER TABLE deal_offer ADD COLUMN deal_offer_background_image_id UUID;

ALTER TABLE deal_offer DROP CONSTRAINT "FK_DealOffer_DealOffer_MerchantLogo";

ALTER TABLE deal_offer ADD CONSTRAINT "FK_DealOffer_DealOffer_MerchantLogo" 
      FOREIGN KEY (deal_offer_merchant_logo_id) REFERENCES merchant_media(merchant_media_id);
      
ALTER TABLE deal_offer DROP CONSTRAINT "FK_DealOffer_DealOffer_BackgroundImage";

ALTER TABLE deal_offer ADD CONSTRAINT "FK_DealOffer_DealOffer_BackgroundImage" 
      FOREIGN KEY (deal_offer_background_image_id) REFERENCES merchant_media(merchant_media_id);

CREATE INDEX deal_offer_geom_idx ON deal_offer USING GIST (geom);

update deal_offer set geom=ST_GeomFromText('POINT(-105.2797 40.0176)', 4326) 
  where title='The Kitchen Test Book #1' or title='Welcome to Boulder' or title='Boulder Payback Book'
    or title='Test - Boulder Payback Book';

update deal_offer set geom=ST_GeomFromText('POINT(-122.6028 45.6336)', 4326) 
  where title='Test - Vancouver Payback Book' or title='Vancouver Payback Book';
  
update deal_offer set geom=ST_GeomFromText('POINT(-75.98 42.23)', 4326) 
  where title='Broome County' or title='Re/Max Welcome Home';
  
COMMIT;

ALTER TYPE media_type ADD VALUE 'DEAL_OFFER_BACKGROUND_IMAGE' AFTER 'DEAL_OFFER_LOGO';
ALTER TYPE media_type ADD VALUE 'DEAL_OFFER_MERCHANT_LOGO' AFTER 'DEAL_OFFER_BACKGROUND_IMAGE';

-----
ALTER TABLE deal_offer DROP CONSTRAINT "FK_DealOffer_DealOffer_BackgroundImage" ;
ALTER TABLE deal_offer DROP CONSTRAINT "FK_DealOffer_DealOffer_MerchantLogo" ;
ALTER TABLE deal_offer DROP CONSTRAINT "FK_Deal_Image" ;

ALTER TABLE deal_offer ADD CONSTRAINT "FK_DealOffer_DealOffer_Icon" 
      FOREIGN KEY (deal_offer_icon_id) REFERENCES merchant_media(merchant_media_id);
      
ALTER TABLE deal_offer ADD CONSTRAINT "FK_DealOffer_DealOffer_Background" 
      FOREIGN KEY (deal_offer_background_id) REFERENCES merchant_media(merchant_media_id);
      
ALTER TABLE deal_offer ADD CONSTRAINT "FK_DealOffer_DealOffer_Logo" 
      FOREIGN KEY (deal_offer_logo_id) REFERENCES merchant_media(merchant_media_id);

-- fix enums?
--ALTER TYPE media_type RENAME TO _old_media_type;

--CREATE TYPE media_type AS ENUM ('DEAL_IMAGE','MERCHANT_IMAGE', 'MERCHANT_LOGO', 'DEAL_OFFER_LOGO','DEAL_OFFER_BACKGROUND','DEAL_OFFER_ICON');

--ALTER TABLE merchant_media ALTER COLUMN media_type TYPE media_type USING (media_type::media_type)

--DROP TYPE _old_media_type;

CREATE OR REPLACE FUNCTION update_dt_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	BEGIN
	   NEW.update_dt = now() at time zone 'utc';
	   RETURN NEW;
	END;
	$$;
	


