package org.hl7.davinci.pr.api.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hl7.davinci.pr.BaseTest;
import org.hl7.davinci.pr.utils.TestUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationUtilsTest extends BaseTest {

  @Test
  void validateDateOfServiceFormat_validDates() {
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat(""));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat(" "));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat(null));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020-01"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020-01-01"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T23:00:00.000Z"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T23:00:00.000+05:00"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T23:00:00.000-01:00"));
  }

  @Test
  void validateDateOfServiceFormat_invalidDates() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateDateOfServiceFormat("2020-01-32"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateDateOfServiceFormat("2020-13-01"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T23:00:00"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T25:00:00"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T00:60:00"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfServiceFormat("2020-01-01T00:00:60"));
  }

  @Test
  void validateDateOfBirthFormat_validDates() {
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat(""));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat(" "));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat(null));

    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat("2020"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat("2020-01"));
    assertDoesNotThrow(() -> ValidationUtils.validateDateOfBirthFormat("2020-01-01"));
  }

  @Test
  void validateDateOfBirthFormat_invalidDates() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateDateOfBirthFormat("2020-01-32"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateDateOfBirthFormat("2020-13-01"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfBirthFormat("2020-01-01T23:00:00"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfBirthFormat("2020-01-01T23:00:00.000Z"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfBirthFormat("2020-01-01T23:00:00.000+05:00"));
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateDateOfBirthFormat("2020-01-01T23:00:00.000-01:00"));
  }

  @Test
  void validatePeriod_validPeriod() {
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(FhirUtils.generatePeriod("2020", null)));
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(FhirUtils.generatePeriod(null, "2020")));
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(FhirUtils.generatePeriod("2020", "2021")));
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(FhirUtils.generatePeriod("2020-01", "2020-02")));
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(FhirUtils.generatePeriod("2020-01-01", "2020-01-02")));
    assertDoesNotThrow(() -> ValidationUtils.validatePeriod(
        FhirUtils.generatePeriod("2020-01-01T00:00:00.000Z", "2020-01-01T01:00:00.000Z")));
    assertDoesNotThrow(
        () -> ValidationUtils.validatePeriod(
            FhirUtils.generatePeriod("2020-01-01T00:00:00.000-01:00", "2020-01-01T00:00:00.000-02:00")));
    assertDoesNotThrow(
        () -> ValidationUtils.validatePeriod(
            FhirUtils.generatePeriod("2020-01-01T00:00:00.000+02:00", "2020-01-01T00:00:00.000+01:00")));
  }

  @Test
  void validatePeriod_invalidPeriod() {
    Period testPeriod = FhirUtils.generatePeriod("2020", "2020");
    Period testPeriod2 = FhirUtils.generatePeriod("2020-01", "2020-01");
    Period testPeriod3 = FhirUtils.generatePeriod("2020-01-01", "2020-01-01");
    Period testPeriod4 = FhirUtils.generatePeriod("2020-01-01T23:00:00.000Z", "2020-01-01T22:59:00.000Z");
    Period testPeriod5 = FhirUtils.generatePeriod("2020-01-01T23:00:00.000-01:00", "2020-01-01T22:59:00.000-01:00");
    Period testPeriod6 = FhirUtils.generatePeriod("2020-01-01T23:00:00.000+01:00", "2020-01-01T22:59:00.000+01:00");

    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod2));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod3));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod4));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod5));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePeriod(testPeriod6));
  }

  @Test
  void validateTin_nullTin() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin(null));
  }

  @Test
  void validateTin_validTin() {
    assertDoesNotThrow(() -> ValidationUtils.validateTin("123456789"));
    assertDoesNotThrow(() -> ValidationUtils.validateTin("000000000"));
  }

  @Test
  void validateTin_invalidTin() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin("12345678"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin("01010101"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin("000AA000"));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateTin("AAAAAAAAA"));
  }

  @Test
  void validateProviderClaimId_nullProviderClaimId() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateProviderClaimId(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateProviderClaimId(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateProviderClaimId(null));
  }

  @Test
  void validateProviderClaimId_validProviderClaimId() {
    assertDoesNotThrow(() -> ValidationUtils.validateProviderClaimId("12345V6789"));
    assertDoesNotThrow(() -> ValidationUtils.validateProviderClaimId("0000v00000"));
    assertDoesNotThrow(() -> ValidationUtils.validateProviderClaimId("000000000V00000000"));
  }

  @Test
  void validatePatientId_nullPatientId() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePatientId(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePatientId(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePatientId(null));
  }

  @Test
  void validatePatientId_validPatientId() {
    assertDoesNotThrow(() -> ValidationUtils.validatePatientId("1SD45V67D9"));
    assertDoesNotThrow(() -> ValidationUtils.validatePatientId("ABHIDIHCBIHSD"));
    assertDoesNotThrow(() -> ValidationUtils.validatePatientId("0000000000000000"));
  }

  @Test
  void validatePaymentNumber_nullAdviceId() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePaymentNumber(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePaymentNumber(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validatePaymentNumber(null));
  }

  @Test
  void validatePaymentNumber_validAdviceId() {
    assertDoesNotThrow(() -> ValidationUtils.validatePaymentNumber("1SD45V67D9"));
    assertDoesNotThrow(() -> ValidationUtils.validatePaymentNumber("ABHIDIHCBIHSD"));
    assertDoesNotThrow(() -> ValidationUtils.validatePaymentNumber("0000000000000000"));
  }

  @Test
  void validateRemittanceAdviceId_nullAdviceId() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceId(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceId(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceId(null));
  }

  @Test
  void validateRemittanceAdviceId_validAdviceId() {
    assertDoesNotThrow(() -> ValidationUtils.validateRemittanceAdviceId("1SD45V67D9"));
    assertDoesNotThrow(() -> ValidationUtils.validateRemittanceAdviceId("ABHIDIHCBIHSD"));
    assertDoesNotThrow(() -> ValidationUtils.validateRemittanceAdviceId("0000000000000000"));
  }

  @Test
  void validateRemittanceAdviceType_nullAdviceType() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceType(""));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceType(" "));
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateRemittanceAdviceType(null));
  }

  @Test
  void validateRemittanceAdviceType_validAdviceType() {
    assertDoesNotThrow(() -> ValidationUtils.validateRemittanceAdviceType("PDF"));
  }

  @Test
  void validateRemittanceAdviceType_invalidAdviceType() {
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateRemittanceAdviceType("Anything-Not-PDF"));
  }

  @Test
  void validateSearchByClaimRequest_noException() {
    String searchByClaimRequest = TestUtils.getSampleSearchByClaimRequestBody();
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(searchByClaimRequest);
    assertDoesNotThrow(() -> ValidationUtils.validateSearchByClaimRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByClaimRequest_emptyParameters() {
    Parameters searchRequestParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByClaimRequest(searchRequestParameters));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "NoTIN",
                "valueString": "123456"
              },
              {
                "name": "Claim",
                "part": [
                  {
                    "name": "ProviderClaimID",
                    "valueString": "212"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "NoClaim",
                "part": [
                  {
                    "name": "ProviderClaimID",
                    "valueString": "212"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "Claim",
                "part": [
                  {
                    "name": "NoProviderClaimID",
                    "valueString": "212"
                  }
                ]
              }
            ]
          }"""
  })
  void validateSearchByClaimRequest_parametersInput(String input) {
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(input);
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByClaimRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByClaimRequest_emptyParametersInput() {
    Parameters emptyParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByClaimRequest(emptyParameters));
  }

  @Test
  void validateSearchByClaimRequest_nullInput() {
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByClaimRequest(null));
  }

  @Test
  void validateSearchByPatientRequest_noException() {
    String searchByPatientRequest = TestUtils.getSampleSearchByPatientRequestBody();
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(searchByPatientRequest);
    assertDoesNotThrow(() -> ValidationUtils.validateSearchByPatientRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByPatientRequest_emptyParameters() {
    Parameters searchRequestParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPatientRequest(searchRequestParameters));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "NoTIN",
                "valueString": "123456"
              },
              {
                "name": "Patient",
                "part": [
                  {
                    "name": "PatientID",
                    "valueString": "212"
                  },
                  {
                    "name": "DateOfBirth",
                    "valueDate": "1990-04-03"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "NoPatient",
                "part": [
                  {
                    "name": "PatientID",
                    "valueString": "212"
                  },
                  {
                    "name": "DateOfBirth",
                    "valueDate": "1990-04-03"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "Patient",
                "part": [
                  {
                    "name": "NoPatientID",
                    "valueString": "212"
                  },
                  {
                    "name": "DateOfBirth",
                    "valueDate": "1990-04-03"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "Patient",
                "part": [
                  {
                    "name": "PatientID",
                    "valueString": "212"
                  },
                  {
                    "name": "NoDateOfBirth",
                    "valueDate": "1990-04-03"
                  }
                ]
              }
            ]
          }"""
  })
  void validateSearchByPatientRequest_parametersInput(String input) {
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(input);
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPatientRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByPatientRequest_emptyParametersInput() {
    Parameters emptyParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPatientRequest(emptyParameters));
  }

  @Test
  void validateSearchByPatientRequest_nullInput() {
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPatientRequest(null));
  }


  @Test
  void validateSearchByPaymentRequest_noException() {
    String searchByPaymentRequest = TestUtils.getSampleSearchByPaymentRequestBody();
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(searchByPaymentRequest);
    assertDoesNotThrow(() -> ValidationUtils.validateSearchByPaymentRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByPaymentRequest_emptyParameters() {
    Parameters searchRequestParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPaymentRequest(searchRequestParameters));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "NoTIN",
                "valueString": "123456"
              },
              {
                "name": "PaymentInfo",
                "part": [
                  {
                    "name": "PaymentIssueDate",
                    "valuePeriod": {
                      "start": "2024-08-01T13:28:16-05:00",
                      "end": "2024-08-12T13:28:17-05:00"
                    }
                  },
                  {
                    "name": "PaymentNumber",
                    "valueString": "1234567"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "NoPayment",
                "part": [
                  {
                    "name": "PaymentIssueDate",
                    "valuePeriod": {
                      "start": "2024-08-01T13:28:16-05:00",
                      "end": "2024-08-12T13:28:17-05:00"
                    }
                  },
                  {
                    "name": "PaymentNumber",
                    "valueString": "1234567"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "PaymentInfo",
                "part": [
                  {
                    "name": "NoPaymentIssueDate",
                    "valuePeriod": {
                      "start": "2024-08-01T13:28:16-05:00",
                      "end": "2024-08-12T13:28:17-05:00"
                    }
                  },
                  {
                    "name": "PaymentNumber",
                    "valueString": "1234567"
                  }
                ]
              }
            ]
          }""",
      """
          {
            "resourceType": "Parameters",
            "parameter": [
              {
                "name": "TIN",
                "valueString": "123456789"
              },
              {
                "name": "PaymentInfo",
                "part": [
                  {
                    "name": "PaymentIssueDate",
                    "valuePeriod": {
                      "start": "2024-08-01T13:28:16-05:00",
                      "end": "2024-08-12T13:28:17-05:00"
                    }
                  },
                  {
                    "name": "NoPaymentNumber",
                    "valueString": "1234567"
                  }
                ]
              }
            ]
          }"""
  })
  void validateSearchByPaymentRequest_parametersInput(String input) {
    Parameters searchRequestParameters = (Parameters) FhirUtils.parseResource(input);
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPaymentRequest(searchRequestParameters));
  }

  @Test
  void validateSearchByPaymentRequest_emptyParametersInput() {
    Parameters emptyParameters = new Parameters();
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPaymentRequest(emptyParameters));
  }

  @Test
  void validateSearchByPaymentRequest_nullInput() {
    assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateSearchByPaymentRequest(null));
  }
}