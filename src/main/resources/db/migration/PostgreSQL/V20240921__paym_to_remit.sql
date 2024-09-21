--add remittance_id to payment
ALTER TABLE IF EXISTS payment
    ADD COLUMN remittance_id integer;
ALTER TABLE payment
    ADD CONSTRAINT fk_paymt_remit FOREIGN KEY (remittance_id) REFERENCES remittance (id);
