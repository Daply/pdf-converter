package pdf_to_html.processors.middleware;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;

public abstract class MiddlewareObjectCreator {

    protected LinesMiddlewareObjectsProcessor linesMiddlewareObjectsProcessor =
            new LinesMiddlewareObjectsProcessor();

    public abstract MiddlewareObject create(PdfDocumentObject object);

    protected void validateObject(PdfDocumentObject object) {
        if (object == null)
            throw new IllegalArgumentException("object in middleware object creator is NULL");
    }

}
