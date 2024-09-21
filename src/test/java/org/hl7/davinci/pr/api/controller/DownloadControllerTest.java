package org.hl7.davinci.pr.api.controller;

import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.api.utils.ValidationUtils;
import org.hl7.davinci.pr.service.DownloadService;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.DocumentReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hl7.davinci.pr.api.controller.DownloadController.DOWNLOAD_REMITTANCE_ENDPOINT;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DownloadControllerTest extends ControllerBaseTest {

    @MockBean
    DownloadService downloadService;


    @Test
    void testDownloadRemittance_withAllParams() throws Exception {
        String requestBody = TestUtils.getSampleDownloadRemittanceRequestBody(TestDataUtils.REMITTANCE_ADVICEID_1);
        String response = TestUtils.getSampleDownloadRemittanceResponse("PDF");
        Binary binaryResource =  (Binary)FhirUtils.parseResource(response);

        //DocumentReference documentReference = (DocumentReference) FhirUtils.parseResource(response);
        String docRefStringResponse = FhirUtils.convertToJSON(binaryResource);
        when(downloadService.downloadDocument(TestDataUtils.REMITTANCE_ADVICEID_1, "PDF")).thenReturn(binaryResource);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
                        MockMvcResultMatchers.content().string(docRefStringResponse)
                ).andReturn();
    }

    @Test
    void testDownloadRemittance_withRequiredParams() throws Exception {
        String requestBody = TestUtils.getSampleDownloadRemittanceRequiredOnly(TestDataUtils.REMITTANCE_ADVICEID_1);
        String response = TestUtils.getSampleDownloadRemittanceResponse("PDF");

        Binary binaryResource = (Binary) FhirUtils.parseResource(response);
        String docRefStringResponse = FhirUtils.convertToJSON(binaryResource);
        when(downloadService.downloadDocument(TestDataUtils.REMITTANCE_ADVICEID_1, null)).thenReturn(binaryResource);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
                        MockMvcResultMatchers.content().string(docRefStringResponse)
                ).andReturn();
    }

    @Test
    void testDownloadRemittance_missingRequiredParams() throws Exception {
        String downloadRemittanceRequest = TestUtils.getSampleDownloadRemittanceNoRequired();

       MvcResult result = mockMvc.perform(
               MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(downloadRemittanceRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest() // Expect HTTP 400 (BAD_REQUEST)
                ).andReturn();
        String json = result.getResponse().getContentAsString();
        Assertions.assertTrue(json.contains("Remittance Advice Identifier is required"));
    }

    @Test
    void testDownloadRemittance_emptyIdParams() throws Exception {
        String downloadRemittanceRequest = TestUtils.getSampleDownloadRemittanceRequestBody("");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(downloadRemittanceRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest() // Expect HTTP 400 (BAD_REQUEST)
                ).andReturn();
        String json = result.getResponse().getContentAsString();
        Assertions.assertTrue(json.contains(ValidationUtils.REMITTANCE_ADVICE_ID_REQUIRED_MESSAGE));
    }
}
