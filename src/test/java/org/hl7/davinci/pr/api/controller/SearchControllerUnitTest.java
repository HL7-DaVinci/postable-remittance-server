package org.hl7.davinci.pr.api.controller;

import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_CLAIM_ENDPOINT;
import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_PATIENT_ENDPOINT;
import static org.hl7.davinci.pr.api.controller.SearchController.SEARCH_BY_PAYMENT_ENDPOINT;

import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.service.SearchService;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SearchControllerUnitTest extends ControllerBaseTest {

  @MockBean
  private SearchService searchService;

  @Test
  void testSearchByClaimEndpoint_withAllParams() throws Exception {
    String searchByClaimRequest = TestUtils.getSampleSearchByClaimRequestBody();
    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByClaimRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByClaim(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByClaimEndpoint_withRequiredParams() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByClaimRequestBodyRequiredOnly();

    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByClaimRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByClaim(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByClaimEndpoint_noResults() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByClaimRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByClaim(ArgumentMatchers.any())).thenReturn(null);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByClaimEndpoint_emptyResult() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByClaimRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByClaim(ArgumentMatchers.any())).thenReturn(new Parameters());

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_CLAIM_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
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
    String searchByClaimRequest = TestUtils.getSampleSearchByPatientRequestBody();
    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByClaimRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByPatient(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPatientEndpoint_withRequiredParams() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByPatientRequestBodyRequiredOnly();

    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByClaimRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByPatient(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPatientEndpoint_noResults() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByPatientRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByPatient(ArgumentMatchers.any())).thenReturn(null);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByPatientEndpoint_emptyResult() throws Exception {
    String searchByClaimRequest = TestUtils.getSearchByPatientRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByPatient(ArgumentMatchers.any())).thenReturn(new Parameters());

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PATIENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByClaimRequest))
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
    String searchByPaymentRequest = TestUtils.getSampleSearchByPaymentRequestBody();
    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByPaymentRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByPayment(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(searchByPaymentRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPaymentEndpoint_withRequiredParams() throws Exception {
    String searchByPaymentRequest = TestUtils.getSearchByPaymentRequestBodyRequiredOnly();
    Parameters requestParameters = (Parameters) FhirUtils.parseResource(searchByPaymentRequest);
    String expectedResponse = FhirUtils.convertToJSON(requestParameters);

    Mockito.when(this.searchService.searchByPayment(ArgumentMatchers.any())).thenReturn(requestParameters);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByPaymentRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isOk(),  // Expect HTTP 200 (OK)
            MockMvcResultMatchers.content().string(expectedResponse)
        );
  }

  @Test
  void testSearchByPaymentEndpoint_noResults() throws Exception {
    String searchByPaymentRequest = TestUtils.getSearchByPaymentRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByPayment(ArgumentMatchers.any())).thenReturn(null);

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByPaymentRequest))
        .andDo(MockMvcResultHandlers.print())
        .andExpectAll(
            MockMvcResultMatchers.status().isNotFound()  // Expect HTTP 404 (NOT_FOUND)
        );
  }

  @Test
  void testSearchByPaymentEndpoint_emptyResult() throws Exception {
    String searchByPaymentRequest = TestUtils.getSearchByPaymentRequestBodyRequiredOnly();

    Mockito.when(this.searchService.searchByPayment(ArgumentMatchers.any())).thenReturn(new Parameters());

    this.mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_BY_PAYMENT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(searchByPaymentRequest))
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