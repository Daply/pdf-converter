package pdftohtml.helpers.testing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import pdftohtml.domain.framework.Rectangle;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PdfPageDrawer {

    public static void drawRectangle(PDDocument document, PDPage page, Rectangle rectangle, Color color) throws IOException {
        String testDocument = "src//main//java//pdftohtml//page.pdf";
        PDPageContentStream contentStream = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream.setNonStrokingColor(Color.RED);
        contentStream.setStrokingColor(color);
        float x = (float) rectangle.getMinX();
        float y = page.getCropBox().getUpperRightY() - (float) rectangle.getMaxY();
        //      -----------
        //      |         |
        //      -----------
        //    x, y
        contentStream.addRect(x, y, (float) rectangle.getWidth(), (float) rectangle.getHeight());
        contentStream.stroke();
        contentStream.close();

        File file = new File(testDocument);
        document.save(file);

        //document.close();
    }

}
