package pdf_to_html.processors.html;

import pdf_to_html.entities.htmltags.HtmlTag;
import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObjectType;
import pdf_to_html.entities.pdfdocument.object.middleware.list.ItemsList;
import pdf_to_html.entities.pdfdocument.object.middleware.list.ItemsListRowContent;

public class ItemsListProcessor extends HtmlTagProcessor {

    @Override
    public String process(MiddlewareObject middlewareObject) {
        validate(middlewareObject);

        HtmlProcessor processor = new HtmlProcessor();
        StringBuilder content = new StringBuilder();
        ItemsList itemsList = (ItemsList) middlewareObject;
        content.append(HtmlTag.getOrderedListOpen());
        for (ItemsListRowContent rowContent: itemsList.getItemslistRowContent()) {
            content.append(HtmlTag.getListElementOpen());
            content.append(processor.process(rowContent.getObjects()));
            content.append(HtmlTag.getListElementClose());
        }
        content.append(HtmlTag.getOrderedListClose());
        return content.toString();
    }

    private void validate(MiddlewareObject middlewareObject) {
        if (!middlewareObject.getType().equals(MiddlewareObjectType.LIST))
            throw new IllegalArgumentException("object is not of type LIST");
    }

}
