package org.hl7.davinci.pr.api.utils;

import static org.hl7.davinci.pr.api.utils.ApiConstants.REMITTANCE_ADVICE_TYPE_835;
import static org.hl7.davinci.pr.api.utils.ApiConstants.REMITTANCE_ADVICE_TYPE_PDF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.util.StringUtils;

@UtilityClass
public class ValidationUtils {

  // DateTime Validation Regex from https://hl7.org/fhir/R4/datatypes.html#dateTime
  public final String DATE_OF_SERVICE_VALIDATION_REGEX = "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)(-(0[1-9]|1[0-2])(-(0[1-9]|[1-2][0-9]|3[0-1])(T([01][0-9]|2[0-3]):[0-5][0-9]:([0-5][0-9]|60)(\\.[0-9]+)?(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00)))?)?)?";
  public static final String TIN_VALIDATION_REGEX = "\\d{9}";
  // Date Validation Regex from https://hl7.org/fhir/R4/datatypes.html#date
  public static final String DATE_FORMAT_VALIDATION_REGEX = "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)(-(0[1-9]|1[0-2])(-(0[1-9]|[1-2][0-9]|3[0-1]))?)?";

  // Validation Messages
  public final String DATE_OF_SERVICE_VALIDATION_MESSAGE = "Invalid Date Format. Date %s must be in the format YYYY, YYYY-MM, YYYY-MM-DD or YYYY-MM-DDThh:mm:ss+zz:zz, e.g. 2018, 1973-06, 1905-08-23, 2015-02-07T13:28:17-05:00 or 2017-01-01T00:00:00.000Z";
  public final String DATE_OF_BIRTH_VALIDATION_MESSAGE = "Invalid Date Format. Date %s must be in the format YYYY, YYYY-MM, or YYYY-MM-DD, e.g. 2018, 1973-06, or 1905-08-23. There SHALL be no time zone.";
  public final String PERIOD_VALIDATION_MESSAGE = "The date %s must be before date %s";
  public final String TIN_VALUE_VALIDATION_MESSAGE = "TIN %s must be exactly 9 digits long.";
  public final String TIN_REQUIRED_MESSAGE = "TIN is required.";
  public final String CLAIM_REQUIRED_MESSAGE = "Claim is required.";
  public final String PROVIDER_CLAIM_ID_REQUIRED_MESSAGE = "ProviderClaimId is required.";
  public final String PATIENT_REQUIRED_MESSAGE = "Patient is required.";
  public final String PATIENT_ID_REQUIRED_MESSAGE = "PatientId is required.";
  public final String DATE_OF_BIRTH_REQUIRED_MESSAGE = "Date of Birth is required.";
  public final String PAYMENT_REQUIRED_MESSAGE = "Payment is required.";
  public final String PAYMENT_ISSUE_DATE_REQUIRED_MESSAGE = "PaymentIssueDate is required.";
  public final String PAYMENT_NUMBER_REQUIRED_MESSAGE = "PaymentNumber is required.";
  public final String REMITTANCE_ADVICE_ID_REQUIRED_MESSAGE = "Remittance Advice Identifier is required.";
  private static final String REMITTANCE_ADVICE_TYPE_REQUIRED_MESSAGE = "Remittance Advice Type is required.";
  private static final String REMITTANCE_ADVICE_TYPE_FORMAT_MESSAGE = "Remittance Advice Type should be either PDF or 835.";
  public final String REQUEST_PARAMETERS_EMPTY_MESSAGE = "Request parameters should not be empty.";
  public final String REMITTANCE_ADVICE_REQUEST_PARAMETERS_EMPTY_MESSAGE = "Download Remittance Advice Parameters should not be empty.";


