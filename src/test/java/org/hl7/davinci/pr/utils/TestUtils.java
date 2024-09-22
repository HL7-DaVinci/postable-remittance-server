package org.hl7.davinci.pr.utils;

import jakarta.persistence.Tuple;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.sql.results.internal.TupleImpl;
import org.hibernate.sql.results.internal.TupleMetadata;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.domain.Remittance;
import org.hl7.davinci.pr.domain.SubscriberPatient;

import static org.hl7.davinci.pr.utils.TestDataUtils.REMITTANCE_ADVICEID_1;

@UtilityClass
public class TestUtils {

  public Integer randomInteger() {
    return (new Random()).nextInt(Integer.MAX_VALUE);
  }

  public Integer randomInteger(final int max) {
    return (new Random()).nextInt(max);
  }

  public Float randomFloat(final int max) {
    return (new Random()).nextFloat(max);
  }

  public String randomAlphabeticString(final int max) {
    return RandomStringUtils.randomAlphabetic(max);
  }

  public String randomAlphanumericString(final int max) {
    return RandomStringUtils.randomNumeric(max);
  }

  public <T> T randomElement(final List<T> list) {
    return list.get(randomInteger(list.size()));
  }

  public Instant randomInstant() {
    return Instant.now(Clock.systemUTC()).plusSeconds((long) randomInteger(100));
  }

  public String getSampleDownloadRemittanceRequestBody(String remittanceAdviceId) {
    return String.format("""
              {
                   "resourceType" : "Parameters",
                   "id" : "ExampleDownloadRemittance",
                   "meta" : {
                     "profile" : [
                       "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/downloadRemittanceParameters"
                     ]
                   },
                   "parameter" : [
                     {
                       "name" : "RemittanceAdviceIdentifier",
                       "valueString" : "%s"
                     },
                     {
                       "name" : "RemittanceAdviceType",
                        "valueCode" : "PDF"
                     }
                   ]
                 }""", remittanceAdviceId);
  }

  public String getSampleDownloadRemittanceRequestBody_missingIdValue() {
    return """
              {
                   "resourceType" : "Parameters",
                   "id" : "ExampleDownloadRemittance",
                   "meta" : {
                     "profile" : [
                       "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/downloadRemittanceParameters"
                     ]
                   },
                   "parameter" : [
                     {
                       "name" : "RemittanceAdviceIdentifier"
                     },
                     {
                       "name" : "RemittanceAdviceType",
                        "valueCode" : "PDF"
                     }
                   ]
                 }""";
  }

  public String getSampleDownloadRemittanceNoRequired() {
    return """
              {
                   "resourceType" : "Parameters",
                   "id" : "ExampleDownloadRemittance",
                   "meta" : {
                     "profile" : [
                       "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/downloadRemittanceParameters"
                     ]
                   },
                   "parameter" : [
                     {
                       "name" : "RemittanceAdviceType",
        "valueCode" : "PDF"
                     }
                   ]
                 }""";
  }

  public String getSampleDownloadRemittanceResponse(String contentType) {
    return String.format("""
            {
                   "resourceType": "Binary",
                   "id": "remittance-document-b6445654-586d-42de-98f7-a0e82a070896",
                   "meta": {
                     "profile": [ "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/remittanceAdviceDocumentt" ]
                   },
                   "contentType" : "application/%s+gzip",
                   "data" : "sample"
            }
       """, contentType);
  }

  public String getSampleDownloadRemittanceRequiredOnly(String remittanceAdviceId) {
    return String.format("""
              {
                   "resourceType" : "Parameters",
                   "id" : "ExampleDownloadRemittance",
                   "meta" : {
                     "profile" : [
                       "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/downloadRemittanceParameters"
                     ]
                   },
                   "parameter" : [
                     {
                       "name" : "RemittanceAdviceIdentifier",
                       "valueString" : "%s"
                     }
                   ]
                 }""", remittanceAdviceId);
  }

  public String getSampleSearchByClaimRequestBody() {
    return """
        {
          "resourceType": "Parameters",
          "parameter": [
            {
              "name": "TIN",
              "valueString": "123456789"
            },
            {
              "name": "DateOfService",
              "valuePeriod": {
                "start": "2023-10-10T13:28:16-05:00",
                "end": "2023-10-10T13:28:17-05:00"
              }
            },
            {
              "name": "PatientID",
              "valueString": "58965"
            },
            {
              "name": "Claim",
              "part": [
                {
                  "name": "ProviderClaimID",
                  "valueString": "212"
                },
                {
                  "name": "ProviderID",
                  "valueString": "36363"
                },
                {
                  "name": "PayerClaimID",
                  "valueString": "41414"
                },
                {
                  "name": "ClaimChargeAmount",
                  "valueString": "34567"
                }
              ]
            },
            {
              "name": "PayerID",
              "valueString": "123456"
            },
            {
              "name": "PayerName",
              "valueString": "123456"
            }
          ]
        }""";
  }

  public String getSearchByClaimRequestBodyRequiredOnly() {
    return """
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
                  "name": "ProviderClaimID",
                  "valueString": "212"
                }
              ]
            }
          ]
        }""";
  }


