package pdftohtml.domain.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrameworkRectangleTest {

    @Test
    void whenSetRectangleCoordinatesThenWidthAndHeightCountedCorrectly() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertEquals(10, rectangle.getWidth());
        assertEquals(5, rectangle.getHeight());
    }

    @Test
    void whenContainsSpecifiedCoordinatesThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(6, 1));
    }

    @Test
    void whenContainsSpecifiedCoordinatesNotInsideThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.contains(4, 1));
    }

    @Test
    void whenContainsItselfThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(rectangle));
    }

    @Test
    void whenContainsTheInnerRectangleThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle innerRectangle = new FrameworkRectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 14, 4);
        assertTrue(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsTheRectangleBiggerWidthThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle innerRectangle = new FrameworkRectangle();
        innerRectangle.setRectangleCoordinates(4, 1, 14, 5);
        assertFalse(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsTheRectangleBiggerHeightThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle innerRectangle = new FrameworkRectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 14, 6);
        assertFalse(rectangle.contains(innerRectangle));
    }

    @Test
    void whenContainsBySpecifiedRectangleCoordinatesThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.contains(6, 1, 9, 3));
    }

    @Test
    void whenContainsBySpecifiedRectangleCoordinatesOutOfBordersThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.contains(6, 1, 10, 5));
    }

    @Test
    void whenContainsByXWithInaccuracyThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.containsByXWithInaccuracy(4, 16, 1.5));
    }

    @Test
    void whenContainsByXWithNotEnoughInaccuracyThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.containsByXWithInaccuracy(4, 16, 0.5));
    }

    @Test
    void whenContainsByYWithInaccuracyThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.containsByYWithInaccuracy(1, 6, 1.5));
    }

    @Test
    void whenContainsByYWithNotEnoughInaccuracyThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.containsByYWithInaccuracy(1, 6, 0.5));
    }

    @Test
    void whenContainsWithXYInaccuracyThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle innerRectangle = new FrameworkRectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 16, 6);
        assertTrue(rectangle.containsWithXYInaccuracies(innerRectangle, 2.5, 2.5));
    }

    @Test
    void whenContainsWithNotEnoughXYInaccuracyThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle innerRectangle = new FrameworkRectangle();
        innerRectangle.setRectangleCoordinates(6, 1, 16, 6);
        assertFalse(rectangle.containsWithXYInaccuracies(innerRectangle, 0.5, 0.5));
    }

    @Test
    void whenIntersectsItselfThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(rectangle));
    }

    @Test
    void whenIntersectsThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle intersectedRectangle = new FrameworkRectangle();
        intersectedRectangle.setRectangleCoordinates(4, 0, 15, 5);
        assertTrue(rectangle.intersects(intersectedRectangle));
    }

    @Test
    void whenNotIntersectsThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        FrameworkRectangle intersectedRectangle = new FrameworkRectangle();
        intersectedRectangle.setRectangleCoordinates(16, 6, 18, 7);
        assertFalse(rectangle.intersects(intersectedRectangle));
    }

    @Test
    void whenIntersectsItselfByCoordinatesThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(5, 0, 10, 5));
    }

    @Test
    void whenIntersectsByCoordinatesThenTrue() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertTrue(rectangle.intersects(4, 3, 10, 5));
    }

    @Test
    void whenNotIntersectsByCoordinatesThenFalse() {
        FrameworkRectangle rectangle = new FrameworkRectangle();
        rectangle.setRectangleCoordinates(5, 0, 15, 5);
        assertFalse(rectangle.intersects(15, 4, 10, 5));
    }
}