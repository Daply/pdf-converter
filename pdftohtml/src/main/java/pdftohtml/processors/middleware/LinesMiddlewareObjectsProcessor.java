package pdftohtml.processors.middleware;

import pdftohtml.domain.pdf.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdf.object.middleware.TextParagraph;
import pdftohtml.domain.pdf.object.process.PdfDocumentObject;
import pdftohtml.domain.pdf.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.process.SkeletonType;
import pdftohtml.domain.pdf.object.process.TextObject;
import pdftohtml.domain.pdf.object.process.complex.Skeleton;
import pdftohtml.domain.pdf.object.process.container.PageLine;

import java.util.ArrayList;
import java.util.List;

public class LinesMiddlewareObjectsProcessor {

    public List<MiddlewareObject> processLines(List<PageLine> lines) {
        MiddlewareObjectCreator middlewareObjectCreator = null;
        List<MiddlewareObject> middlewareObjects = new ArrayList<>();
        TextParagraph paragraph = new TextParagraph();
        PageLine previousLine = null;
        for (PageLine line: lines) {
            for (PdfDocumentObject object: line.getObjects()) {
                if (object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) ||
                        object.getObjectType().equals(PdfDocumentObjectType.LINK)) {
                    TextObject textObject = (TextObject) object;
                    paragraph.addTextObject(textObject);
                }
                else {
                    if (!paragraph.getTextObjects().isEmpty())
                        middlewareObjects.add(paragraph);
                    paragraph = new TextParagraph();
                    if (object.getObjectType().equals(PdfDocumentObjectType.SKELETON)) {
                        Skeleton skeleton = (Skeleton) object;
                        if (skeleton.getType().equals(SkeletonType.LIST)) {
                            middlewareObjectCreator = new ItemsListCreator();
                            middlewareObjects.add(middlewareObjectCreator.create(skeleton));
                        }
                        if (skeleton.getType().equals(SkeletonType.TABLE)) {
                            middlewareObjectCreator = new TableCreator();
                            middlewareObjects.add(middlewareObjectCreator.create(skeleton));
                        }
                    }
                }
            }
            previousLine = line;
        }
        if (!paragraph.getTextObjects().isEmpty())
            middlewareObjects.add(paragraph);
        return middlewareObjects;
    }

}
