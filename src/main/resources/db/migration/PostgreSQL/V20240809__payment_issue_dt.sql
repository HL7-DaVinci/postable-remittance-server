ALTER TABLE payment RENAME COLUMN payment_dt TO payment_issue_dt;

drop index if exists pmt_ident_val_idx;
alter table payment drop column payer_identity_val;
