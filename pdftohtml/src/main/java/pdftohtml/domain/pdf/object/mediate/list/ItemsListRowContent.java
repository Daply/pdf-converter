package pdftohtml.domain.pdf.object.mediate.list;

import pdftohtml.domain.pdf.object.mediate.MiddlewareObject;

import java.util.List;

public class ItemsListRowContent {

    private List<MiddlewareObject> objects;

    public ItemsListRowContent() {
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
