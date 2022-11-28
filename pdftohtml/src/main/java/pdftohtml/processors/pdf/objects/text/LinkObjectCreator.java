package pdftohtml.processors.pdf.objects.text;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdf.object.text.LinkObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.text.TextPositionStyleWrapper;

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
