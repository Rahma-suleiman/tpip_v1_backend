package com.znz.tpip_backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.znz.tpip_backend.model.Payment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReceiptPdfService {

    public byte[] generateReceiptPdf(Payment payment) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();

            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("TPIP PAYMENT RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Content
            document.add(new Paragraph("Reference Number: " + payment.getReferenceNumber()));
            document.add(new Paragraph("Transaction ID: " + payment.getTransactionId()));
            document.add(new Paragraph("Applicant: " + payment.getPayerName()));
            document.add(new Paragraph("Phone: " + payment.getPayerPhone()));
            document.add(new Paragraph("Amount: " + payment.getAmount() + " " + payment.getCurrency()));
            document.add(new Paragraph("Status: " + payment.getStatus()));
            document.add(new Paragraph("Paid At: " + payment.getPaidAt()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for using TPIP System."));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF receipt", e);
        }
    }
}