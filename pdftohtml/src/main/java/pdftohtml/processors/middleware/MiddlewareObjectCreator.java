package pdftohtml.processors.middleware;

import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObject;

public abstract class MiddlewareObjectCreator {

    protected LinesMiddlewareObjectsProcessor linesMiddlewareObjectsProcessor =
            new LinesMiddlewareObjectsProcessor();

    public abstract MiddlewareObject create(PdfDocumentObject object);

    protected void validateObject(PdfDocumentObject object) {
        if (object == null)
            throw new IllegalArgumentException("object in middleware object creator is NULL");
    }

}
