package pdftohtml.processors.html;

import pdftohtml.domain.htmltags.HtmlTag;
import pdftohtml.domain.pdf.object.deprecated.MediateObject;
import pdftohtml.domain.pdf.object.deprecated.MediateObjectType;
import pdftohtml.domain.pdf.object.composite.list.ItemsList;
import pdftohtml.domain.pdf.object.composite.list.ItemsListRowContent;

public class ItemsListProcessor extends HtmlTagProcessor {

    @Override
    public String process(MediateObject mediateObject) {
        validate(mediateObject);

        HtmlProcessor processor = new HtmlProcessor();
        StringBuilder content = new StringBuilder();
        ItemsList itemsList = (ItemsList) mediateObject;
        content.append(HtmlTag.getOrderedListOpen());
        for (ItemsListRowContent rowContent: itemsList.getItemsListRowContent()) {
            content.append(HtmlTag.getListElementOpen());
            content.append(processor.process(rowContent.getObjects()));
            content.append(HtmlTag.getListElementClose());
        }
        content.append(HtmlTag.getOrderedListClose());
        return content.toString();
    }

    private void validate(MediateObject mediateObject) {
        if (!mediateObject.getType().equals(MediateObjectType.LIST))
            throw new IllegalArgumentException("object is not of type LIST");
    }

}
