package pdftohtml.domain.framework;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Rectangle class describes bounds (border rectangle) of an object
 *
 *     minY
 *     minX
 *      *---------
 *      |        |
 *      ---------*
 *              maxX
 *              maxY
 *
 * (Partial copy of JavaFx Rectangle class)
 */
@Getter
public class Rectangle {

    /**
     * Default empty rectangle
     */
    public static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);

    /**
     * Upper-left corner x coordinate
     */
    private double minX;

    /**
     * Upper-left y coordinate
     */
    private double minY;

    /**
     * Lower-right x coordinate
     */
    private double maxX;

    /**
     * Lower-right y coordinate
     */
    private double maxY;

    private double width;

    private double height;

    public Rectangle() {}

    /**
     * Creates a new instance of {@link Rectangle}.
     *
     * @param minX The x coordinate of the upper-left corner of the {@link Rectangle}
     * @param minY The y coordinate of the upper-left corner of the {@link Rectangle}
     * @param width The width of the {@link Rectangle}
     * @param height The height of the {@link Rectangle}
     */
    public Rectangle(double minX, double minY, double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Both width and height must be >= 0");
        }

        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
        this.maxX = minX + width;
        this.maxY = minY + height;
    }

    /**
     * Creates a new instance of {@link Rectangle}.
     *
     * @param minX The x coordinate of the upper-left corner of the {@link Rectangle}
     * @param minY The y coordinate of the upper-left corner of the {@link Rectangle}
     * @param maxX The x coordinate of the lower-right corner of the {@link Rectangle}
     * @param maxY The y coordinate of the lower-right corner of the {@link Rectangle}
     */
    public void setRectangleCoordinates(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    /**
     * Checks if the specified (x, y) coordinates are inside the boundary
     * of {@link Rectangle}.
     *
     * @param x the specified x coordinate
     * @param y the specified y coordinate
     * @return true if the specified (x, y) coordinates are inside the
     *         boundary of this {@link Rectangle}; false otherwise
     */
    public boolean contains(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /**
     * Checks if the interior of this {@link Rectangle} entirely contains the
     * specified Rectangle {@link Rectangle}.
     *
     * @param rectangle The specified Rectangle
     * @return true if the specified Rectangle is inside the
     *         boundary of this {@link Rectangle}; false otherwise
     */
    public boolean contains(Rectangle rectangle) {
        if (rectangle == null) return false;
        return rectangle.minX >= minX && rectangle.minY >= minY && rectangle.maxX <= maxX && rectangle.maxY <= maxY;
    }

    /**
     * Checks if the interior of this {@link Rectangle} entirely contains the
     * specified rectangular area.
     *
     * @param x the x coordinate of the upper-left corner of the specified
     *          rectangular area
     * @param y the y coordinate of the upper-left corner of the specified
     *          rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return true if the interior of this {@link Rectangle} entirely contains
     *         the specified rectangular area; false otherwise
     */
    public boolean contains(double x, double y, double w, double h) {
        return x >= minX && y >= minY && w <= maxX - x && h <= maxY - y;
    }

    /**
     * Check if this rectangle {@link Rectangle} contains the specified
     * rectangular area with current x and y inaccuracy
     *
     * @param rectangle - specified rectangular area
     * @return true - if rectangle1 contains rectangle2, otherwise false
     */
    public boolean containsWithXYInaccuracies(
            Rectangle rectangle,
            double xInaccuracy,
            double yInaccuracy
    ) {
        if (this.contains(rectangle))
            return true;
        return containsByXWithInaccuracy(rectangle.getMinX(), rectangle.getMaxX(), xInaccuracy) &&
                containsByYWithInaccuracy(rectangle.getMinY(), rectangle.getMaxY(), yInaccuracy);
    }

    /**
     * Checks if the interior of this {@link Rectangle} contains the interior
     * of a specified rectangular area by x coordinate with specified inaccuracy.
     *
     * @param rectangleMinX the x coordinate of the upper-left corner of the specified
     *                      rectangular area
     * @param rectangleMaxX the x coordinate of the upper-left corner of the specified
     *                      rectangular area
     * @param inaccuracy inaccuracy by x
     * @return true if the interior of this {@link Rectangle} contains the interior
     *         of the rectangular area by x coordinate with specified inaccuracy
     */
    public boolean containsByXWithInaccuracy(
            double rectangleMinX,
            double rectangleMaxX,
            double inaccuracy
    ) {
        return (this.minX - inaccuracy) <= rectangleMinX &&
                (this.maxX + inaccuracy) >= rectangleMaxX;
    }

    /**
     * Checks if the interior of this {@link Rectangle} contains the interior
     * of a specified rectangular area by y coordinate with specified inaccuracy.
     *
     * @param rectangleMinY the y coordinate of the upper-left corner of the specified
     *                      rectangular area
     * @param rectangleMaxY the y coordinate of the upper-left corner of the specified
     *                      rectangular area
     * @param inaccuracy inaccuracy by y
     * @return true if the interior of this {@link Rectangle} contains the interior
     *         of the rectangular area by y coordinate with specified inaccuracy
     */
    public boolean containsByYWithInaccuracy(
            double rectangleMinY,
            double rectangleMaxY,
            double inaccuracy
    ) {
        return (this.minY - inaccuracy) <= rectangleMinY &&
                (this.maxY + inaccuracy) >= rectangleMaxY;
    }

    /**
     * Checks if the interior of this {@link Rectangle} intersects the interior
     * of a specified Rectangle.
     *
     * @param rectangle The specified Rectangle
     * @return true if the interior of this {@link Rectangle} and the interior
     *         of the specified Rectangle intersect
     */
    public boolean intersects(Rectangle rectangle) {
        if (rectangle == null) return false;
        return rectangle.maxX > minX && rectangle.maxY > minY && rectangle.minX < maxX && rectangle.minY < maxY;
    }

    /**
     * Checks if the interior of this {@link Rectangle} intersects the interior
     * of a specified rectangular area.
     *
     * @param x the x coordinate of the upper-left corner of the specified
     *          rectangular area
     * @param y the y coordinate of the upper-left corner of the specified
     *          rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return true if the interior of this {@link Rectangle} and the interior
     *         of the rectangular area intersect
     */
    public boolean intersects(double x, double y, double w, double h) {
        return x < maxX && y < maxY && x + w > minX && y + h > minY;
    }

    /**
     * Returns width of the intersection of the interior of
     * this {@link Rectangle} and the interior of a specified
     * rectangular area horizontally.
     *
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
     * @param rectangle specified rectangular area
     * @return intersection width
     */
    public float intersectsHorizontally(Rectangle rectangle) {
        if (rectangle == null) return 0f;
        float intersection = 0f;
        float x1 = (float) Math.max(this.getMinX(), rectangle.getMinX());
        float x2 = (float) Math.min(this.getMaxX(), rectangle.getMaxX());
        if (x2 > x1) {
            intersection = x2 - x1;
        }

        return intersection;
    }

    /**
     * Returns height of the intersection of the interior of
     * this {@link Rectangle} and the interior of a specified
     * rectangular area vertically.
     *
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
     * @param rectangle specified rectangular area
     * @return intersection height
     */
    public float intersectsVertically(Rectangle rectangle) {
        if (rectangle == null) return 0f;
        float intersection = 0f;
        float y1 = (float) Math.max(this.getMinY(), rectangle.getMinY());
        float y2 = (float) Math.min(this.getMaxY(), rectangle.getMaxY());
        if (y2 > y1) {
            intersection = y2 - y1;
        }

        return intersection;
    }

    /**
     * Checks if the this rectangle {@link Rectangle} stands
     * before the specified rectangular area by x coordinate
     *
     * @param rectangle specified rectangular area
     * @return true, if this rectangle stands before
     *         the specified rectangular area by x coordinate
     */
    public boolean isBeforeHorizontallyWithXInaccuracy(Rectangle rectangle, double xInaccuracy) {
        if (rectangle == null) return false;
        return this.getMinX() < rectangle.getMinX() &&
                this.getMaxX() <= rectangle.getMinX() + xInaccuracy;
    }

    /**
     * Checks if the this rectangle {@link Rectangle} stands
     * before the specified rectangular area by y coordinate
     *
     * @param rectangle specified rectangular area
     * @return true, if this rectangle stands before
     *         the specified rectangular area by y coordinate
     */
    public boolean isBeforeVerticallyWithYInaccuracy(Rectangle rectangle, double yInaccuracy) {
        if (rectangle == null) return false;
        return this.getMinY() < rectangle.getMinY() &&
                this.getMaxY() <= rectangle.getMinY() + yInaccuracy;
    }

    /**
     * Checks if the this rectangle {@link Rectangle} stands
     * on the same line horizontally with the
     * specified rectangular area
     *
     * @param rectangle specified rectangular area
     * @return true, if this rectangle stands on the same line
     * horizontally with the specified rectangular area
     */
    public boolean onTheSameLineHorizontallyWithYInaccuracy(Rectangle rectangle, double yInaccuracy) {
        if (rectangle == null) return false;
        return Math.abs(this.getMinY() - rectangle.getMinY()) <= yInaccuracy &&
                Math.abs(this.getMaxY() - rectangle.getMaxY()) <= yInaccuracy;
    }

    /**
     * Cut rectangle by the other rectangle
     *
     * @param rectangle specified rectangular area
     * @return list of rectangles
     */
    public List<Rectangle> cut(Rectangle rectangle, double xInaccuracy) {
        if (Math.abs(this.getMinX() - rectangle.getMinX()) <= xInaccuracy &&
                Math.abs(this.getMaxX() - rectangle.getMaxX()) <= xInaccuracy) {
            return Collections.emptyList();
        }
        float yMin = (float) Math.min(this.getMinY(), rectangle.getMinY());
        float yMax = (float) Math.max(this.getMaxY(), rectangle.getMaxY());
        // if rectangle starts at the same coordinate as this rectangle
        if (Math.abs(this.getMinX() - rectangle.getMinX()) <= xInaccuracy) {
            float xMinLeft = (float) rectangle.getMaxX();
            float xMaxLeft = (float) this.getMaxX();
            Rectangle cutRectangle =
                    new Rectangle(xMinLeft, yMin, xMaxLeft - xMinLeft, yMax - yMin);
            return Collections.singletonList(cutRectangle);
        }
        // if rectangle ends at the same coordinate as this rectangle
        else if (Math.abs(this.getMaxX() - rectangle.getMaxX()) <= xInaccuracy) {
            float xMinLeft = (float) this.getMinX();
            float xMaxLeft = (float) rectangle.getMinX();
            Rectangle cutRectangle =
                    new Rectangle(xMinLeft, yMin, xMaxLeft - xMinLeft, yMax - yMin);
            return Collections.singletonList(cutRectangle);
        }
        // if rectangle stands inside of x range of this rectangle
        else if (this.getMinX() < rectangle.getMinX() && this.getMaxX() > rectangle.getMinX() &&
                this.getMinX() < rectangle.getMaxX() && this.getMaxX() > rectangle.getMaxX()) {
            float xMinLeft = (float) this.getMinX();
            float xMaxLeft = (float) rectangle.getMinX();
            Rectangle leftRectangle =
                    new Rectangle(xMinLeft, yMin, xMaxLeft - xMinLeft, yMax - yMin);
            float xMinRight = (float) rectangle.getMaxX();
            float xMaxRight = (float) this.getMaxX();
            Rectangle rightRectangle =
                    new Rectangle(xMinRight, yMin, xMaxRight - xMinRight, yMax - yMin);
            return Arrays.asList(leftRectangle, rightRectangle);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Rectangle) {
            Rectangle other = (Rectangle) obj;
            return minX == other.minX
                    && minY == other.minY
                    && width == other.width
                    && height == other.height;
        } else return false;
    }

    public double getArea() {
        return width * height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, maxX, maxY, width, height);
    }

    @Override public String toString() {
        return "Rectangle [minX = " + minX
                + ", minY=" + minY
                + ", maxX=" + maxX
                + ", maxY=" + maxY
                + ", width=" + width
                + ", height=" + height
                + "]";
    }
}
