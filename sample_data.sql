INSERT INTO patient (first_name, last_name, date_of_birth)
VALUES ('John', 'Doe', '1990-04-03');

INSERT INTO provider (tin, provider_npi, payer_provider_npi)
VALUES ('123456789', '1234567890', '9876543210');

INSERT INTO payer (payer_name, payer_identity)
VALUES ('payer_random1', '1010101');

INSERT INTO claim_query (dcn_icn,
                         received_dt,
                         provider_tin,
                         provider_claimID,
                         provider_npi,
                         payer_provider_npi,
                         payer_claimID,
                         subscriber_patient_id,
                         dos_dt,
                         claim_charge_amt,
                         patient_id,
                         provider_id,
                         payer_id)
VALUES ('52852',
        '2024-08-10',
        '123456789',
        '998899',
        '1234567890',
        '9876543210',
        '1',
        '1',
        '2024-08-11',
        100,
        1,
        1,
        1);

INSERT INTO payment (payment_number, amount, payment_issue_dt, claim_id)
VALUES ('1234567', 100, '2024-08-11', 1);

--- Remittance1
INSERT INTO remittance (claim_id, remittance_adviceID, remittance_advice_type, remittance_advice_dt,
                        remittance_advice_file_size)
VALUES (1, 'adviceID123', 'PDF', '2024-08-11', 150);

--- Remittance2
INSERT INTO remittance (claim_id, remittance_adviceID, remittance_advice_type, remittance_advice_dt,
                        remittance_advice_file_size)
VALUES (1, 'adviceID1234', '835', '2024-08-11', 500);

INSERT INTO subscriber_patient (patient_id, payer_id, subscriber_patient_id)
VALUES (1, 1, '1');