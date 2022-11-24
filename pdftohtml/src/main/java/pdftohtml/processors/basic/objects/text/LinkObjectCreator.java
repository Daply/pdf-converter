package pdftohtml.processors.basic.objects.text;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdf.object.basic.LinkObject;
import pdftohtml.domain.pdf.object.basic.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.basic.TextPositionStyleWrapper;

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
