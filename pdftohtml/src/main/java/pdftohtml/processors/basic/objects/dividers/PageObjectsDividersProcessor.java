package pdftohtml.processors.basic.objects.dividers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Globals;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.domain.pdf.object.process.container.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pdftohtml.helpers.RectangleHelper.*;
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
    private PDPage page;

    private boolean testSortedMode = false;
    private boolean testDividersMode = true;

    private List<FrameworkRectangle> dividers;

    public PageObjectsDividersProcessor(PDDocument document) {
        this.document = document;
        this.dividers = new ArrayList<>();
    }

    public void findDividersOnPage(int pageIndex, List<Block> blocks) {
        this.page = this.document.getPage(pageIndex - 1);

        // TODO list_test


        // 1. sort all blocks by x that do not cross by y
        List<Block> sorted = sortBlocks(
                blocks.stream().filter(block ->
                        doesBlockCrossByYWithAnyInList(block, blocks)
                )
        ).collect(Collectors.toList());

        drawSortedBlocksRectangles(pageIndex, blocks);

        // 2. find dividers between all blocks that cross by y coordinate
        findDividers(sorted);

        // 3. cut and merge dividers !!! ex. image_in_line
        mergeDividers();
        drawDividersForTest(pageIndex);
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

    private void findDividers(List<Block> blocks) {
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
                                this.dividers.add(dividerRectangle);
                                //drawDividersForTest(pageIndex);
                            }
                        }
                        break;
                    }
                }
            }
        );
    }

    /**
     * Combine all dividers
     */
    private void mergeDividers() {
        List<FrameworkRectangle> resultDividers = new ArrayList<>(this.dividers);
        this.dividers.forEach(
                existingDivider -> {
                    for (FrameworkRectangle comparingDivider: this.dividers) {
                        if (!existingDivider.equals(comparingDivider)) {
                            float intersection = comparingDivider.intersectsHorizontally(existingDivider);
                            if ((intersection >= (0.75 * existingDivider.getWidth())
                                    || intersection >= (0.75 * existingDivider.getWidth()))) {
                               FrameworkRectangle resultDivider =
                                       uniteTwoRectanglesByXMinimally(existingDivider, comparingDivider);
                               resultDividers.remove(existingDivider);
                               resultDividers.remove(comparingDivider);
                               resultDividers.add(resultDivider);
                            }
                        }
                    }

                }
        );
        this.dividers = resultDividers;
    }

    private void drawSortedBlocksRectangles(
            int pageIndex,
            List<Block> sorted
    ) {
        if (testSortedMode) {
            int counter = 0;
            for (Block b : sorted) {
                drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        b.getContentRectangle(),
                        Color.red,
                        String.valueOf(counter)
                );
                counter++;
            }
        }
    }

    private void drawDividersForTest(
            int pageIndex
    ) {
        if (testDividersMode) {
            this.dividers.forEach(divider -> {
                drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        divider,
                        Color.red,
                        null
                );
            });
        }
    }
}
