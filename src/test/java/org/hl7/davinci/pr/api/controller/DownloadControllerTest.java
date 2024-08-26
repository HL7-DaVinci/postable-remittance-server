package org.hl7.davinci.pr.api.controller;

import static org.hl7.davinci.pr.api.controller.DownloadController.DOWNLOAD_REMITTANCE_ENDPOINT;

import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.api.utils.ValidationUtils;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DownloadControllerTest extends ControllerBaseTest {


    @Test
    void testDownloadRemittance_withAllParams() throws Exception {
        String downloadRemittanceRequest = TestUtils.getSampleDownloadRemittanceRequestBody("999");

        Parameters requestParameters = (Parameters) FhirUtils.parseResource(downloadRemittanceRequest);
        String expectedRequest = FhirUtils.convertToJSON(requestParameters);

        mockMvc.perform(
                MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(downloadRemittanceRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
                        MockMvcResultMatchers.content().string(expectedRequest)
                );
    }

    @Test
    void testDownloadRemittance_withRequiredParams() throws Exception {
        String downloadRemittanceRequest = TestUtils.getSampleDownloadRemittanceRequiredOnly();

        Parameters requestParameters = (Parameters) FhirUtils.parseResource(downloadRemittanceRequest);
        String expectedRequest = FhirUtils.convertToJSON(requestParameters);

        mockMvc.perform(
                MockMvcRequestBuilders.post(DOWNLOAD_REMITTANCE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(downloadRemittanceRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(), // Expect HTTP 200 (OK)
                        MockMvcResultMatchers.content().string(expectedRequest)
                );
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
