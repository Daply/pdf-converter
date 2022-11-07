package pdftohtml.domain.pdf.object.process.container;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.domain.pdf.object.process.PdfDocumentObject;
import pdftohtml.domain.pdf.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.process.TextObject;

import java.util.ArrayList;
import java.util.List;

import static pdftohtml.helpers.RectangleHelper.combineTwoRectangles;
import static pdftohtml.helpers.RectangleHelper.subtractRectangle;

@Getter
public class PageLine {

    private FrameworkRectangle rectangle;

    private StringBuilder text;

    private List<PdfDocumentObject> objects;

    @Setter
    private int lineNumber;

    public PageLine() {
        init();
    }

    private void init() {
        this.text = new StringBuilder();
        this.rectangle = new FrameworkRectangle();
        this.objects = new ArrayList<>();
        this.lineNumber = -1;
    }

    public FrameworkRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(FrameworkRectangle rectangle) {
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

    public void addToRectangle(FrameworkRectangle rectangle) {
        if (this.rectangle.equals(FrameworkRectangle.EMPTY))
            this.rectangle = rectangle;
        else
            this.rectangle = combineTwoRectangles(this.rectangle, rectangle);
    }

    public void addToText(PdfDocumentObject object) {
        if (object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) ||
                object.getObjectType().equals(PdfDocumentObjectType.LINK)) {
            this.text.append(((TextObject) object).getText());
        }
    }

    public void subtractFromRectangle(FrameworkRectangle rectangle) {
        this.rectangle = subtractRectangle(this.rectangle, rectangle);
    }

    public void setObjects(List<PdfDocumentObject> objects) {
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageLine pageLine = (PageLine) o;
        return lineNumber == pageLine.lineNumber &&
                rectangle.equals(pageLine.rectangle) &&
                text.toString().equals(pageLine.text.toString()) &&
                objects.equals(pageLine.objects);
    }

    @Override
    public int hashCode() {
        int objectsHashcode = objects.stream()
                .mapToInt(PdfDocumentObject::hashCode)
                .sum();
        return objectsHashcode +
                text.toString().hashCode() +
                rectangle.hashCode() +
                lineNumber;
    }
}
