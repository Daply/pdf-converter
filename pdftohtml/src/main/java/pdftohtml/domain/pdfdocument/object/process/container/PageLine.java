package pdftohtml.domain.pdfdocument.object.process.container;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.framework.Rectangle;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObject;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdfdocument.object.process.TextObject;
import pdftohtml.helpers.RectangleHelper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PageLine {

    private Rectangle rectangle;

    private StringBuilder text;

    private List<PdfDocumentObject> objects;

    @Setter
    private int lineNumber;

    public PageLine() {
        init();
    }

    private void init() {
        this.text = new StringBuilder();
        this.rectangle = new Rectangle();
        this.objects = new ArrayList<>();
        this.lineNumber = -1;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
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

    public void addToRectangle(Rectangle rectangle) {
        RectangleHelper helper = new RectangleHelper();
        if (this.rectangle.equals(Rectangle.EMPTY))
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

    public void subtractFromRectangle(Rectangle rectangle) {
        RectangleHelper helper = new RectangleHelper();
        this.rectangle = helper.subtractRectangle(this.rectangle, rectangle);
    }

    public void setObjects(List<PdfDocumentObject> objects) {
        this.objects = objects;
    }
}
