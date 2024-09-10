package org.hl7.davinci.pr.api.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class DataConstants {

    public static final String ZERO_DOLLARS = "$0.00";
    public static final String FLOAT_STRING_FORMAT_PDF = "$%.2f";
    public static final String FLOAT_STRING_FORMAT_TXT = "%.2f";
    public static final String PDF_LABEL_CPT = "CPT";
    public static final String PDF_LABEL_UNITS = "Units";
    public static final String PDF_LABEL_BILLED = "Billed";
    public static final String PDF_LABEL_ALLOW = "Allow";
    public static final String PDF_LABEL_PAY = "Pay";
    public static final String PDF_LABEL_DEDUCT = "Deduct";
    public static final String PDF_LABEL_COINS = "Coins";
    public static final String PDF_LABEL_COPAY = "Copay";
    public static final String PDF_LABEL_OTHER_PR = "Other PR";
    public static final String PDF_EMPTY = "";
    public static final String PDF_LABEL_REAS_RMK = "Reas/Rmk";
    public static final String PDF_LABEL_SERVICE_DATES = "Service Dates";
    public static final String PDF_LABEL_CONTR = "Contr";
    public static final String PDF_LABEL_W_HOLD = "WHold";
    public static final String PDF_LABEL_GLOBAL = "Global";
    public static final String PDF_LABEL_CAP = "Cap";
    public static final String PDF_LABEL_OTH_CO = "Oth CO";
    public static final String PDF_LABEL_DENIED = "Denied";
    public static final String PDF_LABEL_INCENT = "Incent";
    public static final String CLAIM_CPT_VALUE1 = "93280";
    public static final String CLAIM_CPT_UNITS_VALUE = "1";
    public static final String CLAIM_RMK_VALUE = "CO22";
    public static final String CLAIM_CPT_VALUE2 = "90651,SL";
    public static final String CLAIM_CO22_EXPLANATION = "This care may be covered by another payer per coordination of benefits. ";
    public static final String CLAIM_LBL_TOTAL_PAYMENTS = "Total Payments on this EOB: ";
    public static final String CONTENT_TYPE_ZIP = "application/zip";
    public static final String PDF_LABEL_PAYER = "Payer";
    public static final String PDF_LABEL_PAYEE = "Payee";
    public static final String PDF_LABEL_GROUP_NUMBER = "Group Number: ";
    public static final String CLAIM_UNKNOWN_VAL = "Unknown";
    public static final String PDF_LABEL_CHECK_NUMBER = "Check Number: ";
    public static final String PDF_LABEL_SUMMARY_OF_BENEFITS = "SUMMARY OF BENEFITS";
    public static final String PDF_EOB_FILE_NAME = "EOB-sample.pdf";
    public static final String PDF_LABEL_PATIENT = "Patient: ";
    public static final String PDF_LABEL_PATIENT_ID = "Patient ID: ";
    public static final String PDF_LABEL_PAID = "Paid: ";
    public static final String PDF_LABEL_PROVIDER = "Provider: ";
    public static final String PDF_LABEL_OTHER_PROVIDER = "Other Provider #: ";
    public static final String PDF_LABEL_PROVIDER_NPI = "Provider NPI: ";
    public static final String CLAIM_NOT_AVAILABLE_VALUE = "Not Available";
    public static final String PDF_LABEL_PATIENT_ACCOUNT = "Patient Account #: ";
    public static final String PDF_LABEL_PAYER_CLAIM = "Payer Claim #: ";
    public static final String PDF_LABEL_PAY_DATE = "Pay Date: ";
    public static final String FAKE_PAYER_ADDRESS = "PO BOX 1106 \n LEXINGTON KY, 405120000";
    public static final String FAKE_PAYER_ADDRESS_POBOX = "PO BOX 1106";
    public static final String FAKE_PAYER_ADDRESS_CITY = "LEXINGTON";
    public static final String FAKE_PAYER_ADDRESS_STATE = "KY";
    public static final String FAKE_PAYER_ADDRESS_ZIPCODE = "405120000";
    public static final String FAKE_PROVIDER_NAME_WITH_NPI = "Local clinic - (%s)";
    public static final String FAKE_PROVIDER_NAME = "Local clinic LLC";
    public static final String FAKE_PROVIDER_ADDRESS = "PO BOX 15262\nBELFAST ME, 049154047";
    public static final String FAKE_PROVIDER_ADDRESS_POBOX = "PO BOX 18001";
    public static final String FAKE_PROVIDER_ADDRESS_CITY = "BELFAST";
    public static final String FAKE_PROVIDER_ADDRESS_STATE = "ME";
    public static final String FAKE_PROVIDER_ADDRESS_ZIPCODE = "049150000";

    public final static SimpleDateFormat dateFormatter835 = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
 }
