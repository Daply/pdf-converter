package pdf_to_html.processors.creators.objects;

import org.apache.pdfbox.text.TextPosition;
import pdf_to_html.entities.pdfdocument.object.process.*;

public interface PdfTextObjectCreator {

    TextObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper);

}
