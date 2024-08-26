package org.hl7.davinci.pr.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.api.utils.ValidationUtils;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Parameters;
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
  public static final String DOWNLOAD_REMITTANCE_NOT_FOUND_MESSAGE =
      DOWNLOAD_REMITTANCE_ENDPOINT + " is unable to find any records.";

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
  public ResponseEntity<String> downloadRemittance(@RequestBody(required = true) HttpEntity<String> httpEntity) {

    String responseBody = "";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    try {
      Parameters requestResource = (Parameters) FhirUtils.parseResource(httpEntity.getBody());
      ValidationUtils.validateDownloadRemittanceAdviceRequest(requestResource);
      responseBody = FhirUtils.convertToJSON(requestResource);

      log.info(
          String.format("POST Endpoint %s is called with request: %s", DOWNLOAD_REMITTANCE_ENDPOINT, responseBody));
      httpStatus = HttpStatus.OK;

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