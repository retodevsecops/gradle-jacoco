package com.consubanco.openpdf;

import com.consubanco.model.entities.document.gateway.PdfDocumentGateway;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class PdfDocumentAdapter implements PdfDocumentGateway {

    @Override
    public Mono<String> generatePdfWithImages(List<String> imagesInBase64) {
        return Mono.fromCallable(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (Document document = new Document()) {
                PdfWriter.getInstance(document, outputStream);
                document.open();
                imagesInBase64.forEach(imageInBase64 -> {
                    addImageToPdf(document, imageInBase64);
                });
            } catch (Exception e) {
            }
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        });
    }

    private void addImageToPdf(Document document, String imageBase64) {
        try {
            byte[] imageData = Base64.getDecoder().decode(imageBase64);
            Image image = Image.getInstance(imageData);
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();
            float documentWidth = document.getPageSize().getWidth();
            float documentHeight = document.getPageSize().getHeight();
            if (imageWidth > documentWidth) {
                float scaleFactor = documentWidth / imageWidth;
                image.scaleToFit(documentWidth, imageHeight * scaleFactor);
            }
            document.newPage();
            float positionX = (documentWidth - image.getScaledWidth()) / 2;
            float positionY = (documentHeight - image.getScaledHeight()) / 2;
            image.setAbsolutePosition(positionX, positionY);
            document.add(image);
        } catch (IOException exception) {

        }
    }
}
