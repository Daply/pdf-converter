package pdf_to_html.entities.pdfdocument.object.process.container;

import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.entities.pdfdocument.object.process.LinkObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObjectType;
import pdf_to_html.entities.pdfdocument.object.process.TextObject;
import pdf_to_html.helpers.Rectangle2DHelper;

import java.util.ArrayList;
import java.util.List;

public class PageLine {

    private Rectangle2D rectangle;

    private StringBuffer text;

    private List<PdfDocumentObject> objects;

    private int lineNumber;

    public PageLine() {
        init();
    }

    private void init() {
        this.text = new StringBuffer();
        this.rectangle = Rectangle2D.EMPTY;
        this.objects = new ArrayList<>();
        this.lineNumber = -1;
    }

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle2D rectangle) {
        this.rectangle = rectangle;
    }

    public String getText() {
        return text.toString();
    }

    public void addObject(PdfDocumentObject object) {
        this.objects.add(object);
        addToText(object);
        addToRectangle(object.getRectangle());
    }

    public void addAllObjects(List<PdfDocumentObject> objects) {
        this.objects.addAll(objects);
        for (PdfDocumentObject object: objects) {
            addToText(object);
            addToRectangle(object.getRectangle());
        }
    }

    public void removeObject(PdfDocumentObject object) { // TODO
        this.objects.remove(object);
        addToText(object);
        subtractFromRectangle(object.getRectangle());
    }

    public void addToRectangle(Rectangle2D rectangle) {
        Rectangle2DHelper helper = new Rectangle2DHelper();
        if (this.rectangle.equals(Rectangle2D.EMPTY))
            this.rectangle = rectangle;
        else
            this.rectangle = helper.combineTwoRectangles(this.rectangle, rectangle);
    }

    public void addToText(PdfDocumentObject object) {
        if (object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) ||
                object.getObjectType().equals(PdfDocumentObjectType.LINK)) {
            this.text.append(((TextObject) object).getText());
        }
    }

    public void subtractFromRectangle(Rectangle2D rectangle) {
        Rectangle2DHelper helper = new Rectangle2DHelper();
        this.rectangle = helper.subtractRectangle(this.rectangle, rectangle);
    }

    public List<PdfDocumentObject> getObjects() {
        return objects;
    }

    public void setObjects(List<PdfDocumentObject> objects) {
        this.objects = objects;
    }

    public void setObject(int objectIndex, PdfDocumentObject object) {
        this.objects.set(objectIndex, object);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
