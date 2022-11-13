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
 * Class for identifying empty spaces
 * between blocks of page data,
 * for example in table it can be table border
 *
 * @author Daria Pleshchankova
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
        this.dividers = new ArrayList<>();
        this.page = this.document.getPage(pageIndex - 1);

        // TODO list_test


        // 1. sort all blocks by x that do not cross by y
        List<Block> sorted = sortBlocks(
                blocks.stream().filter(block ->
                        doesBlockCrossByYWithAnyInList(block, blocks)
                )
        ).collect(Collectors.toList());

        drawSortedBlocksRectangles(pageIndex, sorted);

        // 2. find dividers between all blocks that cross by y coordinate,
        // blocks should be sorted by x
        findDividers(sorted);

        // 3. cut and merge dividers !!! ex. image_in_line
        mergeDividers();
        cutDividers(blocks);
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

    /**
     * Find all dividers between blocks of page data,
     * from the very start of the page till the blocks and
     * after the blocks content
     *
     * Example: (|  | - divider)
     * |  | text text text |  |
     * |  | text |  | text |  |
     * |            | text |  |
     * |  | text text text |  |
     *
     * @param sortedBlocksByX - block of page data sorted by x coordinate
     */
    private void findDividers(List<Block> sortedBlocksByX) {
        sortedBlocksByX.forEach(block -> {
                boolean hasBlocksOnTheLeft = false;
                boolean hasBlocksOnTheRight = false;
                for (Block comparingBlock : sortedBlocksByX) {
                    if (block.getContentRectangle()
                            .isBeforeHorizontallyWithXInaccuracy(comparingBlock.getContentRectangle(), 0) &&
                            block.getContentRectangle().intersectsVertically(comparingBlock.getContentRectangle()) > 0 &&
                            !hasBlocksOnTheRight
                    ) {
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
                            }
                        }
                        hasBlocksOnTheRight = true;
                    }
                    if (block.getContentRectangle()
                            .isAfterHorizontallyWithXInaccuracy(comparingBlock.getContentRectangle(), 0) &&
                            block.getContentRectangle().intersectsVertically(comparingBlock.getContentRectangle()) > 0 &&
                            !hasBlocksOnTheLeft) {
                        hasBlocksOnTheLeft = true;
                    }
                }
                if (!hasBlocksOnTheLeft) {
                    FrameworkRectangle rectangle = block.getContentRectangle();
                    FrameworkRectangle leftSidePageRectangle =
                            new FrameworkRectangle(
                                    this.page.getCropBox().getLowerLeftX(),
                                    rectangle.getMinY(),
                                    rectangle.getMinX() - this.page.getCropBox().getLowerLeftX(),
                                    rectangle.getHeight()
                            );
                    this.dividers.add(leftSidePageRectangle);
                }
                if (!hasBlocksOnTheRight) {
                    FrameworkRectangle rectangle = block.getContentRectangle();
                    FrameworkRectangle rightSidePageRectangle =
                            new FrameworkRectangle(
                                    rectangle.getMaxX(),
                                    rectangle.getMinY(),
                                    this.page.getCropBox().getUpperRightX(),
                                    rectangle.getHeight()
                            );
                    this.dividers.add(rightSidePageRectangle);
                }
            }
        );
    }

    /**
     * Combine all dividers
     */
    private void mergeDividers() {
        this.dividers.forEach(divider -> {
            for (FrameworkRectangle comparingDivider: this.dividers) {
                float intersection = divider.intersectsHorizontally(comparingDivider);
                if ((intersection >= (0.55 * divider.getWidth())
                        || intersection >= (0.55 * comparingDivider.getWidth()))) {
                    FrameworkRectangle resultDivider =
                            uniteTwoRectanglesByXMinimally(divider, comparingDivider);
                    divider.setRectangleCoordinates(
                            resultDivider.getMinX(),
                            resultDivider.getMinY(),
                            resultDivider.getMaxX(),
                            resultDivider.getMaxY()
                    );
                }
            }
        });
    }

    private void cutDividers(List<Block> blocks) {
        List<FrameworkRectangle> cutResultDividers = new ArrayList<>();
        this.dividers.forEach(divider -> {
            blocks.forEach(block -> {
                if (divider.intersects(block.getContentRectangle())) {

                    FrameworkRectangle intersectionRectangle =
                            divider.getIntersection(block.getContentRectangle());

                    if (intersectionRectangle.getWidth() > 0 &&
                            intersectionRectangle.getHeight() > 0) {
                        FrameworkRectangle downPartDivider =
                                new FrameworkRectangle(
                                        intersectionRectangle.getMinX(),
                                        intersectionRectangle.getMaxY(),
                                        intersectionRectangle.getWidth(),
                                        divider.getMaxY() - intersectionRectangle.getMaxY()
                                );
                        divider.setRectangleCoordinates(
                                divider.getMinX(),
                                divider.getMinY(),
                                intersectionRectangle.getMaxX(),
                                intersectionRectangle.getMinY()
                        );
                        cutResultDividers.add(downPartDivider);
                    }
                }
            });
        });
        this.dividers.addAll(cutResultDividers);
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
