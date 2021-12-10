package pdftohtml;

import pdftohtml.domain.framework.Rectangle;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObject;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdfdocument.object.process.TextObject;
import pdftohtml.domain.pdfdocument.object.process.container.PageLine;

import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

    public static List<PageLine> createMockPageLinesList(
            List<Rectangle> rectangles
    ) {
        return rectangles.stream().map(rectangle -> {
            PageLine line = new PageLine();
            line.addObject(
                    TestUtils.createMockPdfTextObject(PdfDocumentObjectType.SIMPLE_TEXT, Rectangle.EMPTY)
            );
            line.setRectangle(rectangle);
            return line;
        }).collect(Collectors.toList());
    }

    public static PageLine createMockPageLine(
            List<PdfDocumentObject> objects,
            Rectangle rectangle
    ) {
        PageLine pageLine = new PageLine();
        pageLine.setObjects(objects);
        pageLine.setRectangle(rectangle);
        return pageLine;
    }

    public static List<PdfDocumentObject> createMockPdfTextObjectsList(List<Rectangle> rectangles) {
        return rectangles.stream().map(rectangle ->
                createMockPdfTextObject(PdfDocumentObjectType.SIMPLE_TEXT, rectangle))
                .collect(Collectors.toList());
    }

    public static PdfDocumentObject createMockPdfTextObject(
            PdfDocumentObjectType objectType,
            Rectangle rectangle
    ) {
        PdfDocumentObject pdfDocumentObject = new TextObject();
        pdfDocumentObject.setObjectType(objectType);
        pdfDocumentObject.setRectangle(rectangle);
        return pdfDocumentObject;
    }
}
