package pdftohtml.processors.basic.objects.dividers;

import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Globals;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.domain.pdf.object.process.container.Block;

import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pdftohtml.helpers.RectangleHelper.checkXSpaceBetweenTwoRectangles;
import static pdftohtml.helpers.RectangleHelper.createRectangleBetweenTwoRectangles;
import static pdftohtml.helpers.testing.PdfPageDrawer.drawRectangle;

/**
 * Class for identifying empty spaces between text,
 * for example in table it can be table border
 */
public class PageObjectsDividersProcessor {

    /**
     * Main document for processing
     */
    private PDDocument document;

    private boolean testMode = true;

    public PageObjectsDividersProcessor(PDDocument document) {
        this.document = document;
    }

    public void findDividersOnPage(int pageIndex, List<Block> blocks) {

        // TODO list_test


        // TODO sort all blocks by x that do not cross by y
        List<Block> sorted = sortBlocks(
                blocks.stream().filter(block ->
                        doesBlockCrossByYWithAnyInList(block, blocks)
                )
        ).collect(Collectors.toList());


//        int counter = 0;
//        for (Block b: sorted) {
//            drawForTest(
//                    pageIndex,
//                    b,
//                    null,
//                    b.getContentRectangle(),
//                    String.valueOf(counter)
//            );
//            counter++;
//        }

        // TODO find dividers between all blocks that cross by y
        getDividers(pageIndex, sorted);

        // TODO cut and merge dividers !!! ex. image_in_line
    }

    private boolean doesBlockCrossByYWithAnyInList(Block block, List<Block> blocks) {
        return blocks.stream().anyMatch(listBlock -> {
            if (!listBlock.equals(block)) {
                return listBlock.getContentRectangle()
                        .intersectsVertically(block.getContentRectangle()) > 0;
            } else {
                return false;
            }
        });
    }

    private Stream<Block> sortBlocks(Stream<Block> blocks) {
        return blocks.sorted(
                Comparator.comparingDouble(o -> o.getContentRectangle().getMinX())
        );
    }

    private void getDividers(int pageIndex, List<Block> blocks) {

        blocks.forEach(block -> {
                for (Block comparingBlock : blocks) {
                    if (block.getContentRectangle()
                            .isBeforeHorizontallyWithXInaccuracy(
                                    comparingBlock.getContentRectangle(),
                                    0
                            )) {
                        if (checkXSpaceBetweenTwoRectangles(
                                block.getContentRectangle(),
                                comparingBlock.getContentRectangle(),
                                Globals.MINIMUM_DIVIDER_WIDTH
                        )) {
                            FrameworkRectangle dividerRectangle =
                                    createRectangleBetweenTwoRectangles(
                                            block.getContentRectangle(),
                                            comparingBlock.getContentRectangle()
                                    );

                            if (dividerRectangle != null) {
                                drawForTest(
                                        pageIndex,
                                        block,
                                        comparingBlock,
                                        dividerRectangle,
                                        null
                                );
                            }
                        }
                        break;
                    }
                }
            }
        );
    }

    private void drawForTest(
            int pageIndex,
            Block block,
            Block comparingBlock,
            FrameworkRectangle dividerRectangle,
            String text
    ) {
        if (testMode) {
//            drawRectangle(
//                    this.document,
//                    this.document.getPages().get(pageIndex - 1),
//                    block.getContentRectangle(),
//                    Color.MAGENTA,
//                    null
//            );
//            drawRectangle(
//                    this.document,
//                    this.document.getPages().get(pageIndex - 1),
//                    comparingBlock.getContentRectangle(),
//                    Color.BLUE,
//                    null
//            );
            drawRectangle(
                    this.document,
                    this.document.getPages().get(pageIndex - 1),
                    dividerRectangle,
                    Color.red,
                    text
            );
        }
    }
}
