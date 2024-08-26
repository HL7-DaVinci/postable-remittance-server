drop index if exists claim_dates_idx;
ALTER TABLE claim_query RENAME COLUMN dos_start_dt TO dos_dt;
alter table claim_query drop column dos_end_dt;
CREATE INDEX claim_dos_idx ON claim_query (dos_dt);

--claim_charge_amt to claim_query
ALTER TABLE IF EXISTS claim_query
    ADD COLUMN claim_charge_amt integer;
CREATE INDEX claim_chrg_amt_idx ON claim_query (claim_charge_amt);

drop table if exists payer_identity;
ALTER TABLE IF EXISTS payer
    ADD COLUMN payer_identity varchar(255);
CREATE INDEX payer_ident_idx ON payer (payer_identity);

