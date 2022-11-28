package pdftohtml.processors.pdf.objects.text;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.TextPosition;
import pdftohtml.common.Properties;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.text.TextObject;
import pdftohtml.domain.pdf.object.text.TextPositionStyleWrapper;

import java.util.Arrays;
import java.util.Objects;

import static pdftohtml.helpers.RectangleHelper.createTextPositionRectangle;

public class PdfTextObjectsCreationFactory {

  private PDPage page;

  public PdfTextObjectsCreationFactory(PDPage page) {
    this.page = page;
  }

  public PdfDocumentObject create(
      TextPosition textPosition,
      TextPositionStyleWrapper wrapper,
      PDAnnotation link
  ) {
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
    FrameworkRectangle linkRectangle =
        new FrameworkRectangle(
            link.getRectangle().getLowerLeftX(),
            this.page.getCropBox().getHeight() - link.getRectangle().getUpperRightY(),
            link.getRectangle().getWidth(),
            link.getRectangle().getHeight()
        );
    FrameworkRectangle objectRectangle =
            createTextPositionRectangle(textPosition);
    return linkRectangle.containsWithXYInaccuracies(
            objectRectangle,
            Properties.xInaccuracy,
            Properties.yInaccuracy
    );
  }

  public boolean equalsByStyle(
      TextObject textObject,
      TextPosition textPosition,
      TextPositionStyleWrapper wrapper
  ) {
    return Float.compare(textObject.getFontSize(), textPosition.getFontSize()) == 0
        && textObject.getFontSizePt() == textPosition.getFontSizeInPt()
        && Float.compare(
                textObject.getFontWeight(), textPosition.getFont().getFontDescriptor().getFontWeight()
           ) == 0
        && textObject.isUnderlinedText() == wrapper.isUnderlinedText()
        && textObject.isStrikeThroughText() == wrapper.isStrikeThroughText()
        && textObject.isItalicText() == textPosition.getFont().getFontDescriptor().isItalic()
        && textObject.getRotated() == wrapper.getRotated()
        && Objects.equals(
            textObject.getFontFamily(), textPosition.getFont().getFontDescriptor().getFontFamily())
        && Objects.equals(
            textObject.getFontName(), textPosition.getFont().getFontDescriptor().getFontName())
        && Arrays.equals(textObject.getColor(), convertColor(wrapper.getStrokingColor().getComponents()));
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
