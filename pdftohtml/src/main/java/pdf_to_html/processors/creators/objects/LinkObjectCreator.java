package pdf_to_html.processors.creators.objects;

import org.apache.pdfbox.text.TextPosition;
import pdf_to_html.entities.pdfdocument.object.process.*;

public class LinkObjectCreator implements PdfTextObjectCreator {

    @Override
    public LinkObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper) {
        return createLink(textPosition, wrapper);
    }

    public LinkObject createLink(TextPosition textPosition, TextPositionStyleWrapper wrapper) {
        LinkObject object = new LinkObject();
        object.addToTextContent(textPosition.getUnicode());
        object.setObjectType(PdfDocumentObjectType.LINK);
        return object;
    }

}
