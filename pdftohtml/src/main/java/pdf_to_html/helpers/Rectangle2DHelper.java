package pdf_to_html.helpers;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import pdf_to_html.entities.framework.Rectangle2D;

import java.util.List;

public class Rectangle2DHelper {

  /**
   * Information for Rectangle2D
   *
   *     minY
   *     minX
   *      ----------
   *      |        |
   *      ----------
   *              maxX
   *              maxY
   */

  @Getter
  @Setter
  private double xThreshold = 2.5f;

  @Getter
  @Setter
  private double yThreshold = 2.5f;

  /**
   * Check if first rectangle contains second one, using current x and y threshold
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true - if rectangle1 contains rectangle2, otherwise false
   */
  public boolean contains(Rectangle2D rectangle1, Rectangle2D rectangle2) {

    if (rectangle1.contains(rectangle2))
      return true;

    double rect1x1 = rectangle1.getMinX();
    double rect1x2 = rectangle1.getMaxX();
    double rect1y1 = rectangle1.getMinY();
    double rect1y2 = rectangle1.getMaxY();
    double rect2x1 = rectangle2.getMinX();
    double rect2x2 = rectangle2.getMaxX();
    double rect2y1 = rectangle2.getMinY();
    double rect2y2 = rectangle2.getMaxY();

    return xCondition(rect1x1, rect1x2, rect2x1, rect2x2) &&
            yCondition(rect1y1, rect1y2, rect2y1, rect2y2);
  }

  public boolean xCondition(double rectangle1XMin, double rectangle1XMax,
                            double rectangle2XMin, double rectangle2XMax) {
    return (rectangle1XMin - xThreshold) <= rectangle2XMin &&
            (rectangle1XMax + xThreshold) >= rectangle2XMax;
  }

  public boolean yCondition(double rectangle1YMin, double rectangle1YMax,
                            double rectangle2YMin, double rectangle2YMax) {
    return (rectangle1YMin - yThreshold) <= rectangle2YMin &&
            (rectangle1YMax + yThreshold) >= rectangle2YMax;
  }

  public boolean xCondition(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return (rectangle1.getMinX() - xThreshold) <= rectangle2.getMinX() &&
            (rectangle1.getMaxX() + xThreshold) >= rectangle2.getMaxX();
  }

  public boolean yCondition(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return (rectangle1.getMinY() - yThreshold) <= rectangle2.getMinY() &&
            (rectangle1.getMaxY() + yThreshold) >= rectangle2.getMaxY();
  }

  /**
   * Check rectangles intersection
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true - if rectangles intersect, otherwise false
   */
  public boolean intersects(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return rectangle1.intersects(rectangle2);
  }

  /**
   *  Check rectangles intersection only by x coordinates
   *  Situations:
   *  1. ------
   *        ------
   *
   *  2. ------
   *            ------
   *
   *  3.  -----
   *      --------
   *
   *  4. -----------
   *        -----
   *
   *  5. -----------
   *           -----
   *
   *  6. -------
   *     -------
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return intersection width
   */
  public float intersectsHorizontally(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return 0f;
    float intersection = 0f;
    float x1 = (float) Math.max(rectangle1.getMinX(), rectangle2.getMinX());
    float x2 = (float) Math.min(rectangle1.getMaxX(), rectangle2.getMaxX());
    if (x2 > x1) {
      intersection = x2 - x1;
    }

    return intersection;
  }

  /**
   *  Check rectangles intersection only by y coordinates
   *  Situations:
   *  1. |
   *     |  |
   *     |  |
   *        |
   *
   *  2. |
   *     |
   *
   *        |
   *        |
   *
   *  3.  |  |
   *      |  |
   *      |
   *
   *  4. |
   *     |  |
   *     |
   *
   *  5. |
   *     |  |
   *     |  |
   *
   *  6. |  |
   *     |  |
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return intersection height
   */
  public float intersectsVertically(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return 0f;
    float intersection = 0f;
    float y1 = (float) Math.max(rectangle1.getMinY(), rectangle2.getMinY());
    float y2 = (float) Math.min(rectangle1.getMaxY(), rectangle2.getMaxY());
    if (y2 > y1) {
      intersection = y2 - y1;
    }

    return intersection;
  }

  /**
   * Unite two rectangles, where width is their intersection
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return new rectangle, which is a unit of two rectangles
   */
  public Rectangle2D uniteTwoRectanglesByXMinimally(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return null;
    float xMin = (float) Math.max(rectangle1.getMinX(), rectangle2.getMinX());
    float xMax = (float) Math.min(rectangle1.getMaxX(), rectangle2.getMaxX());
    float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
    float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
    return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
  }

  /**
   * Checks two rectangles belong to one line
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true, if first rectangles belong to one line
   */
  public boolean areOneLine(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return Math.abs(rectangle1.getMinY() - rectangle2.getMinY()) <= xThreshold &&
            Math.abs(rectangle1.getMaxY() - rectangle2.getMaxY()) <= xThreshold;
  }

