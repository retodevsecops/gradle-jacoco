package com.consubanco.openpdf;

import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class PDFDocumentAdapter implements PDFDocumentGateway {

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
                throw new RuntimeException("Error generate PDF with images.", e);
            }
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        });
    }

    @Override
    public Mono<String> getPageFromPDF(String base64PDF, Integer page) {
        return Mono.fromCallable(() -> {
            byte[] decodedPdf = Base64.getDecoder().decode(base64PDF);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedPdf);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 PdfReader reader = new PdfReader(inputStream)) {
                Integer pageNumber = getPageNumber(page, reader.getNumberOfPages());
                Document document = new Document(reader.getPageSize(pageNumber));
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                document.open();
                document.newPage();
                writer.getDirectContent().addTemplate(writer.getImportedPage(reader, pageNumber), 0, 0);
                document.close();
                return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException("Error processing PDF", e);
            }
        });
    }

    @Override
    public Mono<String> merge(List<String> base64Documents) {
        return Mono.fromCallable(() -> {
            ByteArrayOutputStream mergedPdfStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, mergedPdfStream);
            document.open();
            try {
                for (String base64Document : base64Documents) {
                    byte[] decodedDocument = Base64.getDecoder().decode(base64Document);
                    PdfReader reader = new PdfReader(new ByteArrayInputStream(decodedDocument));
                    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                        copy.addPage(copy.getImportedPage(reader, i));
                    }
                    reader.close();
                }
                document.close();
            } catch (IOException e) {
                throw new RuntimeException("Error merging PDFs", e);
            }
            return Base64.getEncoder().encodeToString(mergedPdfStream.toByteArray());
        });
    }

    /**
     * If pageRequired is negative, it takes the page counted from the end and if positive it takes it from beginning.
     *
     * @param pageRequired     page required
     * @param numberOfPagesPDF number of pages in pdf file
     * @return required page number of pdf
     */
    private Integer getPageNumber(Integer pageRequired, Integer numberOfPagesPDF) {
        int pageAbsolute = Math.abs(pageRequired);
        if (pageAbsolute == 0) throw new IllegalArgumentException("Invalid page number.");
        if (pageAbsolute > numberOfPagesPDF)
            throw new IndexOutOfBoundsException("The required page exceeds the number of pages in the pdf file.");
        if (pageRequired > 0) return pageRequired;
        return numberOfPagesPDF - (pageAbsolute-1);
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
