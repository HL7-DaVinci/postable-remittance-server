package org.hl7.davinci.pr.api.utils;

import static org.hl7.davinci.pr.PostableRemittanceApplication.getFhirContext;

import ca.uhn.fhir.parser.DataFormatException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Period;

@UtilityClass
public class FhirUtils {

  public static final String RESOURCE_PARSING_EXCEPTION = "FHIR Resource parsing failure: ";
  public static final String RESOURCE_EMPTY_EXCEPTION = "FHIR Resource is empty";
  public static final String DATE_TYPE_FORMAT = "yyyy-MM-dd";
  public static final String MALFORMED_BODY = "Malformed body";

  /**
   * Convert a FHIR resource into JSON.
   *
   * @param resource - the resource to convert to JSON.
   * @return String - the JSON.
   */
  public String convertToJSON(IBaseResource resource) {
    if (!resource.isEmpty()) {
      return getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    } else {
      throw new IllegalArgumentException(RESOURCE_EMPTY_EXCEPTION);
    }
  }

  /**
   * Parses a resource from a given string.
   *
   * @param resourceString the string representation of the resource
   * @return the parsed resource
   * @throws IllegalArgumentException if there is an error parsing the resource
   */
  public IBaseResource parseResource(String resourceString) {
    try {
      return getFhirContext().newJsonParser().parseResource(resourceString);
    } catch (DataFormatException e) {
      throw new IllegalArgumentException(RESOURCE_PARSING_EXCEPTION + MALFORMED_BODY);
    }
  }


  /**
   * Creates an OperationOutcome object with the specified severity, type, and message.
   *
   * @param severity the severity of the issue
   * @param type the type of the issue
   * @param message the diagnostic message
   * @return the created OperationOutcome object
   */
  public OperationOutcome generateErrorOutcome(IssueSeverity severity, IssueType type, String message) {
    OperationOutcome error = new OperationOutcome();
    error.addIssue()
        .setSeverity(severity)
        .setCode(type)
        .setDiagnostics(message);
    return error;
  }

  /**
   * Creates a Period object from the given start and end dates.
   *
   * @param start the start date of the period
   * @param end the end date of the period
   * @return the created Period object
   */
  public Period generatePeriod(String start, String end) {
    try {
      Period period = new Period();
      period.setStartElement(new DateTimeType(start));
      period.setEndElement(new DateTimeType(end));
      return period;
    } catch (DataFormatException e) {
      throw new IllegalArgumentException(RESOURCE_PARSING_EXCEPTION + e.getMessage());
    }
  }

  /**
   * Generates a DateType object from a given Date object.
   *
   * @param date the date to be converted to DateType
   * @return the DateType representation of the given date in the format specified by DATE_TYPE_FORMAT
   */
  public DateType generateDateType(Date date) {
    return new DateType(
        date.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(DATE_TYPE_FORMAT)));
  }

  /**
   * Generates a unique identifier for a resource.
   *
   * @return a unique identifier as a string
   */
  public String generateUniqueResourceID() {
    return UUID.randomUUID().toString();
  }

  /**
   * Generates a Meta object with a profile URL.
   *
   * @param profileUrl the URL of the profile to be added to the Meta object
   * @return a Meta object with the added profile URL
   */
  public Meta generateResourceMeta(String profileUrl) {
    Meta meta = new Meta();
    meta.addProfile(profileUrl);
    return meta;
  }

}
