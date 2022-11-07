package pdftohtml.domain.pdf.object.middleware.list;

import pdftohtml.domain.pdf.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdf.object.middleware.MiddlewareObjectType;

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
