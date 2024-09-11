ALTER TABLE provider
DROP COLUMN payer_provider_npi;

ALTER TABLE claim_query
DROP COLUMN payer_provider_npi;
