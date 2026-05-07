// package com.znz.tpip_backend.service;

// import com.itextpdf.kernel.colors.ColorConstants;
// import com.itextpdf.kernel.pdf.PdfWriter;
// import com.itextpdf.kernel.pdf.PdfDocument;
// import com.itextpdf.layout.Document;
// import com.itextpdf.layout.element.*;
// import com.itextpdf.layout.properties.TextAlignment;
// import com.itextpdf.layout.borders.SolidBorder;
// import com.itextpdf.layout.properties.UnitValue;
// import com.znz.tpip_backend.model.Payment;
// import org.springframework.stereotype.Service;

// import java.io.ByteArrayOutputStream;

// @Service
// public class ReceiptPdfService {

//     public byte[] generateReceiptPdf(Payment payment) {

//         try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

//             PdfWriter writer = new PdfWriter(out);
//             PdfDocument pdf = new PdfDocument(writer);
//             Document document = new Document(pdf);

//             // ================= HEADER =================
//             Paragraph header = new Paragraph("REVOLUTIONARY GOVERNMENT OF ZANZIBAR")
//                     .setBold()
//                     .setFontSize(14)
//                     .setTextAlignment(TextAlignment.CENTER);

//             Paragraph subHeader = new Paragraph("TPIP PAYMENT RECEIPT")
//                     .setBold()
//                     .setFontSize(12)
//                     .setTextAlignment(TextAlignment.CENTER);

//             document.add(header);
//             document.add(subHeader);
//             document.add(new Paragraph("\n"));

//             // ================= RECEIPT BOX =================
//             Table table = new Table(UnitValue.createPercentArray(new float[]{3, 5}))
//                     .useAllAvailableWidth();

//             table.addCell(createCell("Reference Number"));
//             table.addCell(createCell(payment.getReferenceNumber()));

//             table.addCell(createCell("Transaction ID"));
//             table.addCell(createCell(payment.getTransactionId()));

//             table.addCell(createCell("Amount Paid"));
//             table.addCell(createCell(payment.getAmount() + " " + payment.getCurrency()));

//             table.addCell(createCell("Status"));
//             table.addCell(createCell("PAID"));

//             table.addCell(createCell("Date"));
//             table.addCell(createCell(payment.getCreatedAt().toString()));

//             document.add(table);

//             document.add(new Paragraph("\n"));

//             // ================= FOOTER =================
//             Paragraph footer = new Paragraph(
//                     "This is an official receipt generated electronically.\n" +
//                     "For inquiries, contact: support@tpip.go.tz"
//             )
//                     .setFontSize(9)
//                     .setTextAlignment(TextAlignment.CENTER);

//             document.add(footer);

//             document.close();

//             return out.toByteArray();
//         } catch (Exception e) {
//             throw new RuntimeException("Error generating receipt PDF", e);
//         }
//     }

//     // ================= HELPER METHOD =================
//     private Cell createCell(String text) {
//         return new Cell()
//                 .add(new Paragraph(text))
//                 .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
//                 .setPadding(5);
//     }
// }

package com.znz.tpip_backend.service;

import com.znz.tpip_backend.email.EmailService;
import com.znz.tpip_backend.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final EmailService emailService;

    public void generateAndSendReceipt(Payment payment) {

        String receipt = """
                PAYMENT RECEIPT
                ----------------------
                Reference: %s
                Amount: %.2f %s
                Status: PAID
                Transaction: %s
                """.formatted(
                payment.getReferenceNumber(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTransactionId()
        );

        emailService.sendEmail(
                payment.getPayerPhone(),
                "TPIP Payment Receipt",
                receipt
        );
    }
}
