package pdftohtml.domain.pdfdocument.object.process.container;

import org.junit.jupiter.api.Test;
import pdftohtml.TestUtils;
import pdftohtml.domain.framework.Rectangle;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockTest {

    @Test
    void whenAddLineToTheEndOfTheListThenLineAddedAndBlockRectangleInstantiated() {
        Block block = new Block();
        block.setLines(createPageLinesList());

        PageLine newLine = new PageLine();
        // line should be inserted on the second place in lines list
        newLine.setRectangle(new Rectangle(0, 21, 10, 5));
        block.addLine(newLine);

        assertEquals(new Rectangle(0, 0, 10, 26), block.getBorderRectangle());
        assertEquals(4, block.getLines().size());
        assertEquals(newLine.getRectangle(), block.getLines().get(3).getRectangle());
    }

    @Test
    void whenAddLineToTheSecondPlaceOfTheListThenLineAddedAndBlockRectangleInstantiated() {
        Block block = new Block();
        block.setLines(createPageLinesList());

        PageLine newLine = new PageLine();
        // line should be inserted on the second place in lines list
        newLine.setRectangle(new Rectangle(0, 6, 10, 1));
        block.addLine(newLine);

        assertEquals(new Rectangle(0, 0, 10, 19), block.getBorderRectangle());
        assertEquals(4, block.getLines().size());
        assertEquals(newLine.getRectangle(), block.getLines().get(1).getRectangle());
    }

    @Test
    void whenSetLinesThenBlockLinesSetAndBlockRectangleInstantiated() {
        Block block = new Block();
        block.setLines(createPageLinesList());
        assertEquals(new Rectangle(0, 0, 10, 19), block.getBorderRectangle());
    }

    private List<PageLine> createPageLinesList() {
        List<Rectangle> rectangles = Arrays.asList(
                new Rectangle(0, 0, 10, 5),
                new Rectangle(0, 7, 10, 5),
                new Rectangle(0, 14, 10, 5)
        );
        return TestUtils.createMockPageLinesList(rectangles);
    }
}