  /**
   * Checks if the first rectangle stands before the second rectangle by x coordinate
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true, if first rectangle stands before the second rectangle by x coordinate
   */
  public boolean isBeforeHorizontally(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return rectangle1.getMinX() < rectangle2.getMinX() &&
            rectangle1.getMaxX() <= rectangle2.getMinX() + xThreshold;
  }

  /**
   * Checks if the first rectangle stands before the second rectangle by y coordinate
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true, if first rectangle stands before the second rectangle by y coordinate
   */
  public boolean isBeforeVertically(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return rectangle1.getMinY() < rectangle2.getMinY() &&
            rectangle1.getMaxY() <= rectangle2.getMinY();
  }

  /**
   * Combine all given rectangles to one
   *
   * @param rectangles - list of rectangles
   * @return new rectangle, which is a combination of list of rectangles
   */
  public Rectangle2D combineRectangles(List<Rectangle2D> rectangles) {
    if (rectangles == null) return null;
    if (rectangles.isEmpty()) return Rectangle2D.EMPTY;
    float xMin = (float) Double.POSITIVE_INFINITY;
    float yMin = (float) Double.POSITIVE_INFINITY;
    float xMax = 0;
    float yMax = 0;
    for (Rectangle2D rectangle: rectangles) {
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
    return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
  }

  /**
   * Combine two given rectangles to one
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return new rectangle, which is a combination of two rectangles
   */
  public Rectangle2D combineTwoRectangles(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return null;
    float xMin = (float) Math.min(rectangle1.getMinX(), rectangle2.getMinX());
    float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
    float xMax = (float) Math.max(rectangle1.getMaxX(), rectangle2.getMaxX());
    float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
    return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
  }

  /**
   * Subtract second rectangle from the first one
   * ! Only if the rectangle to subtract contains
   * the same xMin and xMax, or the same yMin, yMax
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return new rectangle, which is a subtraction of two rectangles
   */
  public Rectangle2D subtractRectangle(Rectangle2D rectangle1, Rectangle2D rectangle2) {

//        if () {
//
//        }

    if (rectangle1 == null || rectangle2 == null) return null;
    float xMin = (float) Math.min(rectangle1.getMinX(), rectangle2.getMinX());
    float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
    float xMax = (float) Math.max(rectangle1.getMaxX(), rectangle2.getMaxX());
    float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
    return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
  }

  /**
   * Create Rectangle2D object, which is stands between
   * two rectangles
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return new rectangle, which is a space between these two rectangles
   */
  public Rectangle2D createRectangleBetweenTwoRectangles(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return null;
    if (!isBeforeHorizontally(rectangle1, rectangle2)) {
      return null;
    }
    float xMin = (float) rectangle1.getMaxX();
    float yMin = (float) Math.min(rectangle1.getMinY(), rectangle2.getMinY());
    float xMax = (float) rectangle2.getMinX();
    float yMax = (float) Math.max(rectangle1.getMaxY(), rectangle2.getMaxY());
    return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
  }

  /**
   * Check if space between two rectangles is more or equal than given value
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @param space - given space value
   * @return true, if space between two rectangles is more or equal than given value
   */
  public boolean checkXSpaceBetweenTwoRectangles(
          Rectangle2D rectangle1, Rectangle2D rectangle2, float space) {
    if (rectangle1 == null || rectangle2 == null) return false;
    if (isBeforeHorizontally(rectangle1, rectangle2)) {
      return Math.abs(rectangle1.getMaxX() - rectangle2.getMinX()) >= space;
    }
    return Math.abs(rectangle2.getMaxX() - rectangle1.getMinX()) >= space;
  }

  /**
   * Checks if the first rectangle has almost the same x coordinate start as
   * the second rectangle
   *
   * @param rectangle1 - first rectangle
   * @param rectangle2 - second rectangle
   * @return true, if first rectangle has almost the same x coordinate start
   */
  public boolean haveTheSameStart(Rectangle2D rectangle1, Rectangle2D rectangle2) {
    if (rectangle1 == null || rectangle2 == null) return false;
    return Math.abs(rectangle1.getMinX() - rectangle2.getMinX()) <= xThreshold;
  }

  /**
   * Convert PDRectangle object to Rectangle2D object
   *
   * @param pdRectangle - PDRectangle object
   * @return Rectangle2D object
   */
  public Rectangle2D convertPDRectangleToRectangle2D(PDRectangle pdRectangle) {
    double minX = pdRectangle.getUpperRightX() - pdRectangle.getWidth();
    double minY = pdRectangle.getUpperRightY();
    double width = pdRectangle.getWidth();
    double height = pdRectangle.getHeight();
    return new Rectangle2D(minX, minY, width, height);
  }

}