  public String getSampleSearchByPatientRequestBody() {
    return """
        {
            "resourceType": "Parameters",
            "parameter": [
                {
                    "name": "TIN",
                    "valueString": "123456789"
                },
                {
                    "name": "DateOfService",
                    "valuePeriod": {
                        "start": "2024-08-01T13:28:16-05:00",
                        "end": "2024-08-12T13:28:17-05:00"
                    }
                },
                {
                    "name": "Patient",
                    "part": [
                        {
                            "name": "PatientID",
                            "valueString": "1"
                        },
                        {
                            "name": "DateOfBirth",
                            "valueDate": "1990-04-03"
                        },
                        {
                            "name": "PatientFirstName",
                            "valueString": "John"
                        },
                        {
                            "name": "PatientLastName",
                            "valueString": "Doe"
                        }
                    ]
                },
                {
                    "name": "PayerID",
                    "valueString": "1010101"
                },
                {
                    "name": "PayerName",
                    "valueString": "payer_random1"
                }
            ]
        }""";
  }

  public String getSearchByPatientRequestBodyRequiredOnly() {
    return """
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
                             "valueString": "1"
                         },
                         {
                             "name": "DateOfBirth",
                             "valueDate": "1990-04-03"
                         }
                     ]
                 }
             ]
         }""";
  }

  public String getSampleSearchByPaymentRequestBody() {
    return """
        {
            "resourceType": "Parameters",
            "parameter": [
                {
                    "name": "TIN",
                    "valueString": "123456789"
                },
                {
                    "name": "DateOfService",
                    "valuePeriod": {
                        "start": "2024-08-01T13:28:16-05:00",
                        "end": "2024-08-12T13:28:17-05:00"
                    }
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
                            "name": "PaymentAmount",
                            "part": [
                                {
                                    "name": "PaymentAmountLow",
                                    "valueMoney": {
                                        "value": 100.00,
                                        "currency": "USD"
                                    }
                                },
                                {
                                    "name": "PaymentAmountHigh",
                                    "valueMoney": {
                                        "value": 150.00,
                                        "currency": "USD"
                                    }
                                }
                            ]
                        },
                        {
                            "name": "PaymentNumber",
                            "valueString": "1234567"
                        }
                    ]
                },
                {
                    "name": "PayerID",
                    "valueString": "1010101"
                },
                {
                    "name": "PayerName",
                    "valueString": "payer_random1"
                }
            ]
        }""";
  }

  public String getSearchByPaymentRequestBodyRequiredOnly() {
    return """
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
                            "name": "PaymentNumber",
                            "valueString": "1234567"
                        }
                    ]
                }
            ]
        }""";
  }

  public Payer getSamplePayer(String payerId, String payerName) {
    return Payer.builder()
        .payerIdentity(payerId != null ? payerId : String.valueOf(randomInteger(5)))
        .payerName(payerName != null ? payerName : randomAlphabeticString(10))
        .build();
  }

  public Payment getSamplePayment(String paymentNumber) {
    try {
      return Payment.builder()
          .payment_issue_dt(TestDataUtils.dateFormatter.parse(TestDataUtils.PAYMENT_ISSUE_DATE))
          .paymentNumber(paymentNumber != null ? paymentNumber : randomAlphanumericString(10))
          .amount(TestDataUtils.PAYMENT_AMOUNT).remittance(getSampleRemittance(REMITTANCE_ADVICEID_1))
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public ClaimQuery getSampleClaimQuery(String tin, String providerClaimId) {
    try {
      return ClaimQuery.builder()
          .providerTin(tin != null ? tin : String.valueOf(randomInteger(9)))
          .providerClaimID(providerClaimId != null ? providerClaimId : String.valueOf(randomInteger(5)))
          .receivedDate(TestDataUtils.dateFormatter.parse(TestDataUtils.DATE_OF_SERVICE))
          .providerNPI(TestDataUtils.PROVIDER_NPI_1)
          .payerClaimId(TestDataUtils.PAYER_CLAIM_ID_1)
          .subscriberPatientId(TestDataUtils.PATIENT_ID_1)
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public Patient getSamplePatient(String patientId) {
    SubscriberPatient subscriberPatient = new SubscriberPatient();
    subscriberPatient.setSubscriberPatientId(patientId);

    try {
      return Patient.builder()
          .dateOfBirth(TestDataUtils.dateFormatter.parse(TestDataUtils.PATIENT_DOB))
          .id(1)
          .firstName(TestDataUtils.PATIENT_FIRST_NAME_1)
          .lastName(TestDataUtils.PATIENT_LAST_NAME_1)
          .subscriberPatients(Collections.singletonList(subscriberPatient))
          .dateOfBirth(TestDataUtils.dateFormatter.parse(TestDataUtils.PATIENT_DOB))
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public Remittance getSampleRemittance(String remittanceAdviceId) {
    return Remittance.builder()
        .remittanceAdviceId(remittanceAdviceId != null ? remittanceAdviceId : randomInteger(5).toString())
        .remittanceAdviceType(ApiConstants.REMITTANCE_ADVICE_TYPE_PDF)
        .remittanceAdviceDate(new Date())
        .remittanceAdviceFileSize(TestDataUtils.REMITTANCE_ADVICE_FILE_SIZE)
        .build();
  }

  public Tuple generateSampleFindByClaimOrPatientDaoTuple(ClaimQuery claimQuery, Patient patient, Payer payer,
      Payment payment, Remittance remittance) {
    return new TupleImpl(
        new TupleMetadata(null, null),
        List.of(claimQuery, patient, payer, payment, remittance).toArray());
  }

  public Tuple generateSampleFindByPaymentDaoTuple(ClaimQuery claimQuery, Payer payer, Payment payment,
      Remittance remittance) {
    return new TupleImpl(
        new TupleMetadata(null, null),
        List.of(claimQuery, payer, payment, remittance).toArray());
  }
}
