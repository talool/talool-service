

ALTER TABLE deal add column properties HSTORE;
CREATE INDEX deal_properties_idx ON deal USING BTREE (properties);
CREATE INDEX deal_properties_gist_idx ON deal USING GIST (properties);

-------------- Media Tags & Categories ------------------
CREATE TABLE merchant_media_tag (
    merchant_media_id UUID NOT NULL,
    tag_id smallint NOT NULL,
    create_dt timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY(merchant_media_id,tag_id)
);
ALTER TABLE public.merchant_media_tag OWNER TO talool;
ALTER TABLE ONLY merchant_media_tag ADD CONSTRAINT "FK_MerchantMediaTag_MerchantMedia" FOREIGN KEY (merchant_media_id) REFERENCES merchant_media(merchant_media_id);
ALTER TABLE ONLY merchant_media_tag ADD CONSTRAINT "FK_MerchantMediaTag_Tag" FOREIGN KEY (tag_id) REFERENCES tag(tag_id);
CREATE INDEX merchant_media_tag_merchant_media_id_idx ON merchant_media_tag (merchant_media_id);
CREATE INDEX merchant_media_tag_tag_id_idx ON merchant_media_tag (tag_id);

ALTER TABLE merchant_media add column category_id bigint;
ALTER TABLE ONLY merchant_media ADD CONSTRAINT "FK_Merchant_Media_Category" FOREIGN KEY (category_id) REFERENCES category(category_id);
CREATE INDEX merchant_media_category_id_idx ON merchant_media (category_id);