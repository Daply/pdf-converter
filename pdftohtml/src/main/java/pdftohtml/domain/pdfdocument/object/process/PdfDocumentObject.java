package pdftohtml.domain.pdfdocument.object.process;

import pdftohtml.domain.framework.Rectangle;
import pdftohtml.helpers.RectangleHelper;

public abstract class PdfDocumentObject {

    protected Rectangle rectangle;

    protected PdfDocumentObjectType objectType;

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void addToRectangle(Rectangle rectangle) {
        RectangleHelper helper = new RectangleHelper();
        this.rectangle = helper.combineTwoRectangles(this.rectangle, rectangle);
    }

    public PdfDocumentObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(PdfDocumentObjectType objectType) {
        this.objectType = objectType;
    }

}
