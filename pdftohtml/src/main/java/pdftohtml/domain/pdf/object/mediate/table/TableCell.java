package pdftohtml.domain.pdf.object.mediate.table;

import pdftohtml.domain.pdf.object.mediate.MediateObject;

import java.util.ArrayList;
import java.util.List;

public class TableCell {

    private List<MediateObject> objects;

    public TableCell() {
        this.objects = new ArrayList<>();
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
