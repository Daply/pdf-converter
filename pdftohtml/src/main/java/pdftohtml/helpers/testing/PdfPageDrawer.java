package pdftohtml.helpers.testing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pdftohtml.domain.framework.FrameworkRectangle;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PdfPageDrawer {

    public static void drawRectangle(
            PDDocument document,
            PDPage page,
            FrameworkRectangle rectangle,
            Color color,
            String text
    ) {
        String testDocument = "D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\page.pdf";
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
            );
            contentStream.setNonStrokingColor(Color.RED);
            contentStream.setStrokingColor(color);
            float x = (float) rectangle.getMinX();
            float y = page.getCropBox().getUpperRightY() - (float) rectangle.getMaxY();
            //      -----------
            //      |         |
            //      -----------
            //    x, y
            contentStream.addRect(x, y, (float) rectangle.getWidth(), (float) rectangle.getHeight());
            if (text != null) {
                contentStream.beginText();
                contentStream.newLineAtOffset(
                        (float) rectangle.getMinX() + 2f,
                        page.getCropBox().getHeight() - (float) rectangle.getMinY() + 2f
                );
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.showText(text);
                contentStream.endText();
            }
            contentStream.stroke();
            contentStream.close();

            File file = new File(testDocument);
            document.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
