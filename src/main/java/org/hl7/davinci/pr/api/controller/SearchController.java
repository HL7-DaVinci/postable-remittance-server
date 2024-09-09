package org.hl7.davinci.pr.api.controller;

import static org.hl7.davinci.pr.api.utils.ApiConstants.SEARCH_BY_CLAIM_EXAMPLE;
import static org.hl7.davinci.pr.api.utils.ApiConstants.SEARCH_BY_PATIENT_EXAMPLE;
import static org.hl7.davinci.pr.api.utils.ApiConstants.SEARCH_BY_PAYMENT_EXAMPLE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.api.utils.ValidationUtils;
import org.hl7.davinci.pr.service.SearchService;
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
public class SearchController {

  private final SearchService searchService;
  public static final String SEARCH_BY_CLAIM_ENDPOINT = "/$searchByClaim";
  public static final String SEARCH_BY_PATIENT_ENDPOINT = "/$searchByPatient";
  public static final String SEARCH_BY_PAYMENT_ENDPOINT = "/$searchByPayment";
  public static final String RESULTS_NOT_FOUND_MESSAGE = "POST Endpoint %s is unable to find any records.";

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Search by claim endpoint that validates and processes the search request.
   *
   * @param httpEntity the HTTP entity containing the request body
   * @return a ResponseEntity with the response body, status, and headers
   */
  @Operation(
      tags = {ApiConstants.OPEN_API_TAG_SEARCH_BY_CLAIM_ENDPOINT},
      summary = ApiConstants.OPEN_API_DESCRIPTION_SEARCH_BY_CLAIM_ENDPOINT
  )
  @PostMapping(
      path = SEARCH_BY_CLAIM_ENDPOINT,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> searchByClaim(
      @RequestBody(required = true, content = @Content(schema = @Schema(example = SEARCH_BY_CLAIM_EXAMPLE))) HttpEntity<String> httpEntity) {

    String responseBody = "";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    try {
      // Validate and parse the request
      Parameters requestResource = (Parameters) FhirUtils.parseResource(httpEntity.getBody());
      ValidationUtils.validateSearchByClaimRequest(requestResource);
      String requestBody = FhirUtils.convertToJSON(requestResource);
      log.info(String.format("POST Endpoint %s is called with request: %s", SEARCH_BY_CLAIM_ENDPOINT, requestBody));

      // Search
      Parameters responseResource = this.searchService.searchByClaim(requestResource);

      if (responseResource == null || responseResource.isEmpty()) {
        // Unable to find any records
        responseBody = FhirUtils.convertToJSON(
            FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID,
                String.format(RESULTS_NOT_FOUND_MESSAGE, SEARCH_BY_CLAIM_ENDPOINT)));
        httpStatus = HttpStatus.NOT_FOUND;
      } else {
        responseBody = FhirUtils.convertToJSON(responseResource);
        httpStatus = HttpStatus.OK;
      }
    } catch (Exception e) {
      OperationOutcome error = FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID, e.getMessage());
      responseBody = FhirUtils.convertToJSON(error);
      log.error(String.format("POST Endpoint %s failed with response: %s", SEARCH_BY_CLAIM_ENDPOINT, responseBody));
    }
    log.info(String.format("POST Endpoint %s returned with response: %s", SEARCH_BY_CLAIM_ENDPOINT, responseBody));
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_FHIR_JSON)
        .header(HttpHeaders.LOCATION, SEARCH_BY_CLAIM_ENDPOINT)
        .body(responseBody);
  }

  /**
   * Search by patient endpoint that validates and processes the search request.
   *
   * @param httpEntity the HTTP entity containing the request body
   * @return a ResponseEntity with the response body, status, and headers
   */
  @Operation(
      tags = {ApiConstants.OPEN_API_TAG_SEARCH_BY_PATIENT_ENDPOINT},
      summary = ApiConstants.OPEN_API_DESCRIPTION_SEARCH_BY_PATIENT_ENDPOINT
  )
  @PostMapping(
      path = SEARCH_BY_PATIENT_ENDPOINT,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> searchByPatient(
      @RequestBody(required = true, content = @Content(schema = @Schema(example = SEARCH_BY_PATIENT_EXAMPLE))) HttpEntity<String> httpEntity) {

    String responseBody = "";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    try {
      // Validate and parse the request
      Parameters requestResource = (Parameters) FhirUtils.parseResource(httpEntity.getBody());
      ValidationUtils.validateSearchByPatientRequest(requestResource);
      String requestBody = FhirUtils.convertToJSON(requestResource);
      log.info(String.format("POST Endpoint %s is called with request: %s", SEARCH_BY_PATIENT_ENDPOINT, requestBody));

      // Search
      Parameters responseResource = this.searchService.searchByPatient(requestResource);

      if (responseResource == null || responseResource.isEmpty()) {
        // Unable to find any records
        responseBody = FhirUtils.convertToJSON(
            FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID,
                String.format(RESULTS_NOT_FOUND_MESSAGE, SEARCH_BY_PATIENT_ENDPOINT)));
        httpStatus = HttpStatus.NOT_FOUND;
      } else {
        responseBody = FhirUtils.convertToJSON(responseResource);
        httpStatus = HttpStatus.OK;
      }
    } catch (Exception e) {
      OperationOutcome error = FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID, e.getMessage());
      responseBody = FhirUtils.convertToJSON(error);
      log.error(String.format("POST Endpoint %s failed with response: %s", SEARCH_BY_PATIENT_ENDPOINT, responseBody));
    }
    log.info(String.format("POST Endpoint %s returned with response: %s", SEARCH_BY_PATIENT_ENDPOINT, responseBody));
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_FHIR_JSON)
        .header(HttpHeaders.LOCATION, SEARCH_BY_PATIENT_ENDPOINT)
        .body(responseBody);
  }

  /**
   * Search by payment endpoint that validates and processes the search request.
   *
   * @param httpEntity the HTTP entity containing the request body
   * @return a ResponseEntity with the response body, status, and headers
   */
  @Operation(
      tags = {ApiConstants.OPEN_API_TAG_SEARCH_BY_PAYMENT_ENDPOINT},
      summary = ApiConstants.OPEN_API_DESCRIPTION_SEARCH_BY_PAYMENT_ENDPOINT
  )
  @PostMapping(
      path = SEARCH_BY_PAYMENT_ENDPOINT,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> searchByPayment(
      @RequestBody(required = true, content = @Content(schema = @Schema(example = SEARCH_BY_PAYMENT_EXAMPLE))) HttpEntity<String> httpEntity) {

    String responseBody = "";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    try {
      // Validate and parse the request
      Parameters requestResource = (Parameters) FhirUtils.parseResource(httpEntity.getBody());
      ValidationUtils.validateSearchByPaymentRequest(requestResource);
      String requestBody = FhirUtils.convertToJSON(requestResource);
      log.info(String.format("POST Endpoint %s is called with request: %s", SEARCH_BY_PAYMENT_ENDPOINT, requestBody));

      // Search
      Parameters responseResource = this.searchService.searchByPayment(requestResource);

      if (responseResource == null || responseResource.isEmpty()) {
        // Unable to find any records
        responseBody = FhirUtils.convertToJSON(
            FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID,
                String.format(RESULTS_NOT_FOUND_MESSAGE, SEARCH_BY_PAYMENT_ENDPOINT)));
        httpStatus = HttpStatus.NOT_FOUND;
      } else {
        responseBody = FhirUtils.convertToJSON(responseResource);
        httpStatus = HttpStatus.OK;
      }
    } catch (Exception e) {
      OperationOutcome error = FhirUtils.generateErrorOutcome(IssueSeverity.ERROR, IssueType.INVALID, e.getMessage());
      responseBody = FhirUtils.convertToJSON(error);
      log.error(String.format("POST Endpoint %s failed with response: %s", SEARCH_BY_PAYMENT_ENDPOINT, responseBody));
    }
    log.info(String.format("POST Endpoint %s returned with response: %s", SEARCH_BY_PAYMENT_ENDPOINT, responseBody));
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_FHIR_JSON)
        .header(HttpHeaders.LOCATION, SEARCH_BY_PAYMENT_ENDPOINT)
        .body(responseBody);
  }
}