  /**
   * Validates the format of a given date of service string.
   *
   * @param date the date of service string to be validated
   * @throws IllegalArgumentException if the date string does not match the expected format
   */
  public void validateDateOfServiceFormat(String date) {
    if (StringUtils.hasText(date) && !date.matches(DATE_OF_SERVICE_VALIDATION_REGEX)) {
      throw new IllegalArgumentException(String.format(DATE_OF_SERVICE_VALIDATION_MESSAGE, date));
    }
  }

  /**
   * Validates the format of a given date string for date of birth.
   *
   * @param date the date string to be validated
   * @throws IllegalArgumentException if the date string does not match the expected format
   */
  public void validateDateOfBirthFormat(String date) {
    if (StringUtils.hasText(date) && !date.matches(DATE_FORMAT_VALIDATION_REGEX)) {
      throw new IllegalArgumentException(String.format(DATE_OF_BIRTH_VALIDATION_MESSAGE, date));
    }
  }

  /**
   * Validates the start and end dates of a period.
   *
   * @param period the period to be validated
   * @throws IllegalArgumentException if the start date is after the end date
   * @
   */
  public void validatePeriod(Period period) {
    if (period.getStart() != null && period.getEnd() != null && !period.getStart().before(period.getEnd())) {
      throw new IllegalArgumentException(String.format(PERIOD_VALIDATION_MESSAGE, period.getStart(), period.getEnd()));
    }
  }

