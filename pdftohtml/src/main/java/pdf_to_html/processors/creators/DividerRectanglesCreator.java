package pdf_to_html.processors.creators;

import org.apache.pdfbox.text.TextPosition;
import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObjectType;
import pdf_to_html.entities.pdfdocument.object.process.TextObject;

import static pdf_to_html.helpers.Globals.EMPTY_SPACE_PATTERN;
import static pdf_to_html.helpers.Globals.MINIMUM_DIVIDER_WIDTH;

public class DividerRectanglesCreator {

    /**
     * Create Rectangle2D object from TextPosition object
     *
     * @param textPosition - given textPosition
     * @return rectangle
     */
    public Rectangle2D createTextPositionRectangle(TextPosition textPosition) {
        float x = textPosition.getX();
        float y = textPosition.getY() - textPosition.getHeight();
        float width = textPosition.getWidth();
        float height = textPosition.getHeight();
        return new Rectangle2D(x, y, width, height);
    }

    /**
     * Create Rectangle2D object from TextPosition object,
     * which is stands before whole page line content
     *
     * @param textPosition - given textPosition
     */
    public Rectangle2D createRectangleBefore(TextPosition textPosition) {
        return createRectangleBefore(createTextPositionRectangle(textPosition));
    }

    /**
     * Create Rectangle2D object from Rectangle2D object,
     * which is stands before whole page line content
     *
     * @param rectangle - given rectangle
     */
    public Rectangle2D createRectangleBefore(Rectangle2D rectangle) {
        float x = 0;
        float y = (float) rectangle.getMinY();
        float width = (float) rectangle.getMinX();
        float height = (float) rectangle.getHeight();
        return new Rectangle2D(x, y, width, height);
    }

    /**
     * Create Rectangle2D object from Rectangle2D object,
     * which is stands after whole page line content
     *
     * @param rectangle - given rectangle
     */
    public Rectangle2D createRectangleAfter(Rectangle2D rectangle, float pageWidth) {
        float x = (float) rectangle.getMaxX();
        float y = (float) rectangle.getMinY();
        float width = pageWidth - x;
        float height = (float) rectangle.getHeight();
        return new Rectangle2D(x, y, width, height);
    }

    /**
     * Create Rectangle2D object, which is stands between
     * two pdf objects
     *
     * @param previousObject - given previous object
     * @param textPosition - given textPosition
     */
    public Rectangle2D createRectangleBetweenObjects(PdfDocumentObject previousObject, TextPosition textPosition) {
        float betweenMinX = (float) previousObject.getRectangle().getMaxX();
        if (previousObject.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT)) {
            if (((TextObject)previousObject).getText().matches(EMPTY_SPACE_PATTERN)) {
                betweenMinX = (float) previousObject.getRectangle().getMinX();
            }
        }
        float betweenMinY = Math.min((float) previousObject.getRectangle().getMinY(), textPosition.getY());
        float betweenWidth = textPosition.getX() - betweenMinX;
        float betweenHeight = Math.max((float) previousObject.getRectangle().getMaxY(), textPosition.getY()) - betweenMinY;
        if (betweenWidth >= MINIMUM_DIVIDER_WIDTH) {
            return new Rectangle2D(betweenMinX, betweenMinY, betweenWidth, betweenHeight);
        }
        return null;
    }

}
