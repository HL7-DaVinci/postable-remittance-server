package org.hl7.davinci.pr.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hl7.davinci.pr.BaseTest;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Period;
import org.junit.jupiter.api.Test;

class FhirUtilsTest extends BaseTest {

  @Test
  void convertToJSON_Successful() {
    OperationOutcome outcome = new OperationOutcome();
    outcome.addIssue()
        .setSeverity(IssueSeverity.ERROR)
        .setCode(IssueType.INVALID)
        .setDiagnostics("TestErrorDiagnostics");

    String expectedJson = """
        {
          "resourceType": "OperationOutcome",
          "issue": [ {
            "severity": "error",
            "code": "invalid",
            "diagnostics": "TestErrorDiagnostics"
          } ]
        }""";

    String actualJson = FhirUtils.convertToJSON(outcome);
    assertEquals(actualJson, expectedJson);
  }

  @Test
  void convertToJSON_ThrowsException() {
    OperationOutcome outcome = new OperationOutcome();
    assertThrows(IllegalArgumentException.class, () -> FhirUtils.convertToJSON(outcome));
  }

  @Test
  void parseResource_Successful() {
    OperationOutcome expectedResource = new OperationOutcome();
    expectedResource.addIssue()
        .setSeverity(IssueSeverity.ERROR)
        .setCode(IssueType.INVALID)
        .setDiagnostics("TestErrorDiagnostics");

    String resourceString = """
        {
          "resourceType": "OperationOutcome",
          "issue": [ {
            "severity": "error",
            "code": "invalid",
            "diagnostics": "TestErrorDiagnostics"
          } ]
        }""";

    OperationOutcome actualResource = (OperationOutcome) FhirUtils.parseResource(resourceString);
    assertTrue(expectedResource.equalsDeep(actualResource));
  }

  @Test
  void parseResource_ThrowsException() {
    String resourceString = """
        {
          "resource": ""
        }""";
    assertThrows(IllegalArgumentException.class, () -> FhirUtils.parseResource(resourceString));
  }

  @Test
  void generateErrorOutcome_Successful() {
    OperationOutcome outcome = new OperationOutcome();
    outcome.addIssue()
        .setSeverity(IssueSeverity.ERROR)
        .setCode(IssueType.INVALID)
        .setDiagnostics("TestErrorDiagnostics");

    OperationOutcome error = FhirUtils.generateErrorOutcome(
        outcome.getIssue().get(0).getSeverity(),
        outcome.getIssue().get(0).getCode(),
        outcome.getIssue().get(0).getDiagnostics());

    assertTrue(error.equalsDeep(outcome));
  }

  @Test
  void generatePeriod_Successful() {
    String startDate = "2023-10-10T13:28:16-05:00";
    String endDate = "2023-10-10T14:28:16-05:00";
    Period expectedPeriod = new Period();
    expectedPeriod.setStartElement(new DateTimeType(startDate));
    expectedPeriod.setEndElement(new DateTimeType(endDate));

    Period actualPeriod = FhirUtils.generatePeriod(startDate, endDate);
    assertTrue(expectedPeriod.equalsDeep(actualPeriod));
  }

  @Test
  void generatePeriod_ThrowsException() {
    String startDate = "2023-10-10T13:28:16-05:00";
    String endDate = "2023-10-10T";
    assertThrows(IllegalArgumentException.class, () -> FhirUtils.generatePeriod(startDate, endDate));
  }

}