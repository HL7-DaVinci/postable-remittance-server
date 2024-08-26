package org.hl7.davinci.pr.service;

import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.BaseTest;
import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.repositories.*;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.fhir.r4.model.DocumentReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DownloadServiceTest extends BaseTest {

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

    @Autowired
    DownloadService downloadService;
    private TestDataUtils testDataUtils;

    @BeforeEach
    void setupData(){
        testDataUtils = TestDataUtils.builder().claimQueryRepo(claimQueryRepo).patientRepo(patientRepo).paymentRepo(paymentRepo).remittanceRepo(remittanceRepo).providerRepo(providerRepo).payerRepo(payerRepo).build();
    }

    @Test
    public void downloadDocumentTest() {
        try {
            testDataUtils.allDataPopulated(true, true, true, true, true, true, true);
            DocumentReference docReference = downloadService.downloadDocument(TestDataUtils.REMITTANCE_ADVICEID_1,
                    "PDF");
           // docReference.getContent();
            log.debug("DocumentReference: \n" + FhirUtils.convertToJSON(docReference));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
