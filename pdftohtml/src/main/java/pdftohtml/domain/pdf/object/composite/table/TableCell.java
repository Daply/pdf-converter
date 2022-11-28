package pdftohtml.domain.pdf.object.composite.table;

import lombok.Getter;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pdftohtml.helpers.RectangleHelper.combineTwoRectangles;

public class TableCell extends PdfDocumentObject {

    @Getter
    private List<PdfDocumentObject> objects;

    public TableCell() {
        this.objects = new ArrayList<>();
        this.objectType = PdfDocumentObjectType.TABLE_CELL;
    }

    public void addObject(PdfDocumentObject object) {
        this.objects.add(object);
    }

    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    public void resolveRectangle() {
        AtomicReference<FrameworkRectangle> resultRectangle = new AtomicReference<>(new FrameworkRectangle());
        objects.forEach(object -> {
           resultRectangle.set(combineTwoRectangles(resultRectangle.get(), object.getRectangle()));
        });
        this.rectangle = resultRectangle.get();
    }
}
