package pdf_to_html.entities.pdfdocument.object.middleware.list;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObjectType;

import java.util.ArrayList;
import java.util.List;

public class ItemsList extends MiddlewareObject {

    private List<ItemsListRowContent> itemslistRowContent;

    public ItemsList() {
        this.itemslistRowContent = new ArrayList<>();
        type = MiddlewareObjectType.LIST;
    }

    public void addItemListRowContent(ItemsListRowContent itemsListRowContent) {
        this.itemslistRowContent.add(itemsListRowContent);
    }

    public List<ItemsListRowContent> getItemslistRowContent() {
        return itemslistRowContent;
    }

    public void setItemslistRowContent(List<ItemsListRowContent> itemslistRowContent) {
        this.itemslistRowContent = itemslistRowContent;
    }

}
