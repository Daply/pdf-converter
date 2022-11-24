package pdftohtml.processors.basic.objects.text;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdf.object.basic.TextObject;
import pdftohtml.domain.pdf.object.basic.TextPositionStyleWrapper;

public interface PdfTextObjectCreator {

    TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper);

}
