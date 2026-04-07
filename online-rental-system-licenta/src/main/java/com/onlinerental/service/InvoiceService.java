package com.onlinerental.service;

import com.onlinerental.domain.Rental;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public byte[] generateInvoicePdf(Rental rental, String transactionId) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(50, 760);
                cs.showText("Invoice - Online Rental System");
                cs.endText();

                writeLine(cs, 730, "Transaction ID: " + transactionId);
                writeLine(cs, 710, "Date: " + DATE_FMT.format(rental.getCreatedAt()));
                writeLine(cs, 690, "Rental ID: " + rental.getId());
                writeLine(cs, 670, "Customer: " + rental.getUser().getUsername());
                writeLine(cs, 650, "Item: " + rental.getInventory().getProduct().getName());
                writeLine(cs, 630, "Period: " + rental.getStartDate() + " - " + rental.getEndDate());
                writeLine(cs, 610, "Total price: " + rental.getTotalPrice() + " RON");
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    private static void writeLine(PDPageContentStream cs, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(50, y);
        cs.showText(text);
        cs.endText();
    }
}

