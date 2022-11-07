package pdftohtml.helpers;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.TextPosition;
import pdftohtml.common.Properties;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.domain.pdf.object.process.PdfDocumentObject;
import pdftohtml.domain.pdf.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.process.TextObject;

import java.util.List;

import static pdftohtml.common.Globals.MINIMUM_DIVIDER_WIDTH;

public class RectangleHelper {

    /**
     * Unite two rectangles {@link FrameworkRectangle} objects,
     * where width is their intersection
     *
     * @param rectangle1 first rectangle {@link FrameworkRectangle}
     * @param rectangle2 second rectangle {@link FrameworkRectangle}
     * @return new rectangle {@link FrameworkRectangle},
     *         which is a unit of two rectangles
     */
    public static FrameworkRectangle getIntersectionRectangle(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
        if (rectangle1 == null || rectangle2 == null) return null;
        float xMin = (float) Math.max(rectangle1.getMinX(), rectangle2.getMinX());
        float xMax = (float) Math.min(rectangle1.getMaxX(), rectangle2.getMaxX());
        float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
        float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Unite two rectangles {@link FrameworkRectangle} objects,
     * where width is their intersection
     *
     * @param rectangle1 first rectangle {@link FrameworkRectangle}
     * @param rectangle2 second rectangle {@link FrameworkRectangle}
     * @return new rectangle {@link FrameworkRectangle},
     *         which is a unit of two rectangles
     */
    public static FrameworkRectangle uniteTwoRectanglesByXMinimally(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
        if (rectangle1 == null || rectangle2 == null) return null;
        float xMin = (float) Math.min(rectangle1.getMinX(), rectangle2.getMinX());
        float xMax = (float) Math.min(rectangle1.getMaxX(), rectangle2.getMaxX());
        float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
        float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Combine all given rectangles {@link FrameworkRectangle} objects to one
     *
     * @param rectangles list of rectangles {@link FrameworkRectangle} objects
     * @return new rectangle {@link FrameworkRectangle} object,
     *         which is a combination of list of
     *         rectangles {@link FrameworkRectangle}
     */
    public static FrameworkRectangle combineRectangles(List<FrameworkRectangle> rectangles) {
        if (rectangles == null) return null;
        if (rectangles.isEmpty()) return new FrameworkRectangle();
        float xMin = (float) Double.POSITIVE_INFINITY;
        float yMin = (float) Double.POSITIVE_INFINITY;
        float xMax = 0;
        float yMax = 0;
        for (FrameworkRectangle rectangle: rectangles) {
            if (rectangle.getMinX() < xMin) {
                xMin = (float) rectangle.getMinX();
            }
            if (rectangle.getMinY() < yMin) {
                yMin = (float) rectangle.getMinY();
            }
            if (rectangle.getMaxX() > xMax) {
                xMax = (float) rectangle.getMaxX();
            }
            if (rectangle.getMaxY() > yMax) {
                yMax = (float) rectangle.getMaxY();
            }
        }
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Combine two given rectangles {@link FrameworkRectangle} objects to one
     *
     * @param rectangle1 first rectangle {@link FrameworkRectangle}
     * @param rectangle2 second rectangle {@link FrameworkRectangle}
     * @return new rectangle {@link FrameworkRectangle} object,
     *         which is a combination of two
     *         rectangles {@link FrameworkRectangle}
     */
    public static FrameworkRectangle combineTwoRectangles(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
        if (rectangle1 == null || rectangle2 == null) return null;
        float xMin = (float) Math.min(rectangle1.getMinX(), rectangle2.getMinX());
        float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
        float xMax = (float) Math.max(rectangle1.getMaxX(), rectangle2.getMaxX());
        float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Subtract the second rectangle {@link FrameworkRectangle} from the first one
     *
     * ! Only if the rectangle {@link FrameworkRectangle} object
     * to subtract contains the same xMin and xMax,
     * or the same yMin, yMax
     *
     * @param rectangle1 first rectangle {@link FrameworkRectangle}
     * @param rectangle2 second rectangle {@link FrameworkRectangle}
     * @return new rectangle {@link FrameworkRectangle} object,
     *         which is a subtraction of two rectangles
     */
    public static FrameworkRectangle subtractRectangle(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
        if (rectangle1 == null || rectangle2 == null) return null;
        float xMin = (float) Math.min(rectangle1.getMinX(), rectangle2.getMinX());
        float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
        float xMax = (float) Math.max(rectangle1.getMaxX(), rectangle2.getMaxX());
        float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Create rectangle {@link FrameworkRectangle} object, which stands between
     * two rectangles
     *
     * @param rectangle1 first rectangle
     * @param rectangle2 second rectangle
     * @return new rectangle, which is a space between these two rectangles
     */
    public static FrameworkRectangle createRectangleBetweenTwoRectangles(
            FrameworkRectangle rectangle1,
            FrameworkRectangle rectangle2
    ) {
        if (rectangle1 == null || rectangle2 == null) return null;
        if (!rectangle1.isBeforeHorizontallyWithXInaccuracy(rectangle2, Properties.xInaccuracy)) {
            return null;
        }
        float xMin = (float) rectangle1.getMaxX();
        float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
        float xMax = (float) rectangle2.getMinX();
        float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
        return new FrameworkRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * Check if space between two rectangles is more or equal than given value
     *
     * TODO
     *
     * @param rectangle1 first rectangle
     * @param rectangle2 second rectangle
     * @param space - given space value
     * @return true, if space between two rectangles is more or equal than given value
     */
    public static boolean checkXSpaceBetweenTwoRectangles(
            FrameworkRectangle rectangle1, FrameworkRectangle rectangle2, float space
    ) {
        if (rectangle1 == null || rectangle2 == null) return false;
        if (rectangle1.isBeforeHorizontallyWithXInaccuracy(rectangle2, Properties.xInaccuracy)) {
            return rectangle2.getMinX() - rectangle1.getMaxX() >= space;
        }
        return rectangle1.getMinX() - rectangle2.getMaxX() >= space;
    }

    /**
     * Check if space between two rectangles is more or equal than given value
     *
     * TODO
     *
     * @param rectangle1 first rectangle
     * @param rectangle2 second rectangle
     * @param space - given space value
     * @return true, if space between two rectangles is more or equal than given value
     */
    public static boolean spaceByYBetweenTwoRectanglesIsMore(
            FrameworkRectangle rectangle1, FrameworkRectangle rectangle2, float space
    ) {
        if (rectangle1 == null || rectangle2 == null) return false;
        if (rectangle1.isBeforeVerticallyWithYInaccuracy(rectangle2, Properties.xInaccuracy)) {
            return rectangle2.getMinY() - rectangle1.getMaxY() >= space;
        }
        return rectangle1.getMinY() - rectangle2.getMaxY() >= space;
    }

    /**
     * Checks if the first rectangle has the same xmin coordinate with
     * specified inaccuracy as the second rectangle
     *
     * @param rectangle1 first rectangle
     * @param rectangle2 second rectangle
     * @return true, if first rectangle has almost the same x coordinate start
     */
    public static boolean haveTheSameStart(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
        if (rectangle1 == null || rectangle2 == null) return false;
        return Math.abs(rectangle1.getMinX() - rectangle2.getMinX()) <= Properties.xInaccuracy;
    }

    /**
     * Convert PDRectangle {@link PDRectangle} object
     * to Rectangle {@link FrameworkRectangle} object
     *
     * @param pdRectangle PDRectangle {@link PDRectangle} object
     * @return Rectangle {@link FrameworkRectangle} object
     */
    public static FrameworkRectangle convertPDRectangleToRectangle(PDRectangle pdRectangle) {
        double minX = pdRectangle.getUpperRightX() - pdRectangle.getWidth();
        double minY = pdRectangle.getUpperRightY();
        double width = pdRectangle.getWidth();
        double height = pdRectangle.getHeight();
        return new FrameworkRectangle(minX, minY, width, height);
    }

    /**
     * Create Rectangle2D object from TextPosition object
     *
     * @param textPosition - given textPosition
     * @return rectangle
     */
    public static FrameworkRectangle createTextPositionRectangle(TextPosition textPosition) {
        float x = textPosition.getX();
        float y = textPosition.getY() - textPosition.getHeight();
        float width = textPosition.getWidth();
        float height = textPosition.getHeight();
        return new FrameworkRectangle(x, y, width, height);
    }

    /**
     * Create Rectangle2D object from TextPosition object,
     * which is stands before whole page line content
     *
     * @param textPosition - given textPosition
     */
    public static FrameworkRectangle createRectangleBefore(TextPosition textPosition) {
        return createRectangleBefore(createTextPositionRectangle(textPosition));
    }

    /**
     * Create Rectangle2D object from Rectangle2D object,
     * which is stands before whole page line content
     *
     * @param rectangle - given rectangle
     */
    public static FrameworkRectangle createRectangleBefore(FrameworkRectangle rectangle) {
        float x = 0;
        float y = (float) rectangle.getMinY();
        float width = (float) rectangle.getMinX();
        float height = (float) rectangle.getHeight();
        return new FrameworkRectangle(x, y, width, height);
    }

    /**
     * Create Rectangle2D object from Rectangle2D object,
     * which is stands after whole page line content
     *
     * @param rectangle - given rectangle
     */
    public static FrameworkRectangle createRectangleAfter(FrameworkRectangle rectangle, float pageWidth) {
        float x = (float) rectangle.getMaxX();
        float y = (float) rectangle.getMinY();
        float width = pageWidth - x;
        float height = (float) rectangle.getHeight();
        return new FrameworkRectangle(x, y, width, height);
    }

    /**
     * Create Rectangle2D object, which is stands between
     * two pdf objects
     *
     * @param previousObject - given previous object
     * @param textPosition - given textPosition
     */
    public static FrameworkRectangle createRectangleBetweenObjects(PdfDocumentObject previousObject, TextPosition textPosition) {
        float betweenMinX = (float) previousObject.getRectangle().getMaxX();
        if (previousObject.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) &&
                ((TextObject)previousObject).getText().isBlank()) {
            betweenMinX = (float) previousObject.getRectangle().getMinX();
        }
        float betweenMinY = Math.min((float) previousObject.getRectangle().getMinY(), textPosition.getY());
        float betweenWidth = textPosition.getX() - betweenMinX;
        float betweenHeight = Math.max((float) previousObject.getRectangle().getMaxY(), textPosition.getY()) - betweenMinY;
        if (betweenWidth >= MINIMUM_DIVIDER_WIDTH) {
            return new FrameworkRectangle(betweenMinX, betweenMinY, betweenWidth, betweenHeight);
        }
        return null;
    }
}
