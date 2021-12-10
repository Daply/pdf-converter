package pdftohtml.processors.creators.objects;

import org.apache.pdfbox.text.TextPosition;
import pdftohtml.domain.pdfdocument.object.process.*;

public interface PdfTextObjectCreator {

    TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper);

}
