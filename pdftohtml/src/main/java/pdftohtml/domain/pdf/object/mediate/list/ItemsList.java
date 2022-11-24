package pdftohtml.domain.pdf.object.mediate.list;

import pdftohtml.domain.pdf.object.mediate.MediateObject;
import pdftohtml.domain.pdf.object.mediate.MediateObjectType;

import java.util.ArrayList;
import java.util.List;

public class ItemsList extends MediateObject {

    private List<ItemsListBullet> listBulletsCharacters;
    private List<ItemsListRowContent> itemsListRowContent;

    public ItemsList() {
        this.itemsListRowContent = new ArrayList<>();
        type = MediateObjectType.LIST;
    }

    public List<ItemsListBullet> getListBulletsCharacters() {
        return listBulletsCharacters;
    }

    public void setListBulletsCharacters(List<ItemsListBullet> listBulletsCharacters) {
        this.listBulletsCharacters = listBulletsCharacters;
    }

    public void addItemListRowContent(ItemsListRowContent itemsListRowContent) {
        this.itemsListRowContent.add(itemsListRowContent);
    }

    public List<ItemsListRowContent> getItemsListRowContent() {
        return itemsListRowContent;
    }

    public void setItemsListRowContent(List<ItemsListRowContent> itemsListRowContent) {
        this.itemsListRowContent = itemsListRowContent;
    }

}
