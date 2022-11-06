package pdftohtml.processors.basic.objects;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdf.object.process.TextObject;
import pdftohtml.domain.pdf.object.process.TextPositionStyleWrapper;

public interface PdfTextObjectCreator {

    TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper);

}
