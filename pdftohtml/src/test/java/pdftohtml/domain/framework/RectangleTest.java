package pdftohtml.domain.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    @Test
    void whenSetRectangleCoordinatesThenWidthAndHeightCountedCorrectly() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertEquals(10, rectangle.getWidth());
        assertEquals(5, rectangle.getHeight());
    }

    @Test
    void whenContainsSpecifiedCoordinatesThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(6, 1));
    }

    @Test
    void whenContainsSpecifiedCoordinatesNotInsideThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.contains(4, 1));
    }

    @Test
    void whenContainsItselfThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(rectangle));
    }

    @Test
    void whenContainsTheInnerRectangleThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle innerRectangle = new Rectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 14, 4);
        assertTrue(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsTheRectangleBiggerWidthThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle innerRectangle = new Rectangle();
        innerRectangle.setRectangleCoordinates(4, 1, 14, 5);
        assertFalse(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsTheRectangleBiggerHeightThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle innerRectangle = new Rectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 14, 6);
        assertFalse(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsBySpecifiedRectangleCoordinatesThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(6, 1, 9, 3));
    }

    @Test
    void whenContainsBySpecifiedRectangleCoordinatesOutOfBordersThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.contains(6, 1, 10, 5));
    }

    @Test
    void whenContainsByXWithInaccuracyThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.containsByXWithInaccuracy(4, 16, 1.5));
    }

    @Test
    void whenContainsByXWithNotEnoughInaccuracyThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.containsByXWithInaccuracy(4, 16, 0.5));
    }

    @Test
    void whenContainsByYWithInaccuracyThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.containsByYWithInaccuracy(1, 6, 1.5));
    }

    @Test
    void whenContainsByYWithNotEnoughInaccuracyThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.containsByYWithInaccuracy(1, 6, 0.5));
    }

    @Test
    void whenContainsWithXYInaccuracyThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle innerRectangle = new Rectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 16, 6);
        assertTrue(rectangle.containsWithXYInaccuracies(innerRectangle, 2.5, 2.5));
    }

    @Test
    void whenContainsWithNotEnoughXYInaccuracyThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle innerRectangle = new Rectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 16, 6);
        assertFalse(rectangle.containsWithXYInaccuracies(innerRectangle, 0.5, 0.5));
    }

    @Test
    void whenIntersectsItselfThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(rectangle));
    }

    @Test
    void whenIntersectsThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle intersectedRectangle = new Rectangle();
        intersectedRectangle.setRectangleCoordinates(4, 0, 15, 5);
        assertTrue(rectangle.intersects(intersectedRectangle));
    }

    @Test
    void whenNotIntersectsThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        Rectangle intersectedRectangle = new Rectangle();
        intersectedRectangle.setRectangleCoordinates(16, 6, 18, 7);
        assertFalse(rectangle.intersects(intersectedRectangle));
    }

    @Test
    void whenIntersectsItselfByCoordinatesThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(5, 0, 10, 5));
    }

    @Test
    void whenIntersectsByCoordinatesThenTrue() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(4, 3, 10, 5));
    }

    @Test
    void whenNotIntersectsByCoordinatesThenFalse() {
        Rectangle rectangle = new Rectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.intersects(15, 4, 10, 5));
    }
}