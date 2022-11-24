package pdftohtml;

import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.basic.PdfDocumentObject;
import pdftohtml.domain.pdf.object.basic.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.basic.TextObject;
import pdftohtml.domain.pdf.object.basic.container.PageLine;

import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

    public static List<PageLine> createMockPageLinesList(
            List<FrameworkRectangle> rectangles
    ) {
        return rectangles.stream().map(rectangle -> {
            PageLine line = new PageLine();
            line.addObject(
                    TestUtils.createMockPdfTextObject(PdfDocumentObjectType.SIMPLE_TEXT, FrameworkRectangle.EMPTY)
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
                createMockPdfTextObject(PdfDocumentObjectType.SIMPLE_TEXT, rectangle))
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
