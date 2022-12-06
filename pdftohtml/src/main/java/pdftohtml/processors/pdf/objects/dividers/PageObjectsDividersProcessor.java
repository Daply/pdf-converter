package pdftohtml.processors.pdf.objects.dividers;

import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Globals;
import pdftohtml.common.Properties;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.container.Block;
import pdftohtml.domain.pdf.object.template.Divider;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pdftohtml.common.helpers.RectangleHelper.*;
import static pdftohtml.common.helpers.testing.PdfPageDrawer.drawRectangle;

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

    @Getter
    private List<Divider> dividers;
    private List<FrameworkRectangle> dividersRectangles;

    /**
     * Test mode flags
     */
    private boolean testSortedMode = false;
    private boolean testDividersMode = false;

    public PageObjectsDividersProcessor(PDDocument document) {
        this.document = document;
        this.dividers = new ArrayList<>();
        this.dividersRectangles = new ArrayList<>();
    }

    public void findDividersOnPage(int pageIndex, List<Block> blocks) {
        this.dividersRectangles = new ArrayList<>();
        this.page = this.document.getPage(pageIndex - 1);

        // 1. sort all blocks by x that do not cross by y
        List<Block> sorted = sortBlocks(
                blocks.stream().filter(block ->
                        doesBlockCrossByYWithAnyInList(block, blocks)
                )
        ).collect(Collectors.toList());

        drawSortedBlocksRectangles(pageIndex, sorted);

        // 2. find dividers between all blocks that cross by y coordinate,
        //    blocks should be sorted by x
        findDividers(sorted);

        // 3. cut and merge dividers
        mergeDividers();
        cutDividers(blocks);

        // 4. delete copies resulted from previous steps
        //    and filter dividers without any data blocks
        //    from any side
        deleteCopies();
        filterDividers(blocks);

        // 5. create dividers objects list
        this.dividers = this.dividersRectangles.stream()
                .map(d -> new Divider(d, false, false, false))
                .collect(Collectors.toList());

        drawDividersForTest(pageIndex);
    }

    private boolean doesBlockCrossByYWithAnyInList(Block block, List<Block> blocks) {
        return blocks.stream().anyMatch(listBlock -> {
            if (!listBlock.equals(block)) {
                return listBlock.getRectangle()
                        .intersectsVertically(block.getRectangle()) > 0;
            } else {
                return false;
            }
        });
    }

    private Stream<Block> sortBlocks(Stream<Block> blocks) {
        return blocks.sorted(
                Comparator.comparingDouble(o -> o.getRectangle().getMinX())
        );
    }

    /**
     * Find all dividers between blocks of page data,
     * from the very start of the page till the blocks and
     * after the blocks content.
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
                    if (block.getRectangle()
                            .isBeforeHorizontallyWithXInaccuracy(comparingBlock.getRectangle(), 0) &&
                            block.getRectangle().intersectsVertically(comparingBlock.getRectangle()) > 0 &&
                            !hasBlocksOnTheRight
                    ) {
                        if (checkXSpaceBetweenTwoRectangles(
                                block.getRectangle(),
                                comparingBlock.getRectangle(),
                                Globals.MINIMUM_DIVIDER_WIDTH
                        )) {
                            FrameworkRectangle dividerRectangle =
                                    createRectangleBetweenTwoRectangles(
                                            block.getRectangle(),
                                            comparingBlock.getRectangle()
                                    );

                            if (dividerRectangle != null) {
                                this.dividersRectangles.add(dividerRectangle);
                            }
                        }
                        hasBlocksOnTheRight = true;
                    }
                    if (block.getRectangle()
                            .isAfterHorizontallyWithXInaccuracy(comparingBlock.getRectangle(), 0) &&
                            block.getRectangle().intersectsVertically(comparingBlock.getRectangle()) > 0 &&
                            !hasBlocksOnTheLeft) {
                        hasBlocksOnTheLeft = true;
                    }
                }
                if (!hasBlocksOnTheLeft) {
                    FrameworkRectangle rectangle = block.getRectangle();
                    FrameworkRectangle leftSidePageRectangle =
                            new FrameworkRectangle(
                                    this.page.getCropBox().getLowerLeftX(),
                                    rectangle.getMinY(),
                                    rectangle.getMinX() - this.page.getCropBox().getLowerLeftX(),
                                    rectangle.getHeight()
                            );
                    this.dividersRectangles.add(leftSidePageRectangle);
                }
                if (!hasBlocksOnTheRight) {
                    FrameworkRectangle rectangle = block.getRectangle();
                    FrameworkRectangle rightSidePageRectangle =
                            new FrameworkRectangle(
                                    rectangle.getMaxX(),
                                    rectangle.getMinY(),
                                    this.page.getCropBox().getUpperRightX(),
                                    rectangle.getHeight()
                            );
                    this.dividersRectangles.add(rightSidePageRectangle);
                }
            }
        );
    }

    /**
     * Combine all dividers
     */
    private void mergeDividers() {
        this.dividersRectangles.forEach(divider -> {
            for (FrameworkRectangle comparingDivider: this.dividersRectangles) {
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
        this.dividersRectangles.forEach(divider -> {
            blocks.forEach(block -> {
                if (divider.intersects(block.getRectangle())) {

                    FrameworkRectangle intersectionRectangle =
                            divider.getIntersection(block.getRectangle());

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
        this.dividersRectangles.addAll(cutResultDividers);
    }

    /**
     * Delete copied dividers.
     */
    private void deleteCopies() {
         this.dividersRectangles = new ArrayList<>(new HashSet<>(this.dividersRectangles));
    }

    /**
     * Filter out dividers that do not contain
     * any data blocks on pne of their side.
     *
     * @param blocks - blocks of page data
     */
    private void filterDividers(List<Block> blocks) {
        this.dividersRectangles = this.dividersRectangles.stream().filter(d -> {
            boolean hasBlocksOnTheLeft = blocks.stream().anyMatch(b ->
                    b.getRectangle()
                            .isBeforeHorizontallyWithXInaccuracy(d, Properties.xInaccuracy)
            );
            boolean hasBlocksOnTheRight = blocks.stream().anyMatch(b ->
                    b.getRectangle()
                            .isAfterHorizontallyWithXInaccuracy(d, Properties.xInaccuracy)
            );
            return hasBlocksOnTheLeft && hasBlocksOnTheRight;
        }
        ).collect(Collectors.toList());
        System.out.println();
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
                        b.getRectangle(),
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
            this.dividersRectangles.forEach(divider -> {
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
