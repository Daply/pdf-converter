package pdftohtml.processors.mediate;

import pdftohtml.domain.pdf.object.mediate.MediateObject;
import pdftohtml.domain.pdf.object.basic.PdfDocumentObject;

public abstract class MediateObjectCreator {

    public abstract MediateObject create(PdfDocumentObject object);

    protected void validateObject(PdfDocumentObject object) {
        if (object == null)
            throw new IllegalArgumentException("object in middleware object creator is NULL");
    }

}
