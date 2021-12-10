package pdftohtml.processors.html;

import pdftohtml.domain.htmltags.HtmlTag;
import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObjectType;
import pdftohtml.domain.pdfdocument.object.middleware.list.ItemsList;
import pdftohtml.domain.pdfdocument.object.middleware.list.ItemsListRowContent;

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
