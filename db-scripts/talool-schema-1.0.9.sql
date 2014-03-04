


alter table customer add column valid_email bool;

alter table merchant_location add column valid_email bool;

alter table merchant_location alter column name TYPE character varying(256);