package pdf_to_html.processors.creators.objects;

import org.apache.pdfbox.text.TextPosition;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObjectType;
import pdf_to_html.entities.pdfdocument.object.process.TextObject;
import pdf_to_html.entities.pdfdocument.object.process.TextPositionStyleWrapper;

public class TextObjectCreator implements PdfTextObjectCreator {

    @Override
    public TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper) {
        return createSimpleText(textPosition, wrapper);
    }

    public TextObject createSimpleText(TextPosition textPosition, TextPositionStyleWrapper wrapper) {
        TextObject object = new TextObject();
        object.setColor(wrapper.getStrokingColor().getComponents());
        object.setFontFamily(textPosition.getFont().getFontDescriptor().getFontFamily());
        object.setFontName(textPosition.getFont().getFontDescriptor().getFontName());
        object.setFontWeight(textPosition.getFont().getFontDescriptor().getFontWeight());
        object.setFontSize(textPosition.getFontSize());
        object.setFontSizePt((int) textPosition.getFontSizeInPt());
        object.setItalicText(textPosition.getFont().getFontDescriptor().isItalic());
        object.setBoldText(wrapper.isBoldText());
        object.setUnderlinedText(wrapper.isUnderlinedText());
        object.setStrikeThroughText(wrapper.isStrikeThroughText());

        object.addToTextContent(textPosition.getUnicode());
        object.setObjectType(PdfDocumentObjectType.SIMPLE_TEXT);
        return object;
    }

}
