package org.hl7.davinci.pr.service;

import com.imsweb.x12.LineBreak;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hl7.davinci.pr.BaseTest;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.domain.ClaimQuery;
import org.hl7.davinci.pr.domain.Payer;
import org.hl7.davinci.pr.domain.Payment;
import org.hl7.davinci.pr.repositories.*;
import org.hl7.davinci.pr.utils.TestDataUtils;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.DocumentReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
    @Autowired
    ClaimQueryDao claimQueryDao;
    private TestDataUtils testDataUtils;

    @BeforeEach
    void setupData(){
        testDataUtils = TestDataUtils.builder().claimQueryRepo(claimQueryRepo).patientRepo(patientRepo).paymentRepo(paymentRepo).remittanceRepo(remittanceRepo).providerRepo(providerRepo).payerRepo(payerRepo).build();
    }

    @Test
    public void downloadDocumentTest_pdf() {
        try {
            testDataUtils.allDataPopulated(true, true, true, true, true, true, true);
            DocumentReference docReference = downloadService.downloadDocument(
                    TestDataUtils.REMITTANCE_ADVICEID_1, ApiConstants.REMITTANCE_ADVICE_TYPE_PDF);
           Assertions.assertTrue(docReference.getContent().size() >0);
           Assertions.assertTrue(docReference.getContent().get(0).getAttachment() != null);
           Attachment attachment = docReference.getContent().get(0).getAttachment();
           Assertions.assertEquals("application/zip", attachment.getContentType());
           Assertions.assertTrue(attachment.getDataElement() != null);
           // log.debug("DocumentReference: \n" + FhirUtils.convertToJSON(docReference));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void downloadDocumentTest_835(){
        try {
            testDataUtils.allDataPopulated(true, true, true, true, true, true, true);
            DocumentReference docReference = downloadService.downloadDocument(
                    TestDataUtils.REMITTANCE_ADVICEID_1, ApiConstants.REMITTANCE_ADVICE_TYPE_835);
            Assertions.assertTrue(docReference.getContent().size() >0);
            Assertions.assertNotNull(docReference.getContent().get(0).getAttachment());
            Attachment attachment = docReference.getContent().get(0).getAttachment();
            Assertions.assertEquals("application/zip", attachment.getContentType());
            Assertions.assertNotNull(attachment.getDataElement());
          //  log.debug("DocumentReference: \n" + FhirUtils.convertToJSON(docReference));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void build835Text_test() {
        try {
            testDataUtils.allDataPopulated(true, true, true, true, true, true, true);
            List<Tuple> results= claimQueryDao.findByRemittance(TestDataUtils.REMITTANCE_ADVICEID_1);
            Tuple firstTuple = results.get(0);
            ClaimQuery claimQueryFirst = firstTuple.get(0, ClaimQuery.class);
            Payer payerFirst = firstTuple.get(2, Payer.class);
            Payment paymentFirst = firstTuple.get(3, Payment.class);

            String x12Str = downloadService.build835Text(paymentFirst, payerFirst, claimQueryFirst, results);
            log.debug("x12Str: \n" + x12Str);

            String expected = IOUtils
                    .toString(this.getClass().getResourceAsStream("/835-sample.txt"), StandardCharsets.UTF_8).replace(LineBreak.LF.getLineBreakString(), LineBreak.CRLF.getLineBreakString())
                    .trim();
            log.debug("expected: \n" + expected);
            Assertions.assertEquals(expected, x12Str);

            //log.debug("DocumentReference: \n" + FhirUtils.convertToJSON(docReference));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
