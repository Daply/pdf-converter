package pdftohtml.processors.html;

import pdftohtml.domain.htmltags.HtmlTag;
import pdftohtml.domain.pdf.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdf.object.middleware.MiddlewareObjectType;
import pdftohtml.domain.pdf.object.middleware.TextParagraph;
import pdftohtml.domain.pdf.object.process.TextObject;

import java.util.Stack;

public class ParagraphProcessor extends HtmlTagProcessor {

    private Stack<String> innerTags;

    @Override
    public String process(MiddlewareObject middlewareObject) {
        validate(middlewareObject);

        this.innerTags = new Stack<>();
        TextParagraph textParagraph = (TextParagraph) middlewareObject;
        StringBuilder content = new StringBuilder();
        content.append(HtmlTag.getParagraphOpen());
        for (TextObject textObject: textParagraph.getTextObjects()) {
            String tags = openTags(textObject);
            content.append(tags)
                   .append(textObject.getText());
            content.append(closeTags());
        }
        content.append(HtmlTag.getParagraphClose());
        return content.toString();
    }

    private String openTags(TextObject textObject) {
        StringBuilder content = new StringBuilder();
        if (textObject.isItalicText()) {
            content.append(HtmlTag.getItalicOpen());
            this.innerTags.push(HtmlTag.getItalicClose());
        }
        if (textObject.isBoldText()) {
            content.append(HtmlTag.getBoldOpen());
            this.innerTags.push(HtmlTag.getBoldClose());
        }
        if (textObject.isUnderlinedText()) {
            content.append(HtmlTag.getUnderlinedOpen());
            this.innerTags.push(HtmlTag.getUnderlinedClose());
        }
        if (textObject.isStrikeThroughText()) {
            content.append(HtmlTag.getStrikeThroughOpen());
            this.innerTags.push(HtmlTag.getStrikeThroughClose());
        }
        return content.toString();
    }

    private String closeTags() {
        StringBuilder content = new StringBuilder();
        while (!this.innerTags.empty()) {
            content.append(this.innerTags.pop());
        }
        return content.toString();
    }

    private void validate(MiddlewareObject middlewareObject) {
        if (!middlewareObject.getType().equals(MiddlewareObjectType.PARAGRAPH))
            throw new IllegalArgumentException("object is not of type PARAGRAPH");
    }

}
