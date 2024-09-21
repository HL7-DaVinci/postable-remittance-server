package org.hl7.davinci.pr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;

import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.ApiUtils;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.domain.Remittance;
import org.hl7.davinci.pr.repositories.ClaimQueryDao;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class) // Junit 5 for @Mock annotation
class SearchServiceUnitTest {

  private SearchService searchService;

  @Mock
  private ClaimQueryDao claimQueryDao;

  @BeforeEach
  void setup() {
    searchService = new SearchService(claimQueryDao);
  }

  @AfterEach
  void tearDownEach() {
    Mockito.reset(this.claimQueryDao);
  }

  @AfterAll
  static void tearDown() {
    Mockito.reset();
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

    List<Tuple> daoResult = List.of(
        TestUtils.generateSampleFindByClaimOrPatientDaoTuple(claimQuery, patient, payer, payment, remittance));

    when(claimQueryDao.findByClaim(any(), any(), any(), any(), any(), any(), any(), any(), any(),
        any())).thenReturn(daoResult);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByClaim(requestResource);

    // Assert
    assertEquals(expectedResult.getParameter(ApiConstants.TIN).getValue().toString(),
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
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PATIENT_ID_1,
        TestDataUtils.PROVIDER_CLAIMID_1, TestDataUtils.PROVIDER_NPI_1, TestDataUtils.PAYER_CLAIM_ID_1,
        String.valueOf(TestDataUtils.CLAIM_CHARGE_AMOUNT), TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);

    when(claimQueryDao.findByClaim(any(), any(), any(), any(), any(), any(), any(), any(), any(),
        any())).thenReturn(List.of());

    // Act
    Parameters actualResult = searchService.searchByClaim(requestResource);
    // Assert
    assertNull(actualResult);
  }

  @Test
  void searchByClaim_handleException() {
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PATIENT_ID_1,
        TestDataUtils.PROVIDER_CLAIMID_1, TestDataUtils.PROVIDER_NPI_1, TestDataUtils.PAYER_CLAIM_ID_1,
        String.valueOf(TestDataUtils.CLAIM_CHARGE_AMOUNT), TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);

    when(claimQueryDao.findByClaim(any(), any(), any(), any(), any(), any(), any(), any(), any(),
        any())).thenThrow(
        new DataAccessResourceFailureException("Test searchByClaim DataAccessResourceFailureException"));

    // Act & Assert
    assertThrows(DataAccessResourceFailureException.class, () -> searchService.searchByClaim(requestResource));
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

    List<Tuple> daoResult = List.of(
        TestUtils.generateSampleFindByClaimOrPatientDaoTuple(claimQuery, patient, payer, payment, remittance));

    when(claimQueryDao.findByPatient(any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(daoResult);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByPatient(requestResource);

    // Assert
    assertEquals(expectedResult.getParameter(ApiConstants.TIN).getValue().toString(),
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
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB,
        TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

    when(claimQueryDao.findByPatient(any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(List.of());

    // Act
    Parameters actualResult = searchService.searchByPatient(requestResource);
    // Assert
    assertNull(actualResult);
  }

  @Test
  void searchByPatient_handleException() {
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB,
        TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

    when(claimQueryDao.findByPatient(any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(new DataAccessResourceFailureException("Test searchByPatient DataAccessResourceFailureException"));

    // Act & Assert
    assertThrows(DataAccessResourceFailureException.class, () -> searchService.searchByPatient(requestResource));
  }

  @Test
  void searchByPayment_successful() {
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    ClaimQuery claimQuery = TestUtils.getSampleClaimQuery(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.PROVIDER_CLAIMID_1);
    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    List<Tuple> daoResult = List.of(
        TestUtils.generateSampleFindByPaymentDaoTuple(claimQuery, payer, payment, remittance));

    when(claimQueryDao.findByPayment(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(daoResult);

    Parameters expectedResult = ApiUtils.generateSearchByPaymentResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(payer), List.of(payment), List.of(remittance));

    // Act
    Parameters actualResult = searchService.searchByPayment(requestResource);

    // Assert
    assertEquals(expectedResult.getParameter(ApiConstants.TIN).getValue().toString(),
        actualResult.getParameter(ApiConstants.TIN).getValue().toString());
    assertTrue(expectedResult.getParameter(ApiConstants.PAYER)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYER)));
    assertTrue(expectedResult.getParameter(ApiConstants.PAYMENT_INFO)
        .equalsDeep(actualResult.getParameter(ApiConstants.PAYMENT_INFO)));
    List<Parameters.ParametersParameterComponent> paymParts = actualResult.getParameter(ApiConstants.PAYMENT_INFO).getPart();
    Optional<Parameters.ParametersParameterComponent> remittanceComponent = paymParts.stream().filter(e -> e.getName().equals(ApiConstants.REMITTANCE)).findFirst();
    List<Parameters.ParametersParameterComponent> actualRemits = remittanceComponent.get().getPart();
    Optional<Parameters.ParametersParameterComponent> actualRemitId = actualRemits.stream().filter(e->e.getName().equals("RemittanceAdviceIdentifier")).findFirst();
    assertEquals(payment.getRemittance().getRemittanceAdviceId(), actualRemitId.get().getValue().toString());
  }

  @Test
  void searchByPayment_emptyResult() {
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    when(claimQueryDao.findByPayment(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(List.of());

    // Act
    Parameters actualResult = searchService.searchByPayment(requestResource);
    // Assert
    assertNull(actualResult);
  }

  @Test
  void searchByPayment_handleException() {
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    when(claimQueryDao.findByPayment(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(new DataAccessResourceFailureException("Test searchByPayment DataAccessResourceFailureException"));

    // Act & Assert
    assertThrows(DataAccessResourceFailureException.class, () -> searchService.searchByPayment(requestResource));
  }

}