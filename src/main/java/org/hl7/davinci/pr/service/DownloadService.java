package org.hl7.davinci.pr.service;

import com.imsweb.x12.LineBreak;
import com.imsweb.x12.Loop;
import com.imsweb.x12.Segment;
import com.imsweb.x12.Separators;
import com.imsweb.x12.reader.X12Reader;
import com.imsweb.x12.writer.X12Writer;
import com.itextpdf.text.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.pr.api.utils.ApiConstants;
import org.hl7.davinci.pr.api.utils.ApiUtils;
import org.hl7.davinci.pr.api.utils.DataConstants;
import org.hl7.davinci.pr.api.utils.FhirUtils;
import org.hl7.davinci.pr.domain.*;
import org.hl7.davinci.pr.domain.Patient;
import org.hl7.davinci.pr.repositories.ClaimQueryDao;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class DownloadService {

    @Autowired
    ClaimQueryDao claimQueryDao;
    private final static Font normalFontBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private final static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    private final static Font grayFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.LIGHT_GRAY);

    public DocumentReference downloadDocument(String remittanceAdviceId, String remittanceType) throws DocumentException, IOException {

        List<Tuple> searchResults = claimQueryDao.findByRemittance(remittanceAdviceId);
        if (searchResults == null || searchResults.size() == 0) {
            return null;
        }
        DocumentReference documentReference = new DocumentReference();
        documentReference.setId("remittance-document-" + FhirUtils.generateUniqueResourceID());
        documentReference.setMeta(FhirUtils.generateResourceMeta("http://hl7.org/fhir/us/davinci-pr/StructureDefinition/remittanceAdviceDocument"));
        documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);


        /*the order of tuples is:
          {@link ClaimQuery}, {@link org.hl7.davinci.pr.domain.Patient}, {@link org.hl7.davinci.pr.domain.Payer}, {@link Payment}, {@link org.hl7.davinci.pr.domain.Remittance}*/
        Tuple firstTuple = searchResults.get(0);
        ClaimQuery claimQueryFirst = firstTuple.get(0, ClaimQuery.class);
        Payer payerFirst = firstTuple.get(2, Payer.class);
        Payment paymentFirst = firstTuple.get(3, Payment.class);

        //build pdf by default
        ByteArrayOutputStream baos = null;
        if (remittanceType == null || remittanceType.equals(ApiConstants.REMITTANCE_ADVICE_TYPE_PDF)) {
            baos = buildPdf(payerFirst, claimQueryFirst, paymentFirst, searchResults);
        } else {
            String writerResult = build835Text(paymentFirst, payerFirst, claimQueryFirst, searchResults);
            //create zip file, convert to Base64 encoded string
            byte[] uncompressedByteArray = writerResult.getBytes(); //uncompressed
            baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry out = new ZipEntry("835-sample.txt");
                zos.putNextEntry(out);
                zos.write(uncompressedByteArray, 0, uncompressedByteArray.length);
                zos.closeEntry();
            }

        }
        DocumentReference.DocumentReferenceContentComponent content = documentReference.addContent();
        Base64BinaryType base64BinaryType = new Base64BinaryType();
        if (baos != null) {
            byte[] compressedByArray = baos.toByteArray();
            String encodedBase64String = Base64.getEncoder().encodeToString(compressedByArray);
            log.debug("------encoded sting: \n" + encodedBase64String);
            base64BinaryType = new Base64BinaryType();
            base64BinaryType.setValueAsString(encodedBase64String);
        }

        Attachment attachment = new Attachment();
        attachment.setContentType(DataConstants.CONTENT_TYPE_ZIP);
        attachment.setDataElement(base64BinaryType);
        content.setAttachment(attachment);
        return documentReference;
    }

    String build835Text(Payment paymentFirst, Payer payerFirst, ClaimQuery claimQueryFirst, List<Tuple> resultTuples) {
        LineBreak lineBreak = LineBreak.CRLF;
        Separators separators = new Separators();
        separators.setLineBreak(lineBreak);

        Loop isaLoop = new Loop(separators, "ISA_LOOP");

        Segment segment = new Segment("ISA");
        segment.addElement("01", "00");
        segment.addElement("02", "          ");
        segment.addElement("03", "00");
        segment.addElement("04", "          ");
        segment.addElement("05", "ZZ");
        //          "SUBMITTERS.ID  "
        segment.addElement("06", "200787505      ");
        segment.addElement("07", "ZZ");
        segment.addElement("08", "450525148      ");
        segment.addElement("09", "240826");
        segment.addElement("10", "0912");
        segment.addElement("11", "^");
        segment.addElement("12", "00501");
        segment.addElement("13", "101925142");
        segment.addElement("14", "0");
        segment.addElement("15", "P");
        segment.addElement("16", ":");
        isaLoop.addSegment(segment);

        Loop gsLoop = new Loop(separators, "GS_LOOP");
        segment = new Segment("GS");
        addElement(segment, "01", "HP");
        addElement(segment, "02", "200787505");
        addElement(segment, "03", "450525148");
        addElement(segment, "04", "20240826");
        addElement(segment, "05", "0912");
        addElement(segment, "06", "101777856");
        addElement(segment, "07", "X");
        addElement(segment, "08", "005010X221A1");
        //isaLoop.addSegment(segment);
        gsLoop.addSegment(segment);
        isaLoop.getLoops().add(gsLoop);

        //ST loop
        Loop stLoop = new Loop(separators, "ST_LOOP");
        stLoop.addSegment(buildSegment(List.of("835", "0001"), new Segment("ST")));
        gsLoop.getLoops().add(stLoop);

        Loop headerLoop = new Loop(separators, "HEADER");
        stLoop.getLoops().add(headerLoop);
        Segment segmentBpr = new Segment("BPR");
        addElement(segmentBpr, "01", "I");
        addElement(segmentBpr, "02", String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, paymentFirst.getAmount()));
        addElement(segmentBpr, "03", "C");
        addElement(segmentBpr, "04", "ACH");
        addElement(segmentBpr, "05", "CCP");
        //Depository Financial Institution (DFI) Identification Number Qualifier
        addElement(segmentBpr, "06", "01");
        //Sender DFI Identifier
        addElement(segmentBpr, "07", "123000848");
        //Account Number Qualifier
        addElement(segmentBpr, "08", "DA");
        //Sender Bank Account Number
        addElement(segmentBpr, "09", "1700020064");
        //Payer Identifier
        addElement(segmentBpr, "10", payerFirst.getPayerIdentity());
        //Originating Company Supplemental Code
        addElement(segmentBpr, "11", "");
        //Dfi Id Number Qualifier 2-ABA transit routing number incl. check digits
        addElement(segmentBpr, "12", "01");
        //Receiver Or Provider Bank Id Number
        addElement(segmentBpr, "13", "123000848");
        //Account Number Qualifier
        addElement(segmentBpr, "14", "DA");
        //Receiver or Provider Account Number
        addElement(segmentBpr, "15", "153911610365");
        //check issue date
        addElement(segmentBpr, "16", "20240816");
        // txSetHeaderLoop.addSegment(segmentBpr);
        headerLoop.addSegment(segmentBpr);

        //TRN Segment part of the same Header loop
        headerLoop.addSegment(buildSegment(List.of("1", "9155284235", payerFirst.getPayerIdentity()), new Segment("TRN")));

        //Ref segment
        headerLoop.addSegment(buildSegment(List.of("EV", "450525148"), new Segment("REF")));

        //DTM segment
        headerLoop.addSegment(buildSegment(List.of("405", DataConstants.dateFormatter835.format(claimQueryFirst.getDateOfService())), new Segment("DTM")));

        //new loop 1000A
        Loop payerIdentLoop = new Loop(separators, "1000A");
        headerLoop.getLoops().add(payerIdentLoop);
        payerIdentLoop.addSegment(buildSegment(List.of("PR", payerFirst.getPayerName()), new Segment("N1")));

        //n3 segment - payer po box
        payerIdentLoop.addSegment(buildSegment(List.of(DataConstants.FAKE_PAYER_ADDRESS_POBOX), new Segment("N3")));

        //n4 - payer city, state, zipcode
        payerIdentLoop.addSegment(
                buildSegment(List.of(DataConstants.FAKE_PAYER_ADDRESS_CITY,
                        DataConstants.FAKE_PAYER_ADDRESS_STATE,
                        DataConstants.FAKE_PAYER_ADDRESS_ZIPCODE), new Segment("N4")));

        //ref 1
        payerIdentLoop.addSegment(buildSegment(List.of("2U", "TP021"), new Segment("REF")));

        //ref 2
        payerIdentLoop.addSegment(buildSegment(List.of("EO", "363917295"), new Segment("REF")));

        //per1
        payerIdentLoop.addSegment(buildSegment(List.of("CX", payerFirst.getPayerName(), "TE", "8008007885"), new Segment("PER")));

        //per2
        payerIdentLoop.addSegment(buildSegment(List.of("BL", payerFirst.getPayerName(), "TE", "8008007885"), new Segment("PER")));

        //new loop1000B
        Loop payeeIdentifierLoop = new Loop(separators, "1000B");
        headerLoop.getLoops().add(payeeIdentifierLoop);

        //payee N1 - Name, NPI
        payeeIdentifierLoop.addSegment(buildSegment(List.of("PE", DataConstants.FAKE_PROVIDER_NAME, "XX", claimQueryFirst.getProviderNPI()), new Segment("N1")));

        //payee n3 - PO Box
        payeeIdentifierLoop.addSegment(buildSegment(List.of(DataConstants.FAKE_PROVIDER_ADDRESS_POBOX), new Segment("N3")));

        //payee n4 - city, state, zipcode
        payeeIdentifierLoop.addSegment(
                buildSegment(Arrays.asList(DataConstants.FAKE_PROVIDER_ADDRESS_CITY,
                        DataConstants.FAKE_PROVIDER_ADDRESS_STATE,
                        DataConstants.FAKE_PROVIDER_ADDRESS_ZIPCODE), new Segment("N4")));

        //payee ref1- additional payee id
        payeeIdentifierLoop.addSegment(buildSegment(Arrays.asList("TJ", "123456789"), new Segment("REF")));

        //payee ref2- state license num
        payeeIdentifierLoop.addSegment(buildSegment(List.of("0B", "OS12165"), new Segment("REF")));

        //loop to hold claims
        Loop detailsLoop = new Loop(separators, "DETAIL");
        stLoop.getLoops().add(detailsLoop);

        Loop detailsLoop2000 = new Loop(separators, "2000");
        detailsLoop.getLoops().add(detailsLoop2000);

        detailsLoop2000.addSegment(buildSegment(List.of("1"), new Segment("LX")));

        //repeat for all data results, per claim
        float paymentHalf = 0F;
        float totalPaymentAmount = 0F;
        for (Tuple tuple : resultTuples) {

            ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
            Patient patient = tuple.get(1, Patient.class);
            Payer payer = tuple.get(2, Payer.class);
            Payment payment = tuple.get(3, Payment.class);
            totalPaymentAmount += payment.getAmount();
            Remittance remittance = tuple.get(4, Remittance.class);
            paymentHalf = payment.getAmount() / resultTuples.size();

            //build claim
            Loop claimInfoLoop2100 = new Loop(separators, "2100");
            detailsLoop2000.getLoops().add(claimInfoLoop2100);

            //CLP claim segment
            claimInfoLoop2100.addSegment(buildSegment(List.of(claimQuery.getProviderClaimID(), "1", String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, claimQuery.getClaimChargeAmount()),
                    String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, paymentHalf),
                    "", "WC", claimQuery.getPayerClaimId(), "11", "1"), new Segment("CLP")));

            //NM1 segment - patient info
            claimInfoLoop2100.addSegment(buildSegment(List.of("QC", "1", patient.getLastName(), patient.getFirstName(), "", "", "", "34", "999999999"), new Segment("NM1")));

            //NM1 segment - service provider info
            claimInfoLoop2100.addSegment(buildSegment(List.of("82", "1", DataConstants.FAKE_PROVIDER_NAME, DataConstants.FAKE_PROVIDER_NAME, "", "", "", "XX", claimQuery.getProviderNPI()),
                    new Segment("NM1")));

            //ref segment 1
            claimInfoLoop2100.addSegment(buildSegment(List.of("F8", "100184984695"), new Segment("REF")));

            //ref segment 2
            claimInfoLoop2100.addSegment(buildSegment(List.of("CE", "FL"), new Segment("REF")));

            //date segment 1 - claim statement period start
            claimInfoLoop2100.addSegment(buildSegment(List.of("232", DataConstants.dateFormatter835.format(claimQuery.getDateOfService())), new Segment("DTM")));

            //date segment 2 - claim received date
            claimInfoLoop2100.addSegment(buildSegment(List.of("050", DataConstants.dateFormatter835.format(claimQuery.getReceivedDate())), new Segment("DTM")));

            //claim supplemental info
            claimInfoLoop2100.addSegment(buildSegment(List.of("AU", "0.00"), new Segment("AMT")));

            Loop servicePaymentLoop2110 = new Loop(separators, "2110");
            claimInfoLoop2100.getLoops().add(servicePaymentLoop2110);

            //service payment segment-composite element
            Segment svcPaymSegment = new Segment("SVC");
            svcPaymSegment.addCompositeElement("HC:", "HC:99214");
            addElement(svcPaymSegment, "01-02", String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, claimQuery.getClaimChargeAmount()));
            addElement(svcPaymSegment, "01-03", String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, paymentHalf));
            servicePaymentLoop2110.addSegment(svcPaymSegment);

            //service date segment
            servicePaymentLoop2110.addSegment(buildSegment(List.of("472", DataConstants.dateFormatter835.format(claimQuery.getDateOfService())), new Segment("DTM")));

            //service adjustment segment
            servicePaymentLoop2110.addSegment(buildSegment(List.of("CO", "45", "0.00", "", "P24", "0.00"), new Segment("CAS")));

            //services payment ref segment
            servicePaymentLoop2110.addSegment(buildSegment(List.of("6R", "2196311P13648B62477"), new Segment("REF")));

            //services payment amount
            servicePaymentLoop2110.addSegment(buildSegment(List.of("B6", String.format(DataConstants.FLOAT_STRING_FORMAT_TXT, payment.getAmount())), new Segment("AMT")));
        }

        //SE segment - trasnaction set outside of claims loop
        //services payment amount
        Segment txSeSegment = new Segment("SE");
        buildSegment(List.of("32", "0001"), txSeSegment);
        stLoop.addSegment(txSeSegment);

        //GE segment
        Segment geSegment = new Segment("GE");
        buildSegment(List.of("1", "101777856"), geSegment);
        gsLoop.addSegment(geSegment);

        //add IEA segment
        Segment ieaSegment = new Segment("IEA");
        buildSegment(List.of("1", "101925142"), ieaSegment);
        isaLoop.addSegment(ieaSegment);

        X12Writer writer = new X12Writer(X12Reader.FileType.ANSI835_5010_X221, Collections.singletonList(isaLoop), separators);
        String writerResult = writer.toX12String(lineBreak).trim();

        // log.debug("----835 write result: \n" + writerResult);
        return writerResult;
    }

    private Segment buildSegment(List<String> values, Segment segment) {
        int i = 1;
        for (String value : values) {
            String position = String.format("0%s", i);
            addElement(segment, position, value);
            i++;
        }
        return segment;
    }

    private void addElement(Segment segment, String elementNum, String data) {
        segment.addElement(new com.imsweb.x12.Element(segment.getId() + elementNum, data));
    }

    private ByteArrayOutputStream buildPdf(Payer payerFirst, ClaimQuery claimQueryFirst, Payment paymentFirst, List<Tuple> searchResults) throws DocumentException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream baos;
        Document document = new Document(PageSize.LETTER, 1.00F, 0.75F, 0.75F, 0.75F);
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        document.setMargins(40, 40, 40, 40);
        document.newPage();
        document.addHeader("EOB_HEADER", "Sample EOB");

        Paragraph title = new Paragraph(1F, "  ");
        //payer header
        title.add(new Paragraph(DataConstants.PDF_LABEL_PAYER, normalFontBold));
        title.add(new Paragraph(payerFirst.getPayerName(), normalFont));
        title.add(new Paragraph(DataConstants.FAKE_PAYER_ADDRESS, normalFont));

        //payee header
        addEmptyLine(title, 1);
        title.add(new Paragraph(DataConstants.PDF_LABEL_PAYEE, normalFontBold));
        title.add(new Paragraph(String.format(DataConstants.FAKE_PROVIDER_NAME_WITH_NPI, claimQueryFirst.getProviderNPI()), normalFont));
        title.add(new Paragraph(DataConstants.FAKE_PROVIDER_ADDRESS, normalFont));
        //group number
        Paragraph groupNumberLabel = buildField(DataConstants.PDF_LABEL_GROUP_NUMBER, DataConstants.CLAIM_UNKNOWN_VAL);
        title.add(groupNumberLabel);
        //check number
        Paragraph checkNumberLabel = buildField(DataConstants.PDF_LABEL_CHECK_NUMBER, paymentFirst.getPaymentNumber());
        title.add(checkNumberLabel);
        addEmptyLine(title, 2);
        document.add(title);

        //Summary of benefits title
        Paragraph sumOfBenefits = new Paragraph(DataConstants.PDF_LABEL_SUMMARY_OF_BENEFITS, normalFontBold);
        sumOfBenefits.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(sumOfBenefits, 1);
        document.add(sumOfBenefits);

        //loop through data, add them to a table for each claim
        float paymentHalf = 0F;
        float totalPaymentAmount = 0F;
        for (Tuple tuple : searchResults) {
            //new line
            addGrayLine(document);
            Paragraph topTableParagraph = new Paragraph();
            addEmptyLine(topTableParagraph, 1);

            ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
            Patient patient = tuple.get(1, Patient.class);
            Payer payer = tuple.get(2, Payer.class);
            Payment payment = tuple.get(3, Payment.class);
            totalPaymentAmount += payment.getAmount();
            Remittance remittance = tuple.get(4, Remittance.class);
            paymentHalf = payment.getAmount() / searchResults.size();

            //table #1
            PdfPTable topTable = buildTopTable(patient, claimQuery, payment);
            topTableParagraph.add(topTable);
            addEmptyLine(topTableParagraph, 2);
            document.add(topTableParagraph);

            //add claim charges table
            PdfPTable claimTable = new PdfPTable(11);
            claimTable.setWidthPercentage(100);

            //row1 - first header row
            List<PdfPCell> cellsRow1 = buildClaimsRow(normalFontBold, DataConstants.PDF_LABEL_CPT, DataConstants.PDF_LABEL_UNITS,
                    DataConstants.PDF_LABEL_BILLED, DataConstants.PDF_LABEL_ALLOW, DataConstants.PDF_LABEL_PAY, DataConstants.PDF_LABEL_DEDUCT,
                    DataConstants.PDF_LABEL_COINS, DataConstants.PDF_LABEL_COPAY, DataConstants.PDF_LABEL_OTHER_PR, DataConstants.PDF_EMPTY, DataConstants.PDF_LABEL_REAS_RMK);

            //row2 - another header row
            PdfPCell cell2_start = buildPdfPCellfromText(DataConstants.PDF_LABEL_SERVICE_DATES, normalFontBold, 3);
            List<PdfPCell> cellsRow2_end = buildClaimsRow(normalFontBold,
                    DataConstants.PDF_LABEL_CONTR, DataConstants.PDF_LABEL_W_HOLD, DataConstants.PDF_LABEL_GLOBAL,
                    DataConstants.PDF_LABEL_CAP, DataConstants.PDF_LABEL_OTH_CO, DataConstants.PDF_LABEL_DENIED, DataConstants.PDF_LABEL_INCENT,
                    DataConstants.PDF_LABEL_REAS_RMK);

            cellsRow1.add(cell2_start);
            cellsRow1.addAll(cellsRow2_end);

            //row3 - values
            List<PdfPCell> cellsRow3 = buildClaimsRow(normalFont, DataConstants.CLAIM_CPT_VALUE1, DataConstants.CLAIM_CPT_UNITS_VALUE,
                    String.format(DataConstants.FLOAT_STRING_FORMAT_PDF, claimQuery.getClaimChargeAmount()),
                    DataConstants.ZERO_DOLLARS, String.format(DataConstants.FLOAT_STRING_FORMAT_PDF, paymentHalf), DataConstants.ZERO_DOLLARS,
                    DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
            cellsRow1.addAll(cellsRow3);

            //row4 - dates of services row
            PdfPCell cell4_start = buildPdfPCellfromText(ApiUtils.dateFormatter.format(claimQuery.getDateOfService()),
                    normalFont, 3);
            List<PdfPCell> cellsRow4_end = buildClaimsRow(normalFont, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS,
                    DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.CLAIM_RMK_VALUE);
            cellsRow1.add(cell4_start);
            cellsRow1.addAll(cellsRow4_end);

            //row5 another charge line
            List<PdfPCell> cellsRow5 = buildClaimsRow(normalFont, DataConstants.CLAIM_CPT_VALUE2,
                    DataConstants.CLAIM_CPT_UNITS_VALUE,
                    DataConstants.ZERO_DOLLARS,
                    DataConstants.ZERO_DOLLARS, String.format(DataConstants.FLOAT_STRING_FORMAT_PDF, paymentHalf), DataConstants.ZERO_DOLLARS,
                    DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
            cellsRow1.addAll(cellsRow5);

            for (PdfPCell cell : cellsRow1) {
                claimTable.addCell(cell);
            }
            Paragraph claimsParagraph = new Paragraph();
            claimsParagraph.add(claimTable);
            addEmptyLine(claimsParagraph, 1);
            document.add(claimsParagraph);

            //add explanation, after table
            Paragraph lastLine = buildField(String.format("%s: ", DataConstants.CLAIM_RMK_VALUE),
                    DataConstants.CLAIM_CO22_EXPLANATION);
            addEmptyLine(lastLine, 1);
            document.add(lastLine);
        }
        //add total payment
        Paragraph totalPaymentLine = buildField(DataConstants.CLAIM_LBL_TOTAL_PAYMENTS,
                String.format(DataConstants.FLOAT_STRING_FORMAT_PDF, totalPaymentAmount));
        document.add(totalPaymentLine);
        document.close();

        //create zip file, convert to Base64 encoded string
        byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray(); //uncompressed
        baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry out = new ZipEntry(DataConstants.PDF_EOB_FILE_NAME);
            zos.putNextEntry(out);
            zos.write(uncompressedByteArray, 0, uncompressedByteArray.length);
            zos.closeEntry();
        }
        return baos;
    }

    private static List buildClaimsRow(Font font, String... vals) {
        List<PdfPCell> cells = new ArrayList<>();
        for (String value : vals) {
            PdfPCell cell = buildPdfPCellfromText(value, font, 1);
            cells.add(cell);
        }
        return cells;
    }

    private PdfPTable buildTopTable(Patient patient, ClaimQuery claimQuery, Payment payment) {
        PdfPTable topTable = new PdfPTable(3);
        topTable.setWidthPercentage(100);
        PdfPCell c1 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PATIENT, patient.getLastName() + ", " +
                patient.getFirstName());
        PdfPCell c2 = buildPdfPCellWithLabels(String.format("%s: ", DataConstants.PDF_LABEL_BILLED), String.format("$%.2f", claimQuery.getClaimChargeAmount()));
        PdfPCell c3 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PAID, String.format("$%.2f", payment.getAmount()));
        PdfPCell c4 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PATIENT_ID, claimQuery.getSubscriberPatientId());
        PdfPCell c5 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        PdfPCell c6 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        //new row
        PdfPCell c7 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PROVIDER, String.format(DataConstants.FAKE_PROVIDER_NAME_WITH_NPI, claimQuery.getProviderNPI()));
        PdfPCell c8 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_OTHER_PROVIDER, DataConstants.CLAIM_NOT_AVAILABLE_VALUE);
        PdfPCell c9 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        //new row
        PdfPCell c10 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PROVIDER_NPI, claimQuery.getProviderNPI());
        PdfPCell c11 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        PdfPCell c12 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        //new row
        PdfPCell c13 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PATIENT_ACCOUNT, claimQuery.getProviderClaimID());
        PdfPCell c14 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PAYER_CLAIM, claimQuery.getPayerClaimId());
        PdfPCell c15 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PAY_DATE, ApiUtils.dateFormatter.format(payment.getPayment_issue_dt()));

        List cells = List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15);
        for (Object cell : cells) {
            topTable.addCell((PdfPCell) cell);
        }
        return topTable;
    }

    private static PdfPCell buildPdfPCellWithLabels(String label, String fieldValue) {
        Paragraph fieldParagraph = buildField(label, fieldValue);
        return buildPdfPCellfromParagraph(fieldParagraph);
    }

    private static PdfPCell buildPdfPCellfromParagraph(Paragraph fieldParagraph) {
        PdfPCell cell = new PdfPCell(fieldParagraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static PdfPCell buildPdfPCellfromText(String value, Font font, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, font));
        cell.setBorderColorTop(BaseColor.BLACK);
        cell.setColspan(colspan);
        cell.setBorderColorRight(BaseColor.BLACK);
        cell.setBorderColorBottom(BaseColor.BLACK);
        cell.setBorderColorLeft(BaseColor.BLACK);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static Paragraph buildField(String label, String fieldValue) {
        Paragraph field = new Paragraph();
        field.add(new Chunk(label, normalFontBold));
        field.add(new Chunk(fieldValue, normalFont));
        return field;
    }

    private static void addGrayLine(Document document) throws DocumentException {
        var line = new LineSeparator(grayFont);

        document.add(line);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" ", normalFont));
        }
    }
}
