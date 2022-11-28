package pdftohtml;

import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.text.TextObject;
import pdftohtml.domain.pdf.object.container.PageLine;

import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

    public static List<PageLine> createMockPageLinesList(
            List<FrameworkRectangle> rectangles
    ) {
        return rectangles.stream().map(rectangle -> {
            PageLine line = new PageLine();
            line.addObject(
                    TestUtils.createMockPdfTextObject(PdfDocumentObjectType.TEXT, FrameworkRectangle.EMPTY)
            );
            line.setRectangle(rectangle);
            return line;
        }).collect(Collectors.toList());
    }

    public static PageLine createMockPageLine(
            List<PdfDocumentObject> objects,
            FrameworkRectangle rectangle
    ) {
        PageLine pageLine = new PageLine();
        pageLine.setObjects(objects);
        pageLine.setRectangle(rectangle);
        return pageLine;
    }

    public static List<PdfDocumentObject> createMockPdfTextObjectsList(List<FrameworkRectangle> rectangles) {
        return rectangles.stream().map(rectangle ->
                createMockPdfTextObject(PdfDocumentObjectType.TEXT, rectangle))
                .collect(Collectors.toList());
    }

    public static PdfDocumentObject createMockPdfTextObject(
            PdfDocumentObjectType objectType,
            FrameworkRectangle rectangle
    ) {
        PdfDocumentObject pdfDocumentObject = new TextObject();
        pdfDocumentObject.setObjectType(objectType);
        pdfDocumentObject.setRectangle(rectangle);
        return pdfDocumentObject;
    }
}
