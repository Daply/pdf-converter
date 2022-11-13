package pdftohtml.domain.pdf.object.mediate.table;

import pdftohtml.domain.pdf.object.mediate.MiddlewareObject;

import java.util.ArrayList;
import java.util.List;

public class TableCell {

    private List<MiddlewareObject> objects;

    public TableCell() {
        this.objects = new ArrayList<>();
    }

    public List<MiddlewareObject> getObjects() {
        return objects;
    }

    public void addObject(MiddlewareObject object) {
        this.objects.add(object);
    }

    public void setObjects(List<MiddlewareObject> objects) {
        this.objects = objects;
    }
}
