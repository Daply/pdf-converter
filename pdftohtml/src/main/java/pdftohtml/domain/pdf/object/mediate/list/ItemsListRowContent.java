package pdftohtml.domain.pdf.object.mediate.list;

import pdftohtml.domain.pdf.object.mediate.MediateObject;

import java.util.List;

public class ItemsListRowContent {

    private List<MediateObject> objects;

    public ItemsListRowContent() {
    }

    public List<MediateObject> getObjects() {
        return objects;
    }

    public void addObject(MediateObject object) {
        this.objects.add(object);
    }

    public void setObjects(List<MediateObject> objects) {
        this.objects = objects;
    }

}
