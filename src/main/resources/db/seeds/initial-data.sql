----------------------------------------------------------------------
--- Scenario 1
INSERT INTO provider (id, provider_npi, tin) VALUES (1, 'PB654', '123456789')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO provider (id, provider_npi, tin) VALUES (2, 'PB655', '123485858')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO patient (id, first_name, last_name, date_of_birth) VALUES (1, 'QWERT', 'ZXCVB', '2000-11-05')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO patient (id, first_name, last_name, date_of_birth) VALUES (2, 'Jane', 'Doe', '2000-11-05')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO payer (id, payer_name, payer_identity) VALUES (1, 'ABCDE', '12345')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO payer (id, payer_name, payer_identity) VALUES (2, 'PayerAB', '52415')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO subscriber_patient (id, patient_id, payer_id, subscriber_patient_id)
VALUES (1, 1, 1, 'M12345678901')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO subscriber_patient (id, patient_id, payer_id, subscriber_patient_id)
VALUES (2, 2, 2, 'M12345513215')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO claim_query (id, received_dt, provider_tin, provider_claimID, provider_npi, payer_claimID,
                         subscriber_patient_id, dos_dt, claim_charge_amt, patient_id, provider_id, payer_id)
VALUES (1, '2023-09-02', '123456789', '12345V12345', 'PB654', '4567891236', 'M12345678901', '2023-08-11', 20.00, 1, 1,
        1)
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO claim_query (id, received_dt, provider_tin, provider_claimID, provider_npi, payer_claimID,
                         subscriber_patient_id, dos_dt, claim_charge_amt, patient_id, provider_id, payer_id)
VALUES (2, '2023-10-05', '123485858', '12345V54321', 'PB655', 'TYU7894562', 'M12345513215', '2023-08-11', 30.00, 2, 2,
        2)
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO claim_query (id, received_dt, provider_tin, provider_claimID, provider_npi, payer_claimID,
                         subscriber_patient_id, dos_dt, claim_charge_amt, patient_id, provider_id, payer_id)
VALUES (3, '2023-10-04', '123485858', '12345V54321', 'PB655', 'TYU7894566', 'M12345513215', '2023-08-12', 30.00, 2, 2,
        2)
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO payment (id, claim_id, payment_number, amount, payment_issue_dt)
VALUES (1, 1, 'A123456', 20.00, '2023-10-02')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO payment (id, claim_id, payment_number, amount, payment_issue_dt)
VALUES (2, 2, 'A12385858', 30.00, '2023-11-02')
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO payment (id, claim_id, payment_number, amount, payment_issue_dt)
VALUES (3, 3, 'A12385859', 30.00, '2023-11-03')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------------
--- Scenario 1
INSERT INTO remittance (id, claim_id, remittance_adviceID, remittance_advice_type, remittance_advice_dt,
                        remittance_advice_file_size)
VALUES (1, 1, 'A123456BCD', '835', '2023-10-02', 1024)
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO remittance (id, claim_id, remittance_adviceID, remittance_advice_type, remittance_advice_dt,
                        remittance_advice_file_size)
VALUES (2, 2, 'A123456BCDEF', '835', '2023-11-02', 1536)
ON CONFLICT DO NOTHING;
--- Scenario 2
INSERT INTO remittance (id, claim_id, remittance_adviceID, remittance_advice_type, remittance_advice_dt,
                        remittance_advice_file_size)
VALUES (3, 3, 'A123456BCDXY', '835', '2023-11-03', 2048)
ON CONFLICT DO NOTHING;