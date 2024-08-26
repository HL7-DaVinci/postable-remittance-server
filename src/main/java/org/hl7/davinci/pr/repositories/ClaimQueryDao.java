package org.hl7.davinci.pr.repositories;

import jakarta.persistence.Tuple;

import java.util.Date;
import java.util.List;

public interface ClaimQueryDao {

    List<Tuple> findByClaim(String tin, String providerClaimId, Date dosStart, Date dosEnd, String patientId, String payerIdentity, String payerName, String claimProviderId, String payerClaimId, Float claimChargeAmount);
    List<Tuple> findByPatient(String tin, Date patientDob, Date dosStart, Date dosEnd, String patientId, String payerIdentity, String payerName, String patientFirstName, String patientLastName);
    List<Tuple> findByPayment(String tin, String paymentNumber, Date paymentIssueDateStart, Date paymentIssueDateEnd, Date dosStart, Date dosEnd, String payerIdentity, String payerName, Float paymentAmountLow, Float paymentAmountHigh);
    List<Tuple> findByRemittance(String remittanceAdviceId);
}
