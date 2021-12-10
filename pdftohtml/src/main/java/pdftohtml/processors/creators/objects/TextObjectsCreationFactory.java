package pdftohtml.processors.creators.objects;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.TextPosition;
import pdftohtml.common.Properties;
import pdftohtml.domain.framework.Rectangle;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObject;
import pdftohtml.domain.pdfdocument.object.process.TextObject;
import pdftohtml.domain.pdfdocument.object.process.TextPositionStyleWrapper;
import pdftohtml.helpers.RectangleHelper;

import java.util.Arrays;
import java.util.Objects;

public class TextObjectsCreationFactory {

  private PDPage page;

  public TextObjectsCreationFactory(PDPage page) {
    this.page = page;
  }

  public PdfDocumentObject create(
      TextPosition textPosition, TextPositionStyleWrapper wrapper, PDAnnotation link) {
    PdfDocumentObject object = null;
    PdfTextObjectCreator creator;
    if (link != null && isLink(link, textPosition)) {
      creator = new LinkObjectCreator();
      object = creator.create(textPosition, wrapper);
    } else {
      creator = new TextObjectCreator();
      object = creator.create(textPosition, wrapper);
    }
    return object;
  }

  public boolean isLink(PDAnnotation link, TextPosition textPosition) {
    RectangleHelper helper = new RectangleHelper();
    Rectangle linkRectangle =
        new Rectangle(
            link.getRectangle().getLowerLeftX(),
            this.page.getCropBox().getHeight() - link.getRectangle().getUpperRightY(),
            link.getRectangle().getWidth(),
            link.getRectangle().getHeight());
    Rectangle objectRectangle =
        new Rectangle(
            textPosition.getX(),
            textPosition.getY(),
            textPosition.getWidth(),
            textPosition.getHeight());
    return linkRectangle.containsWithXYInaccuracies(
        objectRectangle, Properties.xInaccuracy, Properties.yInaccuracy);
  }

  public boolean equalsByStyle(
      TextObject o1, TextPosition textPosition, TextPositionStyleWrapper wrapper) {
    return Float.compare(o1.getFontSize(), textPosition.getFontSize()) == 0
        && o1.getFontSizePt() == textPosition.getFontSizeInPt()
        && Float.compare(
                o1.getFontWeight(), textPosition.getFont().getFontDescriptor().getFontWeight())
            == 0
        && o1.isUnderlinedText() == wrapper.isUnderlinedText()
        && o1.isStrikeThroughText() == wrapper.isStrikeThroughText()
        && o1.isItalicText() == textPosition.getFont().getFontDescriptor().isItalic()
        && o1.getRotated() == wrapper.getRotated()
        && Objects.equals(
            o1.getFontFamily(), textPosition.getFont().getFontDescriptor().getFontFamily())
        && Objects.equals(
            o1.getFontName(), textPosition.getFont().getFontDescriptor().getFontName())
        && Arrays.equals(o1.getColor(), convertColor(wrapper.getStrokingColor().getComponents()));
  }

  private int[] convertColor(float[] colorF) {
    int[] color = new int[3];
    if (colorF.length == 3) {
      color[0] = Math.round(colorF[0] * 255.0F);
      color[1] = Math.round(colorF[1] * 255.0F);
      color[2] = Math.round(colorF[2] * 255.0F);
    }
    return color;
  }
}
