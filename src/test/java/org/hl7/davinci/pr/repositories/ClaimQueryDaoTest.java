package org.hl7.davinci.pr.repositories;

import jakarta.persistence.Tuple;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import org.hibernate.Hibernate;
import org.hl7.davinci.pr.BaseTest;
import org.hl7.davinci.pr.domain.*;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class ClaimQueryDaoTest extends BaseTest {


    TestDataUtils testDataUtils;

    @Autowired
    ClaimQueryDao claimQueryDao;

    @Autowired
    ClaimQueryRepository claimQueryRepo;

    @Autowired
    PatientRepository patientRepo;

    @Autowired
    PayerRepository payerRepo;

    @Autowired
    PaymentRepository paymentRepo;

    @Autowired
    ProviderRepository providerRepo;

    @Autowired
    RemittanceRepository remittanceRepo;

    @BeforeEach
    void setupData(){
        testDataUtils = TestDataUtils.builder().claimQueryRepo(claimQueryRepo).patientRepo(patientRepo).paymentRepo(paymentRepo).remittanceRepo(remittanceRepo).providerRepo(providerRepo).payerRepo(payerRepo).build();
    }

    //payername not provided, but has payerID, still should return claim without adding where for payername
   @Test
    public void findCLaimsTest_noPayerName() {
        try {
            testDataUtils.allDataPopulated(true, true, false, true , true, true, true);

            Date startDos = TestDataUtils.dateFormatter.parse("2024-05-05");
            Date endDos = TestDataUtils.dateFormatter.parse("2024-07-05");

            List<Tuple> result = claimQueryDao.findByClaim(TestDataUtils.PROVIDER_TIN_1, TestDataUtils.PROVIDER_CLAIMID_1,
                    startDos, endDos, TestDataUtils.PATIENT_ID_1,
                    TestDataUtils.PAYER_ID_VAL_1, null, TestDataUtils.PAYER_CLAIM_ID_1, TestDataUtils.PROVIDER_NPI_1,
                    TestDataUtils.CLAIM_CHARGE_AMOUNT);
            Assertions.assertEquals(1, result.size());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findClaimsTest_allData() {
        try {
            testDataUtils.allDataPopulated(true, true, true, true , true, true, true);

            Date startDos = TestDataUtils.dateFormatter.parse("2024-05-05");
            Date endDos = TestDataUtils.dateFormatter.parse("2024-07-05");

            List<Tuple> result = claimQueryDao.findByClaim(TestDataUtils.PROVIDER_TIN_1, TestDataUtils.PROVIDER_CLAIMID_1,
                    startDos, endDos, TestDataUtils.PATIENT_ID_1,
                    TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1, TestDataUtils.PAYER_CLAIM_ID_1, TestDataUtils.PROVIDER_NPI_1,
                    TestDataUtils.CLAIM_CHARGE_AMOUNT);
            Assertions.assertEquals(1, result.size());

            int expectedTupleSize = 5;
            for (Tuple tuple : result) {
                Assertions.assertEquals(expectedTupleSize, tuple.getElements().size());
                ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
                Assertions.assertEquals(TestDataUtils.PROVIDER_TIN_1, claimQuery.getProviderTin());

                Assertions.assertTrue(claimQuery.getDateOfService().getTime() >= startDos.getTime() && claimQuery.getDateOfService().getTime() <= endDos.getTime());

                Assertions.assertEquals(Patient.class, tuple.get(1, Patient.class).getClass());
                Patient patient = tuple.get(1, Patient.class);
                Assertions.assertEquals(TestDataUtils.PATIENT_ID_1, claimQuery.getSubscriberPatientId());
                Assertions.assertEquals(TestDataUtils.CLAIM_CHARGE_AMOUNT, claimQuery.getClaimChargeAmount());
                Assertions.assertEquals(claimQuery.getPatient().getId(), patient.getId());

                Assertions.assertNotNull(tuple.get(2));
                Assertions.assertEquals(Payer.class, tuple.get(2, Payer.class).getClass());
                Payer payer = tuple.get(2, Payer.class);
                Assertions.assertEquals(TestDataUtils.PAYER_NAME_1, payer.getPayerName());
                Assertions.assertEquals(TestDataUtils.PAYER_ID_VAL_1, payer.getPayerIdentity());

                Assertions.assertNotNull(tuple.get(3));
                Assertions.assertEquals(Payment.class, tuple.get(3, Payment.class).getClass());
                Payment payment = tuple.get(3, Payment.class);
                Assertions.assertEquals(claimQuery.getId(), payment.getClaimQuery().getId());

                Assertions.assertNotNull(tuple.get(4));
                Assertions.assertEquals(Remittance.class, tuple.get(4, Remittance.class).getClass());
                Remittance remittance = tuple.get(4, Remittance.class);
                Assertions.assertEquals(claimQuery.getId(), remittance.getClaimQuery().getId());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findRemittanceTest_allData() {
        try {
            testDataUtils.allDataPopulated(true, true, true, true, true, true, true);

            Date startDos = TestDataUtils.dateFormatter.parse("2024-05-05");
            Date endDos = TestDataUtils.dateFormatter.parse("2024-07-05");

            List<Tuple> result = claimQueryDao.findByRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);

            Assertions.assertEquals(1, result.size());

            int expectedTupleSize = 5;
            for (Tuple tuple : result) {
                Assertions.assertEquals(expectedTupleSize, tuple.getElements().size());
                ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
                Assertions.assertEquals(TestDataUtils.PROVIDER_TIN_1, claimQuery.getProviderTin());
                Assertions.assertTrue(claimQuery.getDateOfService().getTime() >= startDos.getTime() && claimQuery.getDateOfService().getTime() <= endDos.getTime());

                Assertions.assertEquals(Patient.class, tuple.get(1, Patient.class).getClass());
                Patient patient = tuple.get(1, Patient.class);
                Assertions.assertEquals(TestDataUtils.PATIENT_ID_1, claimQuery.getSubscriberPatientId());
                Assertions.assertEquals(TestDataUtils.CLAIM_CHARGE_AMOUNT, claimQuery.getClaimChargeAmount());
                Assertions.assertEquals(claimQuery.getPatient().getId(), patient.getId());

                Assertions.assertNotNull(tuple.get(2));
                Assertions.assertEquals(Payer.class, tuple.get(2, Payer.class).getClass());
                Payer payer = tuple.get(2, Payer.class);
                Assertions.assertEquals(TestDataUtils.PAYER_NAME_1, payer.getPayerName());
                Assertions.assertEquals(TestDataUtils.PAYER_ID_VAL_1, payer.getPayerIdentity());

                Assertions.assertNotNull(tuple.get(3));
                Assertions.assertEquals(Payment.class, tuple.get(3, Payment.class).getClass());
                Payment payment = tuple.get(3, Payment.class);
                Assertions.assertEquals(claimQuery.getId(), payment.getClaimQuery().getId());

                Assertions.assertNotNull(tuple.get(4));
                Assertions.assertEquals(Remittance.class, tuple.get(4, Remittance.class).getClass());
                Remittance remittance = tuple.get(4, Remittance.class);
                Assertions.assertEquals(claimQuery.getId(), remittance.getClaimQuery().getId());
                Assertions.assertEquals(TestDataUtils.REMITTANCE_ADVICEID_1, remittance.getRemittanceAdviceId());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findByPaymentTest_allData() {
        try {
            testDataUtils.allDataPopulated(false, true, true, true, true, true, true);

            Date startDos = TestDataUtils.dateFormatter.parse("2024-05-05");
            Date endDos = TestDataUtils.dateFormatter.parse("2024-07-05");

            Date paymentIssueDtStart = TestDataUtils.dateFormatter.parse("2024-05-10");
            Date paymentIssueDtEnd = TestDataUtils.dateFormatter.parse("2024-07-05");

            String searchStrWithWildChar = TestDataUtils.PAYMENT_NUM_SEARCH_STR ;

            List<Tuple> result = this.claimQueryDao.findByPayment(TestDataUtils.PROVIDER_TIN_1, searchStrWithWildChar,
                    paymentIssueDtStart, paymentIssueDtEnd,
                    startDos, endDos, TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1,
                    TestDataUtils.PAYMENT_AMOUNT, TestDataUtils.PAYMENT_AMOUNT + 10);

            Assertions.assertEquals(1, result.size());

            int expectedTupleSize = 4;
            for (Tuple tuple : result) {
                Assertions.assertEquals(expectedTupleSize, tuple.getElements().size());
                ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
                Assertions.assertEquals(TestDataUtils.PROVIDER_TIN_1, claimQuery.getProviderTin());
                Assertions.assertTrue(claimQuery.getDateOfService().getTime() >= startDos.getTime() && claimQuery.getDateOfService().getTime() <= endDos.getTime());

                Assertions.assertNotNull(tuple.get(1));
                Assertions.assertEquals(Payer.class, tuple.get(1, Payer.class).getClass());
                Payer payer =  tuple.get(1, Payer.class);
                Assertions.assertEquals(TestDataUtils.PAYER_ID_VAL_1, payer.getPayerIdentity());
                Assertions.assertEquals(TestDataUtils.PAYER_NAME_1, payer.getPayerName());

                Assertions.assertNotNull(tuple.get(2));
                Assertions.assertEquals(Payment.class, tuple.get(2, Payment.class).getClass());
                Payment payment = tuple.get(2, Payment.class);
                Assertions.assertEquals(claimQuery.getId(), payment.getClaimQuery().getId());
                Assertions.assertTrue(payment.getPayment_issue_dt().getTime() >= paymentIssueDtStart.getTime() && payment.getPayment_issue_dt().getTime() <= paymentIssueDtEnd.getTime());
                Assertions.assertTrue(payment.getPaymentNumber().indexOf(TestDataUtils.PAYMENT_NUM_SEARCH_STR) > -1 );
                Assertions.assertTrue(payment.getAmount() >= TestDataUtils.PAYMENT_AMOUNT - 10 && payment.getAmount() <= TestDataUtils.PAYMENT_AMOUNT + 10);

                Assertions.assertNotNull(tuple.get(3));
                Assertions.assertEquals(Remittance.class, tuple.get(3, Remittance.class).getClass());
                Remittance remittance = tuple.get(3, Remittance.class);
                Assertions.assertEquals(claimQuery.getId(), remittance.getClaimQuery().getId());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findPatientsTest_allData() {
        try {
            testDataUtils.allDataPopulated(true, true, true,true , true, true, true);

            Date startDos = TestDataUtils.dateFormatter.parse("2024-05-05");
            Date endDos = TestDataUtils.dateFormatter.parse("2024-07-05");
            Date expectedPatDob = TestDataUtils.dateFormatter.parse(TestDataUtils.PATIENT_DOB);
            List<Tuple> result = this.claimQueryDao.findByPatient(TestDataUtils.PROVIDER_TIN_1, expectedPatDob, startDos, endDos, TestDataUtils.PATIENT_ID_1, TestDataUtils.PAYER_ID_VAL_1, TestDataUtils.PAYER_NAME_1, TestDataUtils.PATIENT_FIRST_NAME_1, TestDataUtils.PATIENT_LAST_NAME_1);

            Assertions.assertEquals(1, result.size());


            int expectedTupleSize = 5;
            for (Tuple tuple : result) {
                Assertions.assertEquals(expectedTupleSize, tuple.getElements().size());
                ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
                Assertions.assertEquals(TestDataUtils.PROVIDER_TIN_1, claimQuery.getProviderTin());
                Assertions.assertTrue(claimQuery.getDateOfService().getTime() >= startDos.getTime() && claimQuery.getDateOfService().getTime() <= endDos.getTime());

                Assertions.assertEquals(Patient.class, tuple.get(1, Patient.class).getClass());
                Patient patient = tuple.get(1, Patient.class);
                Hibernate.initialize(patient);
                Assertions.assertEquals(TestDataUtils.PATIENT_ID_1, claimQuery.getSubscriberPatientId());
                Assertions.assertEquals(claimQuery.getPatient().getId(), patient.getId());
                Assertions.assertEquals(expectedPatDob, claimQuery.getPatient().getDateOfBirth());
                Assertions.assertEquals(expectedPatDob, patient.getDateOfBirth());

                Assertions.assertEquals(Payer.class, tuple.get(2, Payer.class).getClass());

                Assertions.assertEquals(Payment.class, tuple.get(3, Payment.class).getClass());
                Payment payment = tuple.get(3, Payment.class);
                Assertions.assertEquals(claimQuery.getId(), payment.getClaimQuery().getId());

                Assertions.assertNotNull(tuple.get(4));
                Assertions.assertEquals(Remittance.class, tuple.get(4, Remittance.class).getClass());
                Remittance remittance = tuple.get(4, Remittance.class);
                Assertions.assertEquals(claimQuery.getId(), remittance.getClaimQuery().getId());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
