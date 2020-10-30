package pdf_to_html.processors.creators.objects;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.TextPosition;
import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.TextObject;
import pdf_to_html.entities.pdfdocument.object.process.TextPositionStyleWrapper;
import pdf_to_html.helpers.Rectangle2DHelper;

import java.util.Arrays;
import java.util.Objects;

public class TextObjectsCreationFactory {

    private PDPage page;

    public TextObjectsCreationFactory(PDPage page) {
        this.page = page;
    }

    public PdfDocumentObject create(TextPosition textPosition, TextPositionStyleWrapper wrapper, PDAnnotation link) {
        PdfDocumentObject object = null;
        PdfTextObjectCreator creator;
        if (link != null && isLink(link, textPosition)) {
            creator = new LinkObjectCreator();
            object = creator.create(textPosition, wrapper);
        }
        else {
            creator = new TextObjectCreator();
            object = creator.create(textPosition, wrapper);
        }
        return object;
    }

    public boolean isLink(PDAnnotation link, TextPosition textPosition) {
        Rectangle2DHelper helper = new Rectangle2DHelper();
        Rectangle2D linkRectangle = new Rectangle2D(link.getRectangle().getLowerLeftX(),
                this.page.getCropBox().getHeight() - link.getRectangle().getUpperRightY(),
                link.getRectangle().getWidth(),
                link.getRectangle().getHeight());
        Rectangle2D objectRectangle = new Rectangle2D(textPosition.getX(), textPosition.getY(),
                textPosition.getWidth(),
                textPosition.getHeight());
        return helper.contains(linkRectangle, objectRectangle);
    }

    public boolean equalsByStyle(TextObject o1, TextPosition textPosition, TextPositionStyleWrapper wrapper) {
        return Float.compare(o1.getFontSize(), textPosition.getFontSize()) == 0 &&
                o1.getFontSizePt() == textPosition.getFontSizeInPt() &&
                Float.compare(o1.getFontWeight(), textPosition.getFont().getFontDescriptor().getFontWeight()) == 0 &&
                o1.isUnderlinedText() == wrapper.isUnderlinedText() &&
                o1.isStrikeThroughText() == wrapper.isStrikeThroughText() &&
                o1.isItalicText() == textPosition.getFont().getFontDescriptor().isItalic() &&
                o1.getRotated() == wrapper.getRotated() &&
                Objects.equals(o1.getFontFamily(), textPosition.getFont().getFontDescriptor().getFontFamily()) &&
                Objects.equals(o1.getFontName(), textPosition.getFont().getFontDescriptor().getFontName()) &&
                Arrays.equals(o1.getColor(), convertColor(wrapper.getStrokingColor().getComponents()));
    }

    private int [] convertColor(float[] colorF) {
        int [] color = new int[3];
        if (colorF.length == 3) {
            color[0] = Math.round(colorF[0] * 255.0F);
            color[1] = Math.round(colorF[1] * 255.0F);
            color[2] = Math.round(colorF[2] * 255.0F);
        }
        return color;
    }

}
