package pdf_to_html.entities.pdfdocument.object.middleware.list;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;

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
