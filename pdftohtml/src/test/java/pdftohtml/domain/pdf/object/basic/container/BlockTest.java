package pdftohtml.domain.pdf.object.basic.container;

import org.junit.jupiter.api.Test;
import pdftohtml.TestUtils;
import pdftohtml.domain.common.FrameworkRectangle;

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
        newLine.setRectangle(new FrameworkRectangle(0, 21, 10, 5));
        block.addLine(newLine);

        assertEquals(new FrameworkRectangle(0, 0, 10, 26), block.getContentRectangle());
        assertEquals(4, block.getLines().size());
        assertEquals(newLine.getRectangle(), block.getLines().get(3).getRectangle());
    }

    @Test
    void whenAddLineToTheSecondPlaceOfTheListThenLineAddedAndBlockRectangleInstantiated() {
        Block block = new Block();
        block.setLines(createPageLinesList());

        PageLine newLine = new PageLine();
        // line should be inserted on the second place in lines list
        newLine.setRectangle(new FrameworkRectangle(0, 6, 10, 1));
        block.addLine(newLine);

        assertEquals(new FrameworkRectangle(0, 0, 10, 19), block.getContentRectangle());
        assertEquals(4, block.getLines().size());
        assertEquals(newLine.getRectangle(), block.getLines().get(1).getRectangle());
    }

    @Test
    void whenSetLinesThenBlockLinesSetAndBlockRectangleInstantiated() {
        Block block = new Block();
        block.setLines(createPageLinesList());
        assertEquals(new FrameworkRectangle(0, 0, 10, 19), block.getContentRectangle());
    }

    private List<PageLine> createPageLinesList() {
        List<FrameworkRectangle> rectangles = Arrays.asList(
                new FrameworkRectangle(0, 0, 10, 5),
                new FrameworkRectangle(0, 7, 10, 5),
                new FrameworkRectangle(0, 14, 10, 5)
        );
        return TestUtils.createMockPageLinesList(rectangles);
    }
}