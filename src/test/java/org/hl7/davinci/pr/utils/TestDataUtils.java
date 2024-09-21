package org.hl7.davinci.pr.utils;

import static org.hl7.davinci.pr.api.utils.ApiConstants.REMITTANCE_ADVICE_TYPE_PDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiFunction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.domain.Provider;
import org.hl7.davinci.pr.domain.Remittance;
import org.hl7.davinci.pr.repositories.ClaimQueryRepository;
import org.hl7.davinci.pr.repositories.PatientRepository;
import org.hl7.davinci.pr.repositories.PayerRepository;
import org.hl7.davinci.pr.repositories.PaymentRepository;
import org.hl7.davinci.pr.repositories.ProviderRepository;
import org.hl7.davinci.pr.repositories.RemittanceRepository;


@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDataUtils {

    public static final String PROVIDER_TIN_1 = "123456789";
    public static final String PROVIDER_CLAIMID_1 = "provider_claimid_1";
    public static final String PATIENT_ID_1 = "subscriber_patient_id_1";
    public static final String PAYER_CLAIM_ID_1 = "payer_claim_id_1";
    public static final String PROVIDER_NPI_1 = "provider_npi_1";
    public static final String PATIENT_FIRST_NAME_1 = "PatientName1";
    public static final String PATIENT_LAST_NAME_1 = "PatientLastName1";
    public static final String REMITTANCE_ADVICEID_1 = "rem_adviceid_1";
    public static final String DATE_OF_SERVICE = "2024-05-05";
    public static final String DATE_OF_SERVICE_END = "2024-05-06";
    public static final String PATIENT_DOB = "1980-05-05";
    public static final String PAYER_ID_VAL_1 = "Payer_ID_VAL_1";
    public static final String PAYER_NAME_1 = "PAYER_NAME_1";
    public static final float CLAIM_CHARGE_AMOUNT = 100.00F;
    public static final String PAYMENT_ISSUE_DATE = "2024-05-10";
    public static final String PAYMENT_ISSUE_DATE_END = "2024-05-11";
    public static final float PAYMENT_AMOUNT = 100.00F;
    public static final float PAYMENT_AMOUNT_HIGH = 120.00F;
    public static final String PAYMENT_NUM_SEARCH_STR = "PAYMENT_N";
    public static final String PAYM_NUM_1 = "PAYMENT_NUMBER_1";
    public static final int REMITTANCE_ADVICE_FILE_SIZE = 123;
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private ClaimQueryRepository claimQueryRepo;

    private PatientRepository patientRepo;

    private PayerRepository payerRepo;

    private PaymentRepository paymentRepo;

    private ProviderRepository providerRepo;

    private RemittanceRepository remittanceRepo;


    public void allDataPopulated(boolean hasPatient, boolean hasProvider, boolean hasPayerName,
                                 boolean hasPayerIdentity, boolean hasPayment,
                                 boolean hasDates) throws ParseException {
        //create data
        Patient patient;
        Date dob = dateFormatter.parse(PATIENT_DOB);
        if (hasPatient) {
            patient = Patient.builder().id(1).firstName(PATIENT_FIRST_NAME_1).lastName(PATIENT_LAST_NAME_1).dateOfBirth(dob).build();
            patientRepo.save(patient);
        } else {
            patient = null;
        }

        Provider provider;
        if (hasProvider) {
            provider = Provider.builder().id(1).providerNPI(PROVIDER_NPI_1).tin(PROVIDER_TIN_1).build();
            providerRepo.save(provider);
        } else {
            provider = null;
        }

        Payer payer;
        Payer.PayerBuilder pb = Payer.builder();
        if (hasPayerName || hasPayerIdentity) {
            pb = pb.id(1);
            if (hasPayerName) {
                pb = pb.payerName(PAYER_NAME_1);
            }
            if (hasPayerIdentity) {
                pb = pb.payerIdentity(PAYER_ID_VAL_1);
            }
            payer = pb.build();
            payerRepo.save(payer);
        } else {
            payer = null;
        }

        //add optional parameters and references if they are present for claim query
        BiFunction<Boolean, ClaimQuery.ClaimQueryBuilder, ClaimQuery.ClaimQueryBuilder> hasPatientBuilder =
                (hasPatientValue, clbInternal) -> hasPatientValue ? clbInternal.patient(patient) : clbInternal;
        BiFunction<Boolean, ClaimQuery.ClaimQueryBuilder, ClaimQuery.ClaimQueryBuilder> hasProviderBuilder =
                (hasProvVal, clbInternal) -> hasProvider ? clbInternal.provider(provider) : clbInternal;
        BiFunction<Boolean, ClaimQuery.ClaimQueryBuilder, ClaimQuery.ClaimQueryBuilder> hasPayerBuilder =
                (hasPayerVal, clbInternal) -> hasPayerVal ? clbInternal.payer(payer) : clbInternal;
        Date dateOfService = dateFormatter.parse(DATE_OF_SERVICE);
        BiFunction<Boolean, ClaimQuery.ClaimQueryBuilder, ClaimQuery.ClaimQueryBuilder> hasDateBuilder = (hasDatesVal, clbInternal) -> hasDatesVal ?
                clbInternal.dateOfService(dateOfService) : clbInternal;
        BiFunction<Boolean, ClaimQuery.ClaimQueryBuilder, ClaimQuery.ClaimQueryBuilder> hasReceivedDateBuilder = (hasDatesVal, clbInternal) ->
                hasDatesVal ? clbInternal.receivedDate(dateOfService) : clbInternal;

        ClaimQuery.ClaimQueryBuilder clb = ClaimQuery.builder().id(1);
        clb = hasPatientBuilder.apply(hasPatient, clb);
        clb = hasProviderBuilder.apply(hasProvider, clb);
        clb = hasPayerBuilder.apply(hasPayerName || hasPayerIdentity, clb);
        clb = hasDateBuilder.apply(hasDates, clb);
        clb = hasReceivedDateBuilder.apply(hasDates, clb);

        //populate claim query direct fields
        ClaimQuery claimQuery = clb.payerClaimId(PAYER_CLAIM_ID_1).
                providerClaimID(PROVIDER_CLAIMID_1).providerNPI(PROVIDER_NPI_1).providerTin(PROVIDER_TIN_1).
                subscriberPatientId(PATIENT_ID_1).claimChargeAmount(CLAIM_CHARGE_AMOUNT).
                dateOfService(dateOfService).build();
        claimQueryRepo.save(claimQuery);


        Remittance remittance = Remittance.builder()
                .id(1)
                .claimQuery(claimQuery)
                .remittanceAdviceId(REMITTANCE_ADVICEID_1)
                .remittanceAdviceType(REMITTANCE_ADVICE_TYPE_PDF)
                .remittanceAdviceDate(new Date())
                .remittanceAdviceFileSize(REMITTANCE_ADVICE_FILE_SIZE)
                .build();
        remittanceRepo.save(remittance);


        //populate Payment and Remittance that refer to the claimQuery
        Date paymentIssueDt = dateFormatter.parse(PAYMENT_ISSUE_DATE);
        Payment.PaymentBuilder paymentBuilder = Payment.builder().id(1).payment_issue_dt(paymentIssueDt).
                paymentNumber(PAYM_NUM_1).claimQuery(claimQuery).remittance(remittance);
        if (hasPayment) {
            paymentBuilder.amount(PAYMENT_AMOUNT);
        }
        Payment payment = paymentBuilder.build();
        paymentRepo.save(payment);


    }
}
