package org.hl7.davinci.pr.service;

import com.itextpdf.text.*;
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
import org.hl7.davinci.pr.repositories.ClaimQueryDao;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
        if(searchResults == null || searchResults.size() == 0) {
            return null;
        }
        DocumentReference documentReference = new DocumentReference();
        documentReference.setId("remittance-document-" + FhirUtils.generateUniqueResourceID());
        documentReference.setMeta(FhirUtils.generateResourceMeta("http://hl7.org/fhir/us/davinci-pr/StructureDefinition/remittanceAdviceDocument"));
        documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        /*the order of tuples is:
          {@link ClaimQuery}, {@link org.hl7.davinci.pr.domain.Patient}, {@link org.hl7.davinci.pr.domain.Payer}, {@link Payment}, {@link org.hl7.davinci.pr.domain.Remittance}*/
        Tuple firstTuple = searchResults.get(0);
        ClaimQuery claimQueryFirst = firstTuple.get(0, ClaimQuery.class);
        Payer payerFirst = firstTuple.get(2, Payer.class);
        Payment paymentFirst = firstTuple.get(3, Payment.class);

        //build pdf by default
        if (remittanceType == null || remittanceType.equals(ApiConstants.REMITTANCE_ADVICE_TYPE_PDF)) {
            Document document = new Document(PageSize.LETTER, 1.00F, 0.75F, 0.75F, 0.75F);
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            document.setMargins(40,40,40,40);
            document.newPage();
            document.addHeader("EOB_HEADER", "Sample EOB");

            Paragraph title = new Paragraph(1F, "  ");
            //payer header
            title.add(new Paragraph(DataConstants.PDF_LABEL_PAYER, normalFontBold ));
            title.add(new Paragraph(payerFirst.getPayerName(), normalFont ));
            title.add(new Paragraph(DataConstants.FAKE_PAYER_ADDRESS, normalFont));

            //payee header
            addEmptyLine(title, 1);
            title.add(new Paragraph(DataConstants.PDF_LABEL_PAYEE, normalFontBold ));
            title.add(new Paragraph(String.format(DataConstants.FAKE_PROVIDER_NAME, claimQueryFirst.getProviderNPI()), normalFont ));
            title.add(new Paragraph(DataConstants.FAKE_PROVIDER_ADDRESS, normalFont));
            //group number
            Paragraph groupNumberLabel = buildField(DataConstants.PDF_LABEL_GROUP_NUMBER, DataConstants.CLAIM_UNKNOWN_VAL);
            title.add(groupNumberLabel);
            //check number
            Paragraph checkNumberLabel = buildField(DataConstants.PDF_LABEL_CHECK_NUMBER, paymentFirst.getPaymentNumber());
            title.add(checkNumberLabel);
            addEmptyLine(title,2);
            document.add(title);

            //Summary of benefits title
            Paragraph sumOfBenefits = new Paragraph(DataConstants.PDF_LABEL_SUMMARY_OF_BENEFITS, normalFontBold);
            sumOfBenefits.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(sumOfBenefits,1);
            document.add(sumOfBenefits);

            //loop through data, add them to a table for each claim
            float paymentHalf = 0F;
            float totalPaymentAmount = 0F;
            for(Tuple tuple:searchResults) {
                //new line
                addGrayLine(document);
                Paragraph topTableParagraph = new Paragraph();
                addEmptyLine(topTableParagraph,1);

                ClaimQuery claimQuery = tuple.get(0, ClaimQuery.class);
                Patient patient = tuple.get(1, Patient.class);
                Payer payer = tuple.get(2, Payer.class);
                Payment payment = tuple.get(3, Payment.class);
                totalPaymentAmount += payment.getAmount();
                Remittance remittance = tuple.get(4, Remittance.class);
                paymentHalf = payment.getAmount()/2;

                //table #1
                PdfPTable topTable = buildTopTable(patient, claimQuery, payment);
                topTableParagraph.add(topTable);
                addEmptyLine(topTableParagraph,2);
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
                        String.format(DataConstants.FLOAT_STRING_FORMAT,claimQuery.getClaimChargeAmount()),
                        DataConstants.ZERO_DOLLARS, String.format(DataConstants.FLOAT_STRING_FORMAT,paymentHalf), DataConstants.ZERO_DOLLARS,
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
                        DataConstants.ZERO_DOLLARS, String.format(DataConstants.FLOAT_STRING_FORMAT,paymentHalf), DataConstants.ZERO_DOLLARS,
                        DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.ZERO_DOLLARS, DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
                cellsRow1.addAll(cellsRow5);

                for(PdfPCell cell:cellsRow1) {
                    claimTable.addCell(cell);
                }
                Paragraph claimsParagraph = new Paragraph();
                claimsParagraph.add(claimTable);
                addEmptyLine(claimsParagraph, 1);
                document.add(claimsParagraph);

                //add explanation, after table
                Paragraph lastLine = buildField(String.format("%s: ", DataConstants.CLAIM_RMK_VALUE),
                        DataConstants.CLAIM_CO22_EXPLANATION);
                addEmptyLine(lastLine,1);
                document.add(lastLine);
            }
            //add total payment
            Paragraph totalPaymentLine = buildField(DataConstants.CLAIM_LBL_TOTAL_PAYMENTS,
                    String.format(DataConstants.FLOAT_STRING_FORMAT, totalPaymentAmount));
            document.add(totalPaymentLine);
            document.close();

            //create zip file, convert to Base64 encoded string
            byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray(); //uncompressed
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try( ZipOutputStream zos = new ZipOutputStream( baos ) ) {
                ZipEntry out = new ZipEntry(DataConstants.PDF_EOB_FILE_NAME);
                zos.putNextEntry( out );
                zos.write( uncompressedByteArray, 0, uncompressedByteArray.length );
                zos.closeEntry();
            }
            byte[] compressedByArray = baos.toByteArray();

            String encodedBase64String = Base64.getEncoder().encodeToString(compressedByArray);
            log.debug("------encoded sting: \n" + encodedBase64String);
            DocumentReference.DocumentReferenceContentComponent content = documentReference.addContent();

            Base64BinaryType base64BinaryType = new Base64BinaryType();
            base64BinaryType.setValueAsString(encodedBase64String);
            Attachment attachment = new Attachment();
            attachment.setContentType(DataConstants.CONTENT_TYPE_ZIP);
            attachment.setDataElement(base64BinaryType);
            content.setAttachment(attachment);
        }
        return documentReference;
    }

    private static List buildClaimsRow(Font font, String... vals) {
        List<PdfPCell> cells = new ArrayList<>();
        for(String value:vals) {
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
        PdfPCell c3 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PAID,  String.format("$%.2f", payment.getAmount()));
        PdfPCell c4 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PATIENT_ID, claimQuery.getSubscriberPatientId());
        PdfPCell c5 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        PdfPCell c6 = buildPdfPCellWithLabels(DataConstants.PDF_EMPTY, DataConstants.PDF_EMPTY);
        //new row
        PdfPCell c7 = buildPdfPCellWithLabels(DataConstants.PDF_LABEL_PROVIDER, String.format(DataConstants.FAKE_PROVIDER_NAME, claimQuery.getProviderNPI()));
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
        for(Object cell:cells) {
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
