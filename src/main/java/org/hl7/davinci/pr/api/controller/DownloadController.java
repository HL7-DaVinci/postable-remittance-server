package org.hl7.davinci.pr.api.controller;

import static org.hl7.davinci.pr.api.controller.SearchController.RESULTS_NOT_FOUND_MESSAGE;
import static org.hl7.davinci.pr.api.utils.ApiConstants.DOWNLOAD_REMITTANCE_EXAMPLE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.api.utils.ValidationUtils;
import org.hl7.davinci.pr.service.DownloadService;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("")
public class DownloadController {

  public static final String DOWNLOAD_REMITTANCE_ENDPOINT = "/$downloadRemittance";

  @Autowired
  private DownloadService downloadService;

  /**
   * Download remittance by providing remittance advice id
   *
   * @param httpEntity the HTTP entity containing the request body
   * @return a ResponseEntity with the response body, status, and headers
   */
  @Operation(
      tags = {ApiConstants.OPEN_API_DOWNLOAD_REMITTANCE_ENDPOINT},
      summary = ApiConstants.OPEN_API_DESCRIPTION_DOWNLOAD_REMITTANCE_ENDPOINT
  )
  @PostMapping(
      path = DOWNLOAD_REMITTANCE_ENDPOINT,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> downloadRemittance(
      @RequestBody(required = true, content = @Content(schema = @Schema(example = DOWNLOAD_REMITTANCE_EXAMPLE))) HttpEntity<String> httpEntity) {

    String responseBody = "";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    try {
      Parameters requestResource = (Parameters) FhirUtils.parseResource(httpEntity.getBody());
      ValidationUtils.validateDownloadRemittanceAdviceRequest(requestResource);

      String remittanceAdviceId = requestResource.getParameter(ApiConstants.REMITTANCE_ADVICE_IDENTIFIER).getValue().toString();
      //optional, so either null or should have a value and it should be validated by now
      String remittanceAdviceType = (requestResource.hasParameter(ApiConstants.REMITTANCE_ADVICE_TYPE))?requestResource.getParameter(ApiConstants.REMITTANCE_ADVICE_TYPE).getValue().toString():null;

      DocumentReference documentReference = downloadService.downloadDocument(remittanceAdviceId, remittanceAdviceType);
      //no remittance advice has been found
      if(documentReference == null) {
        responseBody = FhirUtils.convertToJSON(
                FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID,
                        String.format(RESULTS_NOT_FOUND_MESSAGE, DOWNLOAD_REMITTANCE_ENDPOINT)));
        httpStatus = HttpStatus.NOT_FOUND;
      } else {
        responseBody = FhirUtils.convertToJSON(documentReference);
        httpStatus = HttpStatus.OK;
      }

    } catch (Exception e) {
      OperationOutcome error = FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID, e.getMessage());
      responseBody = FhirUtils.convertToJSON(error);
      log.error(String.format("POST Endpoint %s failed with response: %s", DOWNLOAD_REMITTANCE_ENDPOINT, responseBody));
    }
    log.info(String.format("POST Endpoint %s returned with response: %s", DOWNLOAD_REMITTANCE_ENDPOINT, responseBody));
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_FHIR_JSON)
        .header(HttpHeaders.LOCATION, DOWNLOAD_REMITTANCE_ENDPOINT)
        .body(responseBody);
  }
}