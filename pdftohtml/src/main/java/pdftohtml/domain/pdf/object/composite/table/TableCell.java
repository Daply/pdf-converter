package pdftohtml.domain.pdf.object.composite.table;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pdftohtml.common.helpers.RectangleHelper.combineTwoRectangles;

@Getter
public class TableCell extends PdfDocumentObject {

    private final List<PdfDocumentObject> objects;

    /**
     * Row order number in a table
     */
    @Setter
    private int rowNumber;

    /**
     * Cell order number in a row
     */
    @Setter
    private int number;

    public TableCell() {
        this.objects = new ArrayList<>();
        this.objectType = PdfDocumentObjectType.TABLE_CELL;
    }

    public void addObject(PdfDocumentObject object) {
        this.objects.add(object);
    }

    public void addAllObjects(List<PdfDocumentObject> objects) {
        this.objects.addAll(objects);
        resolveRectangle();
    }

    public void cleanObjects() {
        this.objects.clear();
        this.rectangle = new FrameworkRectangle();
    }

    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    public void resolveRectangle() {
        AtomicReference<FrameworkRectangle> resultRectangle = new AtomicReference<>();
        objects.forEach(object -> {
            if (resultRectangle.get() == null) {
                resultRectangle.set(object.getRectangle());
            } else {
                resultRectangle.set(combineTwoRectangles(resultRectangle.get(), object.getRectangle()));
            }
        });
        this.rectangle = resultRectangle.get();
    }
}
