package org.hl7.davinci.pr.api.controller;

import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_CLAIM_ENDPOINT;
import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_PATIENT_ENDPOINT;
import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_PAYMENT_ENDPOINT;

import java.text.ParseException;
import java.util.List;
import org.hl7.davinci.pr.api.utils.ApiUtils;
import org.hl7.davinci.pr.api.utils.FhirUtils;
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
import org.hl7.davinci.pr.service.SearchService;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SearchControllerIntegrationTest extends ControllerBaseTest {

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
  void testSearchByClaimEndpoint_withAllParams() throws Exception {
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

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByClaimEndpoint_withRequiredParams() throws Exception {
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(TestDataUtils.PROVIDER_TIN_1,
        null, null, null, TestDataUtils.PROVIDER_CLAIMID_1,
        null, null, null, null, null);

    ClaimQuery claimQuery = TestUtils.getSampleClaimQuery(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.PROVIDER_CLAIMID_1);
    Patient patient = TestUtils.getSamplePatient(TestDataUtils.PATIENT_ID_1);
    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByClaimEndpoint_noResults() throws Exception {
    String tin = "101010101";
    Parameters requestResource = ApiUtils.generateSearchByClaimRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PATIENT_ID_1,
        TestDataUtils.PROVIDER_CLAIMID_1, TestDataUtils.PROVIDER_NPI_1, TestDataUtils.PAYER_CLAIM_ID_1,
        String.valueOf(TestDataUtils.CLAIM_CHARGE_AMOUNT), TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByClaimEndpoint_badRequest() throws Exception {
    String searchByClaimRequest = """
        {
          "resourceType": "Parameters",
          "parameter": [
            {
              "name": "SomethingElse",
              "valueString": "123456789"
            },
            {
              "name": "SomethingElse",
              "part": [
                {
                  "name": "SomethingElse",
                  "valueString": "212"
                }
              ]
            }
          ]
        }""";

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest()  // Expect HTTP 400 (BAD_REQUEST)
        );
  }

  @Test
  void testSearchByPatientEndpoint_withAllParams() throws Exception {
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

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }


  @Test
  void testSearchByPatientEndpoint_withRequiredParams() throws Exception {
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(TestDataUtils.PROVIDER_TIN_1,
        null, null, null, null,
        TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB, null, null);

    ClaimQuery claimQuery = TestUtils.getSampleClaimQuery(TestDataUtils.PROVIDER_TIN_1,
        TestDataUtils.PROVIDER_CLAIMID_1);
    Patient patient = TestUtils.getSamplePatient(TestDataUtils.PATIENT_ID_1);
    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByClaimOrPatientResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(claimQuery), List.of(patient), List.of(payer), List.of(payment), List.of(remittance));

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPatientEndpoint_noResults() throws Exception {
    String tin = "101010101";
    Parameters requestResource = ApiUtils.generateSearchByPatientRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_ID_1, TestDataUtils.PATIENT_DOB,
        TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByPatientEndpoint_badRequest() throws Exception {
    String searchByClaimRequest = """
        {
          "resourceType": "Parameters",
          "parameter": [
            {
              "name": "SomethingElse",
              "valueString": "123456789"
            },
            {
              "name": "SomethingElse",
              "part": [
                {
                  "name": "SomethingElse",
                  "valueString": "212"
                }
              ]
            }
          ]
        }""";

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest()  // Expect HTTP 400 (BAD_REQUEST)
        );
  }


  @Test
  void testSearchByPaymentEndpoint_withAllParams() throws Exception {
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

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }


  @Test
  void testSearchByPaymentEndpoint_withRequiredParams() throws Exception {
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(TestDataUtils.PROVIDER_TIN_1,
        null, null, null, null,
        TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END, null, null,
        TestDataUtils.PAYM_NUM_1);

    Payer payer = TestUtils.getSamplePayer(TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1);
    Payment payment = TestUtils.getSamplePayment(TestDataUtils.PAYM_NUM_1);
    Remittance remittance = TestUtils.getSampleRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

    Parameters expectedResult = ApiUtils.generateSearchByPaymentResponse(TestDataUtils.PROVIDER_TIN_1,
        List.of(payer), List.of(payment), List.of(remittance));

    String expectedResponse = FhirUtils.convertToJSON(expectedResult);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPaymentEndpoint_noResults() throws Exception {
    String tin = "101010101";
    Parameters requestResource = ApiUtils.generateSearchByPaymentRequestResource(tin,
        TestDataUtils.DATE_OF_SERVICE, TestDataUtils.DATE_OF_SERVICE_END, TestDataUtils.PAYER_ID_VAL_1,
        TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYMENT_ISSUE_DATE, TestDataUtils.PAYMENT_ISSUE_DATE_END,
        Float.toString(TestDataUtils.PAYMENT_AMOUNT), Float.toString(TestDataUtils.PAYMENT_AMOUNT_HIGH),
        TestDataUtils.PAYM_NUM_1);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(FhirUtils.convertToJSON(requestResource)))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByPaymentEndpoint_badRequest() throws Exception {
    String searchByPaymentRequest = """
        {
          "resourceType": "Parameters",
          "parameter": [
            {
              "name": "SomethingElse",
              "valueString": "123456789"
            },
            {
              "name": "SomethingElse",
              "part": [
                {
                  "name": "SomethingElse",
                  "valueString": "212"
                }
              ]
            }
          ]
        }""";

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByPaymentRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest()  // Expect HTTP 400 (BAD_REQUEST)
        );
  }

}