  /**
   * Validates the TIN (Tax Identification Number) provided.
   *
   * @param tin the TIN to be validated
   * @throws IllegalArgumentException if the TIN is empty or in the incorrect format
   */
  public void validateTin(String tin) {
    if (StringUtils.hasText(tin)) {
      if (!tin.matches(TIN_VALIDATION_REGEX)) {
        throw new IllegalArgumentException(String.format(TIN_VALUE_VALIDATION_MESSAGE, tin));
      }
    } else {
      throw new IllegalArgumentException(TIN_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates the provider claim ID.
   *
   * @param providerClaimId the provider claim ID to be validated
   * @throws IllegalArgumentException if the provider claim ID is empty
   */
  public void validateProviderClaimId(String providerClaimId) {
    if (!StringUtils.hasText(providerClaimId)) {
      throw new IllegalArgumentException(PROVIDER_CLAIM_ID_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates the patient ID.
   *
   * @param patientId the provider claim ID to be validated
   * @throws IllegalArgumentException if the patientID is empty
   */
  public void validatePatientId(String patientId) {
    if (!StringUtils.hasText(patientId)) {
      throw new IllegalArgumentException(PATIENT_ID_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates the patient ID.
   *
   * @param paymentNumber the payment number to be validated
   * @throws IllegalArgumentException if the payment number is empty
   */
  public void validatePaymentNumber(String paymentNumber) {
    if (!StringUtils.hasText(paymentNumber)) {
      throw new IllegalArgumentException(PAYMENT_NUMBER_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates if the remittance advice ID is not empty.
   *
   * @param remittanceAdviceId the remittance advice ID to be validated
   * @throws IllegalArgumentException if the remittance advice ID is empty
   */
  public void validateRemittanceAdviceId(String remittanceAdviceId){
    if (!StringUtils.hasText(remittanceAdviceId)) {
      throw new IllegalArgumentException(REMITTANCE_ADVICE_ID_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates the remittance advice type provided.
   *
   * @param remittanceAdviceType the remittance advice type to be validated
   * @throws IllegalArgumentException if the remittance advice type is empty or not in the correct format
   */
  public void validateRemittanceAdviceType(String remittanceAdviceType){
    //if it's empty
    if (!StringUtils.hasText(remittanceAdviceType)) {
      throw new IllegalArgumentException(REMITTANCE_ADVICE_TYPE_REQUIRED_MESSAGE);
    } else if (!remittanceAdviceType.equalsIgnoreCase(REMITTANCE_ADVICE_TYPE_PDF) && !remittanceAdviceType.equalsIgnoreCase(REMITTANCE_ADVICE_TYPE_835)) {
      throw new IllegalArgumentException(REMITTANCE_ADVICE_TYPE_FORMAT_MESSAGE);
    }
  }

  /**
   * Validates a FHIR Parameters resource for downloading a remittance advice.
   *
   * @param requestResource the FHIR Parameters resource to be validated
   */
  public void validateDownloadRemittanceAdviceRequest(Parameters requestResource) {
    if (requestResource.isEmpty()) {
      throw new IllegalArgumentException(REMITTANCE_ADVICE_REQUEST_PARAMETERS_EMPTY_MESSAGE);
    }
    // Validate required RemittanceAdviceIdentifier.
    if (requestResource.hasParameter(ApiConstants.REMITTANCE_ADVICE_IDENTIFIER)) {
      validateRemittanceAdviceId(requestResource.getParameter(ApiConstants.REMITTANCE_ADVICE_IDENTIFIER).getValue().toString());
    } else {
      throw new IllegalArgumentException(REMITTANCE_ADVICE_ID_REQUIRED_MESSAGE);
    }

    // Validate RemittanceAdviceType is present and it has the right value
    if (requestResource.hasParameter(ApiConstants.REMITTANCE_ADVICE_TYPE)) {
      String remittanceAdviceType =  requestResource.getParameter(ApiConstants.REMITTANCE_ADVICE_TYPE).getValue().toString();
      validateRemittanceAdviceType(remittanceAdviceType);
    }
  }

  /**
   * Validates following fields for SearchByClaimRequest endpoint:
   * - TIN
   * - Claim: Provider Claim ID
   * - Period: Date of Service Start
   * - Period: Date of Service End
   *
   * @throws IllegalArgumentException if any of the fields are invalid
   */
  public void validateSearchByClaimRequest(Parameters requestResource) throws IllegalArgumentException {
    if (requestResource == null || requestResource.isEmpty()) {
      throw new IllegalArgumentException(REQUEST_PARAMETERS_EMPTY_MESSAGE);
    }
    // Validate required TIN
    if (requestResource.hasParameter(ApiConstants.TIN)) {
      validateTin(requestResource.getParameter(ApiConstants.TIN).getValue().toString());
    } else {
      throw new IllegalArgumentException(TIN_REQUIRED_MESSAGE);
    }
    // Validate required ProviderClaimId from CLAIM
    if (requestResource.hasParameter(ApiConstants.CLAIM)) {
      requestResource.getParameter(ApiConstants.CLAIM).getPart().stream()
          .filter(part -> part.getName().equals(ApiConstants.PROVIDER_CLAIM_ID))
          .findAny()
          .ifPresentOrElse(component -> validateProviderClaimId(component.getValue().toString()),
              () -> {
                throw new IllegalArgumentException(PROVIDER_CLAIM_ID_REQUIRED_MESSAGE);
              });
    } else {
      throw new IllegalArgumentException(CLAIM_REQUIRED_MESSAGE);
    }

    // Validate Date of Service format and range
    if (requestResource.hasParameter(ApiConstants.DATE_OF_SERVICE)) {
      Period period = (Period) requestResource.getParameter(ApiConstants.DATE_OF_SERVICE).getValue();
      validateDateOfServiceFormat(period.getStartElement().asStringValue());
      validateDateOfServiceFormat(period.getEndElement().asStringValue());
      validatePeriod(period);
    }
  }

  /**
   * Validates following fields for the SearchByPatientRequest endpoint:
   * - TIN
   * - Patient: PatientID
   * - Patient: DateOfBirth
   *
   * @param requestResource the {@link Parameters} object to validate
   * @throws IllegalArgumentException if any of the fields are invalid
   */
  public void validateSearchByPatientRequest(Parameters requestResource) throws IllegalArgumentException {
    if (requestResource == null || requestResource.isEmpty()) {
      throw new IllegalArgumentException(REQUEST_PARAMETERS_EMPTY_MESSAGE);
    }
    // Validate required TIN
    if (requestResource.hasParameter(ApiConstants.TIN)) {
      validateTin(requestResource.getParameter(ApiConstants.TIN).getValue().toString());
    } else {
      throw new IllegalArgumentException(TIN_REQUIRED_MESSAGE);
    }
    // Validate required PatientID and DateOfBirth from Patient
    if (requestResource.hasParameter(ApiConstants.PATIENT)) {
      List<ParametersParameterComponent> patientParameter = requestResource.getParameter(ApiConstants.PATIENT)
          .getPart();
      Map<String, Type> patientObject = new HashMap<>();
      for (ParametersParameterComponent patientComponent : patientParameter) {
        patientObject.put(patientComponent.getName(), patientComponent.getValue());
      }
      // Required PatientID
      if (patientObject.containsKey(ApiConstants.PATIENT_ID)) {
        validatePatientId(new StringType().castToString(patientObject.get(ApiConstants.PATIENT_ID)).asStringValue());
      } else {
        throw new IllegalArgumentException(PATIENT_ID_REQUIRED_MESSAGE);
      }
      // Required DateOfBirth
      if (patientObject.containsKey(ApiConstants.DATE_OF_BIRTH)) {
        validateDateOfBirthFormat(
            new DateType().castToDate(patientObject.get(ApiConstants.DATE_OF_BIRTH)).asStringValue());
      } else {
        throw new IllegalArgumentException(DATE_OF_BIRTH_REQUIRED_MESSAGE);
      }
    } else {
      throw new IllegalArgumentException(PATIENT_REQUIRED_MESSAGE);
    }
  }

  /**
   * Validates following fields for the SearchByPaymentRequest endpoint:
   * - TIN
   *
   * @param requestResource the {@link Parameters} object to validate
   * @throws IllegalArgumentException if any of the fields are invalid
   */
  public void validateSearchByPaymentRequest(Parameters requestResource) throws IllegalArgumentException {
    if (requestResource == null || requestResource.isEmpty()) {
      throw new IllegalArgumentException(REQUEST_PARAMETERS_EMPTY_MESSAGE);
    }
    // Validate required TIN
    if (requestResource.hasParameter(ApiConstants.TIN)) {
      validateTin(requestResource.getParameter(ApiConstants.TIN).getValue().toString());
    } else {
      throw new IllegalArgumentException(TIN_REQUIRED_MESSAGE);
    }
    // Validate required PaymentIssueDate and PaymentNumber from Payment
    if (requestResource.hasParameter(ApiConstants.PAYMENT)) {
      List<ParametersParameterComponent> paymentParameter = requestResource.getParameter(ApiConstants.PAYMENT)
          .getPart();
      Map<String, Type> paymentObject = new HashMap<>();
      for (ParametersParameterComponent paymentComponent : paymentParameter) {
        paymentObject.put(paymentComponent.getName(), paymentComponent.getValue());
      }
      // Required PaymentIssueDate range
      if (paymentObject.containsKey(ApiConstants.PAYMENT_ISSUE_DATE)) {
        Period period = (Period) paymentObject.get(ApiConstants.PAYMENT_ISSUE_DATE);
        validateDateOfServiceFormat(period.getStartElement().asStringValue());
        validateDateOfServiceFormat(period.getEndElement().asStringValue());
        validatePeriod(period);
      } else {
        throw new IllegalArgumentException(PAYMENT_ISSUE_DATE_REQUIRED_MESSAGE);
      }
      // Required PaymentNumber
      if (paymentObject.containsKey(ApiConstants.PAYMENT_NUMBER)) {
        validatePaymentNumber(
            new StringType().castToString(paymentObject.get(ApiConstants.PAYMENT_NUMBER)).asStringValue());
      } else {
        throw new IllegalArgumentException(PAYMENT_NUMBER_REQUIRED_MESSAGE);
      }
    } else {
      throw new IllegalArgumentException(PAYMENT_REQUIRED_MESSAGE);
    }
  }
}
