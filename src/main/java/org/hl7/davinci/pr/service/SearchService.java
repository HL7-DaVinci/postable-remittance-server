package org.hl7.davinci.pr.service;

import jakarta.persistence.Tuple;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.ApiUtils;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.domain.Remittance;
import org.hl7.davinci.pr.repositories.ClaimQueryDao;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final ClaimQueryDao claimQueryDao;

  public SearchService(ClaimQueryDao claimQueryDao) {
    this.claimQueryDao = claimQueryDao;
  }

  /**
   * Searches for postable remittances by claim information and returns the claim details along with
   * associated patient, payer, payment, and remittance information.
   *
   * @param requestResource the FHIR Parameters resource containing the search criteria
   * @return the FHIR Parameters resource containing the claim and remittance details
   */
  public Parameters searchByClaim(Parameters requestResource) {

    // TIN is required and validated in the controller
    String tin = requestResource.getParameter(ApiConstants.TIN).getValue().toString();
    String patientId = null;
    String payerId = null;
    String payerName = null;
    String providerId = null;
    String payerClaimId = null;
    Float claimChargeAmount = null;
    Period period = new Period(); // Empty period by default
    if (requestResource.hasParameter(ApiConstants.PATIENT_ID)) {
      patientId = requestResource.getParameter(ApiConstants.PATIENT_ID).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.PAYER_ID)) {
      payerId = requestResource.getParameter(ApiConstants.PAYER_ID).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.PAYER_NAME)) {
      payerName = requestResource.getParameter(ApiConstants.PAYER_NAME).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.DATE_OF_SERVICE)) {
      period = (Period) requestResource.getParameter(ApiConstants.DATE_OF_SERVICE).getValue();
    }

    // Claim is required and validated in the controller
    List<ParametersParameterComponent> claimParameter = requestResource.getParameter(ApiConstants.CLAIM).getPart();
    Map<String, String> claimObject = new HashMap<>();
    for (ParametersParameterComponent claim : claimParameter) {
      claimObject.put(claim.getName(), claim.getValue().toString());
    }
    // Provider claim ID is required and validated in the controller
    String providerClaimId = claimObject.get(ApiConstants.PROVIDER_CLAIM_ID);

    if (claimObject.containsKey(ApiConstants.PROVIDER_ID)) {
      providerId = claimObject.get(ApiConstants.PROVIDER_ID);
    }
    if (claimObject.containsKey(ApiConstants.PAYER_CLAIM_ID)) {
      payerClaimId = claimObject.get(ApiConstants.PAYER_CLAIM_ID);
    }
    if (claimObject.containsKey(ApiConstants.CLAIM_CHARGE_AMOUNT)) {
      claimChargeAmount = Float.parseFloat(claimObject.get(ApiConstants.CLAIM_CHARGE_AMOUNT));
    }

    try {
      List<Tuple> result = this.claimQueryDao.findByClaim(tin, providerClaimId, period.getStart(), period.getEnd(),
          patientId, payerId, payerName, payerClaimId, providerId, claimChargeAmount);

      if (!result.isEmpty()) {
        Map<String, List<Object>> resultObjectMap = parseSearchByClaimOrPatientResults(result);

        // Cast into respective classes
        List<ClaimQuery> claimQueryList = resultObjectMap.get(ClaimQuery.class.getSimpleName()).stream()
            .map(ClaimQuery.class::cast).toList();
        List<Patient> patientList = resultObjectMap.get(Patient.class.getSimpleName()).stream()
            .map(Patient.class::cast).toList();
        List<Payer> payerList = resultObjectMap.get(Payer.class.getSimpleName()).stream()
            .map(Payer.class::cast).toList();
        List<Payment> paymentList = resultObjectMap.get(Payment.class.getSimpleName()).stream()
            .map(Payment.class::cast).toList();
        List<Remittance> remittanceList = resultObjectMap.get(Remittance.class.getSimpleName()).stream()
            .map(Remittance.class::cast).toList();

        return ApiUtils.generateSearchByClaimOrPatientResponse(tin, claimQueryList, patientList, payerList, paymentList,
            remittanceList);
      }
    } catch (Exception e) {
      throw new DataAccessResourceFailureException(e.getMessage());
    }
    return null;
  }

  /**
   * Searches for postable remittances by patient information and returns the claim details along with
   * associated patient, payer, payment, and remittance information.
   *
   * @param requestResource the FHIR Parameters resource containing the search criteria
   * @return the FHIR Parameters resource containing the claim and remittance details
   */
  public Parameters searchByPatient(Parameters requestResource) {
    // TIN is required and validated in the controller
    String tin = requestResource.getParameter(ApiConstants.TIN).getValue().toString();
    String payerId = null;
    String payerName = null;
    Period period = new Period(); // Empty period by default
    String patientFirstName = null;
    String patientLastName = null;

    if (requestResource.hasParameter(ApiConstants.PAYER_ID)) {
      payerId = requestResource.getParameter(ApiConstants.PAYER_ID).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.PAYER_NAME)) {
      payerName = requestResource.getParameter(ApiConstants.PAYER_NAME).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.DATE_OF_SERVICE)) {
      period = (Period) requestResource.getParameter(ApiConstants.DATE_OF_SERVICE).getValue();
    }

    // Patient is required and validated in the controller
    List<ParametersParameterComponent> patientComponent = requestResource.getParameter(ApiConstants.PATIENT).getPart();
    Map<String, Type> patientObject = new HashMap<>();
    for (ParametersParameterComponent patient : patientComponent) {
      patientObject.put(patient.getName(), patient.getValue());
    }
    // Patient ID and Date of Birth is required and validated in the controller
    String patientId = new StringType().castToString(patientObject.get(ApiConstants.PATIENT_ID)).asStringValue();
    DateType dateOfBirth = (DateType) patientObject.get(ApiConstants.DATE_OF_BIRTH);

    if (patientObject.containsKey(ApiConstants.PATIENT_FIRST_NAME)) {
      patientFirstName = new StringType().castToString(patientObject.get(ApiConstants.PATIENT_FIRST_NAME))
          .asStringValue();
    }
    if (patientObject.containsKey(ApiConstants.PATIENT_LAST_NAME)) {
      patientLastName = new StringType().castToString(patientObject.get(ApiConstants.PATIENT_LAST_NAME))
          .asStringValue();
    }

    try {
      List<Tuple> result = this.claimQueryDao.findByPatient(tin, dateOfBirth.getValue(), period.getStart(),
          period.getEnd(), patientId, payerId, payerName, patientFirstName, patientLastName);

      if (!result.isEmpty()) {
        Map<String, List<Object>> resultObjectMap = parseSearchByClaimOrPatientResults(result);

        // Cast into respective classes
        List<ClaimQuery> claimQueryList = resultObjectMap.get(ClaimQuery.class.getSimpleName()).stream()
            .map(ClaimQuery.class::cast).toList();
        List<Patient> patientList = resultObjectMap.get(Patient.class.getSimpleName()).stream()
            .map(Patient.class::cast).toList();
        List<Payer> payerList = resultObjectMap.get(Payer.class.getSimpleName()).stream()
            .map(Payer.class::cast).toList();
        List<Payment> paymentList = resultObjectMap.get(Payment.class.getSimpleName()).stream()
            .map(Payment.class::cast).toList();
        List<Remittance> remittanceList = resultObjectMap.get(Remittance.class.getSimpleName()).stream()
            .map(Remittance.class::cast).toList();

        return ApiUtils.generateSearchByClaimOrPatientResponse(tin, claimQueryList, patientList, payerList, paymentList,
            remittanceList);
      }
    } catch (Exception e) {
      throw new DataAccessResourceFailureException(e.getMessage());
    }
    return null;
  }

  /**
   * Searches for postable remittances by payment information and returns the claim details along with
   * associated patient, payer, payment, and remittance information.
   *
   * @param requestResource the FHIR Parameters resource containing the search criteria
   * @return the FHIR Parameters resource containing the claim and remittance details
   */
  public Parameters searchByPayment(Parameters requestResource) {
    // TIN is required and validated in the controller
    String tin = requestResource.getParameter(ApiConstants.TIN).getValue().toString();
    Period dateOfServicePeriod = new Period(); // Empty period by default
    String payerId = null;
    String payerName = null;
    Float paymentAmountLow = null;
    Float paymentAmountHigh = null;
    Period paymentIssueDatePeriod = new Period();
    String paymentNumber = null;

    if (requestResource.hasParameter(ApiConstants.PAYER_ID)) {
      payerId = requestResource.getParameter(ApiConstants.PAYER_ID).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.PAYER_NAME)) {
      payerName = requestResource.getParameter(ApiConstants.PAYER_NAME).getValue().toString();
    }
    if (requestResource.hasParameter(ApiConstants.DATE_OF_SERVICE)) {
      dateOfServicePeriod = (Period) requestResource.getParameter(ApiConstants.DATE_OF_SERVICE).getValue();
    }

    // Payment is required and validated in the controller
    List<ParametersParameterComponent> paymentComponent = requestResource.getParameter(ApiConstants.PAYMENT).getPart();

    for (ParametersParameterComponent payment : paymentComponent) {
      // PaymentAmount is required and validated in the controller
      if (payment.getName().equals(ApiConstants.PAYMENT_AMOUNT)) {
        List<ParametersParameterComponent> paymentAmount = payment.getPart();
        for (ParametersParameterComponent amount : paymentAmount) {
          if (amount.getName().equals(ApiConstants.PAYMENT_AMOUNT_LOW)) {
            paymentAmountLow = new Money().castToMoney(amount.getValue()).getValue().floatValue();
          } else if (amount.getName().equals(ApiConstants.PAYMENT_AMOUNT_HIGH)) {
            paymentAmountHigh = new Money().castToMoney(amount.getValue()).getValue().floatValue();
          }
        }
      }
      // PaymentIssueDate and PaymentNumber are required and validated in the controller
      if (payment.getName().equals(ApiConstants.PAYMENT_ISSUE_DATE)) {
        paymentIssueDatePeriod = (Period) payment.getValue();
      }
      if (payment.getName().equals(ApiConstants.PAYMENT_NUMBER)) {
        paymentNumber = payment.getValue().toString();
      }
    }

    try {
      List<Tuple> result = this.claimQueryDao.findByPayment(tin, paymentNumber, paymentIssueDatePeriod.getStart(),
          paymentIssueDatePeriod.getEnd(), dateOfServicePeriod.getStart(), dateOfServicePeriod.getEnd(),
          payerId, payerName, paymentAmountLow, paymentAmountHigh);

      if (!result.isEmpty()) {
        Map<String, List<Object>> resultObjectMap = parseSearchByPaymentResults(result);

        // Cast into respective classes
        List<Payer> payerList = resultObjectMap.get(Payer.class.getSimpleName()).stream()
            .map(Payer.class::cast).toList();
        List<Payment> paymentList = resultObjectMap.get(Payment.class.getSimpleName()).stream()
            .map(Payment.class::cast).toList();
        List<Remittance> remittanceList = resultObjectMap.get(Remittance.class.getSimpleName()).stream()
            .map(Remittance.class::cast).toList();

        return ApiUtils.generateSearchByPaymentResponse(tin, payerList, paymentList, remittanceList);
      }
    } catch (Exception e) {
      throw new DataAccessResourceFailureException(e.getMessage());
    }
    return null;
  }

  /**
   * Helper function to parse the results from DAO to generate the response for search by claim and patient
   *
   * @param result List of Tuples from DAO that contains claimQuery, patient, payer, payment, and remittance
   * @return Map of unique identifier and list of objects
   */
  private Map<String, List<Object>> parseSearchByClaimOrPatientResults(List<Tuple> result) {

    // Each entry in result will be a tuple of 5 objects in following sequence guaranteed by the claimQueryDao:
    // ClaimQuery, Patient, Payer, Payment, Remittance
    // There will be multiple entries of Tuple in the 'result' even if there's a single value difference in any of these objects

    // Hence, we are considering to filter out objects based on the possible unique identifier in them.
    // Example: if 'payerClaimId' are different in claimQuery object of two tuples, then there will be two entries of claimQuery in uniqueObjectMap
    // uniqueIdMap for 'claimQuery' -> Set of payerClaimIds
    // uniqueObjectMap map for 'claimQuery' -> List of claimQuery Objects
    Map<String, HashSet<String>> uniqueIdMap = new HashMap<>();
    uniqueIdMap.computeIfAbsent(ClaimQuery.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Patient.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Payer.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Payment.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Remittance.class.getSimpleName(), k -> new HashSet<>());

    Map<String, List<Object>> uniqueObjectMap = new HashMap<>();
    uniqueObjectMap.computeIfAbsent(ClaimQuery.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Patient.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Payer.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Payment.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Remittance.class.getSimpleName(), k -> new LinkedList<>());

    for (Tuple tuple : result) {
      // Objects Order in Tuple is important and guaranteed by the claimQueryDao
      ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
      if (!uniqueIdMap.get(ClaimQuery.class.getSimpleName()).contains(claimQuery.getPayerClaimId())) {
        uniqueIdMap.get(ClaimQuery.class.getSimpleName()).add(claimQuery.getPayerClaimId());
        uniqueObjectMap.get(ClaimQuery.class.getSimpleName()).add(claimQuery);
      }
      Patient patient = tuple.get(1, Patient.class);
      if (!uniqueIdMap.get(Patient.class.getSimpleName()).contains(patient.getId().toString())) {
        uniqueIdMap.get(Patient.class.getSimpleName()).add(patient.getId().toString());
        uniqueObjectMap.get(Patient.class.getSimpleName()).add(patient);
      }
      Payer payer = tuple.get(2, Payer.class);
      if (!uniqueIdMap.get(Payer.class.getSimpleName()).contains(payer.getPayerIdentity())) {
        uniqueIdMap.get(Payer.class.getSimpleName()).add(payer.getPayerIdentity());
        uniqueObjectMap.get(Payer.class.getSimpleName()).add(payer);
      }
      Payment payment = tuple.get(3, Payment.class);
      if (!uniqueIdMap.get(Payment.class.getSimpleName()).contains(payment.getPaymentNumber())) {
        uniqueIdMap.get(Payment.class.getSimpleName()).add(payment.getPaymentNumber());
        uniqueObjectMap.get(Payment.class.getSimpleName()).add(payment);
      }
      Remittance remittance = tuple.get(4, Remittance.class);
      if (!uniqueIdMap.get(Remittance.class.getSimpleName()).contains(remittance.getRemittanceAdviceId())) {
        uniqueIdMap.get(Remittance.class.getSimpleName()).add(remittance.getRemittanceAdviceId());
        uniqueObjectMap.get(Remittance.class.getSimpleName()).add(remittance);
      }
    }
    return uniqueObjectMap;
  }

  /**
   * Helper function to parse the results from DAO to generate the response for search by payment.
   * Different from parseSearchByClaimOrPatientResults function since there's no Patient information in result.
   *
   * @param result List of Tuples from DAO that contains claimQuery, payer, payment, and remittance
   * @return Map of unique identifier and list of objects
   */
  private Map<String, List<Object>> parseSearchByPaymentResults(List<Tuple> result) {
    Map<String, HashSet<String>> uniqueIdMap = new HashMap<>();
    uniqueIdMap.computeIfAbsent(ClaimQuery.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Patient.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Payer.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Payment.class.getSimpleName(), k -> new HashSet<>());
    uniqueIdMap.computeIfAbsent(Remittance.class.getSimpleName(), k -> new HashSet<>());

    Map<String, List<Object>> uniqueObjectMap = new HashMap<>();
    uniqueObjectMap.computeIfAbsent(ClaimQuery.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Patient.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Payer.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Payment.class.getSimpleName(), k -> new LinkedList<>());
    uniqueObjectMap.computeIfAbsent(Remittance.class.getSimpleName(), k -> new LinkedList<>());

    for (Tuple tuple : result) {
      // Objects Order in Tuple is important and guaranteed by the claimQueryDao
      ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
      if (!uniqueIdMap.get(ClaimQuery.class.getSimpleName()).contains(claimQuery.getPayerClaimId())) {
        uniqueIdMap.get(ClaimQuery.class.getSimpleName()).add(claimQuery.getPayerClaimId());
        uniqueObjectMap.get(ClaimQuery.class.getSimpleName()).add(claimQuery);
      }
      Payer payer = tuple.get(1, Payer.class);
      if (!uniqueIdMap.get(Payer.class.getSimpleName()).contains(payer.getPayerIdentity())) {
        uniqueIdMap.get(Payer.class.getSimpleName()).add(payer.getPayerIdentity());
        uniqueObjectMap.get(Payer.class.getSimpleName()).add(payer);
      }
      Payment payment = tuple.get(2, Payment.class);
      if (!uniqueIdMap.get(Payment.class.getSimpleName()).contains(payment.getPaymentNumber())) {
        uniqueIdMap.get(Payment.class.getSimpleName()).add(payment.getPaymentNumber());
        uniqueObjectMap.get(Payment.class.getSimpleName()).add(payment);
      }
      Remittance remittance = tuple.get(3, Remittance.class);
      if (!uniqueIdMap.get(Remittance.class.getSimpleName()).contains(remittance.getRemittanceAdviceId())) {
        uniqueIdMap.get(Remittance.class.getSimpleName()).add(remittance.getRemittanceAdviceId());
        uniqueObjectMap.get(Remittance.class.getSimpleName()).add(remittance);
      }
    }
    return uniqueObjectMap;
  }
}
