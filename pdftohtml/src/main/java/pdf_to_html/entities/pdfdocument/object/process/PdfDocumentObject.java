package pdf_to_html.entities.pdfdocument.object.process;

import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.helpers.Rectangle2DHelper;

public abstract class PdfDocumentObject {

    protected Rectangle2D rectangle;

    protected PdfDocumentObjectType objectType;

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle2D rectangle) {
        this.rectangle = rectangle;
    }

    public void addToRectangle(Rectangle2D rectangle) {
        Rectangle2DHelper helper = new Rectangle2DHelper();
        this.rectangle = helper.combineTwoRectangles(this.rectangle, rectangle);
    }

    public PdfDocumentObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(PdfDocumentObjectType objectType) {
        this.objectType = objectType;
    }

}
