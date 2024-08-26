package org.hl7.davinci.pr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.List;
import org.hl7.davinci.pr.BaseTest;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.ApiUtils;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.domain.Remittance;
import org.hl7.davinci.pr.repositories.ClaimQueryRepository;
import org.hl7.davinci.pr.repositories.PatientRepository;
import org.hl7.davinci.pr.repositories.PayerRepository;
import org.hl7.davinci.pr.repositories.PaymentRepository;
import org.hl7.davinci.pr.repositories.ProviderRepository;
import org.hl7.davinci.pr.repositories.RemittanceRepository;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SearchServiceIntegrationTest extends BaseTest {

  @Autowired
  private SearchService searchService;

  @Autowired
  ClaimQueryRepository claimQueryRepo;

  @Autowired
  PatientRepository patientRepo;

  @Autowired
  PayerRepository payerRepo;

  @Autowired
  PaymentRepository paymentRepo;

  @Autowired
  ProviderRepository providerRepo;

  @Autowired
  RemittanceRepository remittanceRepo;

  @BeforeEach
  void setupData() {
    TestDataUtils testDataUtils = TestDataUtils.builder().claimQueryRepo(claimQueryRepo).patientRepo(patientRepo)
        .paymentRepo(paymentRepo).remittanceRepo(remittanceRepo).providerRepo(providerRepo).payerRepo(payerRepo)
        .build();
    try {
      testDataUtils.allDataPopulated(true, true, true, true, true, true, true);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void searchByClaim_successful() {
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PATIENT_ID_1,
        TestDataUtils.PROVIDER_CLAIMID_1, TestDataUtils.PROVIDER_NPI_1, TestDataUtils.PAYER_CLAIM_ID_1,
        String.valueOf(TestDataUtils.CLAIM_CHARGE_AMOUNT), TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);

    ClaimQuery claimQuery = TestUtils.getSampleClaimQuery(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.PROVIDER_CLAIMID_1);
    Patient patient = TestUtils.getSamplePatient(TestDataUtils.PATIENT_ID_1);
    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByClaim(requestResource);

    // Assert
    assertEquals(TestDataUtils.PROVIDER_TIN_1,
        actualResult.getParameter(ApiConstants.TIN).getValue().toString());
    assertTrue(expectedResult.getParameter(ApiConstants.PAYER)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYER)));
    assertTrue(expectedResult.getParameter(ApiConstants.PATIENT)
        .equalsDeep(actualResult.getParameter(ApiConstants.PATIENT)));
    assertTrue(expectedResult.getParameter(ApiConstants.CLAIM)
        .equalsDeep(actualResult.getParameter(ApiConstants.CLAIM)));
  }

  @Test
  void searchByClaim_emptyResult() {
    String tin = "111111111";
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PATIENT_ID_1,
        TestDataUtils.PROVIDER_CLAIMID_1, TestDataUtils.PROVIDER_NPI_1, TestDataUtils.PAYER_CLAIM_ID_1,
        String.valueOf(TestDataUtils.CLAIM_CHARGE_AMOUNT), TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);

    // Act
    Parameters actualResult = searchService.searchByClaim(requestResource);
    // Assert
    assertNull(actualResult);
  }


  @Test
  void searchByPatient_successful() {
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB,
        TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

    ClaimQuery claimQuery = TestUtils.getSampleClaimQuery(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.PROVIDER_CLAIMID_1);
    Patient patient = TestUtils.getSamplePatient(TestDataUtils.PATIENT_ID_1);
    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByPatient(requestResource);

    // Assert
    assertEquals(TestDataUtils.PROVIDER_TIN_1,
        actualResult.getParameter(ApiConstants.TIN).getValue().toString());
    assertTrue(expectedResult.getParameter(ApiConstants.PAYER)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYER)));
    assertTrue(expectedResult.getParameter(ApiConstants.PATIENT)
        .equalsDeep(actualResult.getParameter(ApiConstants.PATIENT)));
    assertTrue(expectedResult.getParameter(ApiConstants.CLAIM)
        .equalsDeep(actualResult.getParameter(ApiConstants.CLAIM)));
  }

  @Test
  void searchByPatient_emptyResult() {
    String tin = "111111111";
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB,
        TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

    // Act
    Parameters actualResult = searchService.searchByPatient(requestResource);
    // Assert
    assertNull(actualResult);
  }

  @Test
  void searchByPayment_successful() {
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByPaymentResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByPayment(requestResource);

    // Assert
    assertEquals(TestDataUtils.PROVIDER_TIN_1,
        actualResult.getParameter(ApiConstants.TIN).getValue().toString());
    assertTrue(expectedResult.getParameter(ApiConstants.PAYER)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYER)));
    assertTrue(expectedResult.getParameter(ApiConstants.PAYMENT)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYMENT)));
    assertTrue(expectedResult.getParameter(ApiConstants.REMITTANCE)
        .equalsDeep(actualResult.getParameter(ApiConstants.REMITTANCE)));
  }

  @Test
  void searchByPayment_emptyResult() {
    String tin = "111111111";
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    // Act
    Parameters actualResult = searchService.searchByPayment(requestResource);
    // Assert
    assertNull(actualResult);
  }
}