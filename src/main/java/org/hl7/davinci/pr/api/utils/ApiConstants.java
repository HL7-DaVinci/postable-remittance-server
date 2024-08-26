package org.hl7.davinci.pr.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiConstants {

  // Open API Constants
  public static final String OPEN_API_TAG_SERVICE_NAME = "Postable Remittance Reference Implementation Service";
  public static final String OPEN_API_TAG_SEARCH_BY_CLAIM_ENDPOINT = "Search By Claim";
  public static final String OPEN_API_DESCRIPTION_SEARCH_BY_CLAIM_ENDPOINT = "This operation is used to search for a postable remittance by providing claim information.";
  public static final String OPEN_API_TAG_SEARCH_BY_PATIENT_ENDPOINT = "Search By Patient";
  public static final String OPEN_API_DESCRIPTION_SEARCH_BY_PATIENT_ENDPOINT = "This operation is used to search for a postable remittance by providing patient information.";
  public static final String OPEN_API_TAG_SEARCH_BY_PAYMENT_ENDPOINT = "Search By Payment";
  public static final String OPEN_API_DESCRIPTION_SEARCH_BY_PAYMENT_ENDPOINT = "This operation is used to search for a postable remittance by providing payment information.";
  public static final String OPEN_API_DOWNLOAD_REMITTANCE_ENDPOINT = "Download Remittance Advice";
  public static final String OPEN_API_DESCRIPTION_DOWNLOAD_REMITTANCE_ENDPOINT = "This operation is used to download a previously sent postable remittance.";

  // Controller constants
  public static final String CONTENT_TYPE_APPLICATION_FHIR_JSON = "application/fhir+json; charset=utf-8";
  public static final String SEARCH_BY_CLAIM_PATIENT_RESPONSE_META_PROFILE_URL = "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/searchResultParameters";
  public static final String SEARCH_RESPONSE_RESOURCE_ID = "SearchResult";
  public static final String SEARCH_BY_PAYMENT_RESPONSE_META_PROFILE_URL = "http://hl7.org/fhir/us/davinci-pr/StructureDefinition/searchByPaymentResultParameters";

  // SearchByClaim Request Constants
  public static final String TIN = "TIN";
  public static final String CLAIM = "Claim";
  public static final String PROVIDER_CLAIM_ID = "ProviderClaimID";
  public static final String PROVIDER_ID = "ProviderID";
  public static final String PAYER_CLAIM_ID = "PayerClaimID";
  public static final String CLAIM_CHARGE_AMOUNT = "ClaimChargeAmount";
  public static final String DATE_OF_SERVICE = "DateOfService";
  public static final String PATIENT_ID = "PatientID";
  public static final String PAYER_ID = "PayerID";
  public static final String PAYER_NAME = "PayerName";

  // SearchByClaim Response Constants
  public static final String PAYER = "Payer";
  public static final String CLAIM_RECEIVED_DATE = "ClaimReceivedDate";

  public static final String PATIENT = "Patient";
  public static final String DATE_OF_BIRTH = "DateOfBirth";
  public static final String PATIENT_FIRST_NAME = "PatientFirstName";
  public static final String PATIENT_LAST_NAME = "PatientLastName";

  public static final String PAYMENT = "Payment";
  public static final String PAYMENT_INFO = "PaymentInfo";
  public static final String PAYMENT_DATE = "PaymentDate";
  public static final String PAYMENT_NUMBER = "PaymentNumber";
  public static final String PAYMENT_AMOUNT = "PaymentAmount";
  public static final String PAYMENT_CURRENCY = "USD";
  public static final String PAYMENT_ISSUE_DATE = "PaymentIssueDate";
  public static final String PAYMENT_AMOUNT_LOW = "PaymentAmountLow";
  public static final String PAYMENT_AMOUNT_HIGH = "PaymentAmountHigh";

  public static final String REMITTANCE = "Remittance";
  public static final String REMITTANCE_ADVICE_IDENTIFIER = "RemittanceAdviceIdentifier";
  public static final String REMITTANCE_ADVICE_TYPE = "RemittanceAdviceType";
  public static final String REMITTANCE_ADVICE_DATE = "RemittanceAdviceDate";
  public static final String REMITTANCE_ADVICE_FILE_SIZE = "RemittanceAdviceFileSize";

  public static final String REMITTANCE_ADVICE_TYPE_PDF = "PDF";
  public static final String REMITTANCE_ADVICE_TYPE_835 = "835";
}
