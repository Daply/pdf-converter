package pdftohtml.processors.html;

import pdftohtml.domain.htmltags.HtmlTag;
import pdftohtml.domain.pdf.object.deprecated.MediateObject;
import pdftohtml.domain.pdf.object.deprecated.MediateObjectType;
import pdftohtml.domain.pdf.object.text.TextObject;

import java.util.Stack;

public class ParagraphProcessor extends HtmlTagProcessor {

    private Stack<String> innerTags;

    @Override
    public String process(MediateObject mediateObject) {
        validate(mediateObject);

        this.innerTags = new Stack<>();
//        Text text = (Text) mediateObject;
        StringBuilder content = new StringBuilder();
        content.append(HtmlTag.getParagraphOpen());
//        for (TextObject textObject: text.getTextObjects()) {
//            String tags = openTags(textObject);
//            content.append(tags)
//                   .append(textObject.getText());
//            content.append(closeTags());
//        }
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

    private void validate(MediateObject mediateObject) {
        if (!mediateObject.getType().equals(MediateObjectType.PARAGRAPH))
            throw new IllegalArgumentException("object is not of type PARAGRAPH");
    }

}
