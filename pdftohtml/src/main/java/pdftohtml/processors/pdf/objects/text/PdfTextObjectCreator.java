package pdftohtml.processors.pdf.objects.text;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdf.object.text.TextObject;
import pdftohtml.domain.pdf.object.text.TextPositionStyleWrapper;

public interface PdfTextObjectCreator {

    TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper);

}
