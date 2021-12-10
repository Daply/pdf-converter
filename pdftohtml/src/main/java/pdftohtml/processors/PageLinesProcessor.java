package pdftohtml.processors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Globals;
import pdftohtml.common.Properties;
import pdftohtml.domain.framework.Rectangle;
import pdftohtml.domain.pdfdocument.object.process.*;
import pdftohtml.domain.pdfdocument.object.process.complex.Skeleton;
import pdftohtml.domain.pdfdocument.object.process.container.Block;
import pdftohtml.domain.pdfdocument.object.process.container.PageLine;
import pdftohtml.domain.pdfdocument.object.process.template.Divider;
import pdftohtml.helpers.RectangleHelper;
import pdftohtml.helpers.testing.LineObjectsPrinter;
import pdftohtml.helpers.testing.PdfPageDrawer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates skeletons of all page data from dividers border rectangles and lines of the page.
 * Skeleton is a rectangle with inner content, that can have the whole page content, to be a list
 * (list in a text like 1. something 2. something, etc.) and a table (table in a text with rows and
 * cells)
 *
 * @author Daria Pleshchankova
 */
public class PageLinesProcessor {

    private boolean linesTestMode = false;
    private boolean blocksTestMode = true;
    private boolean graphicsTestMode = true;
    private boolean dividersTestMode = false;
    private boolean skeletonsTestMode = false;

    private PDDocument document;

    private List<PageLine> pageLines = new ArrayList<>();
    private List<Divider> dividers = new ArrayList<>();
    private List<Skeleton> skeletons = new ArrayList<>();
    /** helpers */
    private RectangleHelper helper;

    public PageLinesProcessor(PDDocument document) {
        this.document = document;
        this.helper = new RectangleHelper();
    }

    public void processPage(int pageIndex) throws IOException {
        PageObjectsProcessor processor = new PageObjectsProcessor();
        processor.setStartPage(pageIndex);
        processor.setEndPage(pageIndex);
        PDPage page = this.document.getPage(pageIndex - 1);
        processor.setPage(page);
        processor.processPage(page);
        processor.getText(document);
        // get text data blocks
        List<Block> blocks = processor.getBlocks();

        // extract all page graphics
        GraphicsProcessor graphicsProcessor = new GraphicsProcessor();
        graphicsProcessor.processPage(page);
        List<GraphicsObject> graphics =  graphicsProcessor.getGraphicsObjects();

        this.pageLines.addAll(processor.getPageLines());
        this.dividers.addAll(processor.getDividers());

        if (this.linesTestMode) {
            LineObjectsPrinter.printLinesObjects(this.pageLines);
            for (PageLine line : this.pageLines) {
                PdfPageDrawer.drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        line.getRectangle(),
                        Color.BLACK);
            }
        }

        if (this.blocksTestMode) {
            for (Block block : blocks) {
                PdfPageDrawer.drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        block.getContentRectangle(),
                        Color.MAGENTA);
            }
        }

        if (this.graphicsTestMode) {
            for (GraphicsObject graphicsObject : graphics) {
                PdfPageDrawer.drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        graphicsObject.getRectangle(),
                        Color.green);
            }
        }

        if (this.dividersTestMode) {
            for (Divider div : this.dividers) {
                PdfPageDrawer.drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        div.getRectangle(),
                        Color.RED);
            }
        }

//        processSkeletons();

        if (this.skeletonsTestMode) {
            for (Skeleton skeleton : this.skeletons) {
                PdfPageDrawer.drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        skeleton.getRectangle(),
                        Color.GREEN);
            }
        }
    }

    /**
     *  ------------
     *  |  *-------|---  -> * - starting point of rectangle
     *  |  |       |  |
     *  ---|--------  |
     *     ------------
     *
     *  1) Check if image is kind of emoji or small icon,
     *  that can be inserted in a line by checking image height and
     *  lines intersection:
     *  if image height is
     *  2)
     *
     * @param blocks
     * @param graphics
     */
    private void gatherGraphicsToBlocksAndLines(List<Block> blocks, List<GraphicsObject> graphics) {
        for (Block block: blocks) {
            for (GraphicsObject graphicsObject: graphics) {
                boolean rectanglesIntersect = block.getContentRectangle().intersects(graphicsObject.getRectangle());
                boolean rectangleContainsLeftUpperPoint = block.getContentRectangle()
                        .contains(
                                graphicsObject.getRectangle().getMinX(),
                                graphicsObject.getRectangle().getMinY()
                        );
                if (rectanglesIntersect && rectangleContainsLeftUpperPoint) {
                    List<PageLine> intersectedLines = block.getLines().stream()
                            .filter(line ->
                                    line.getRectangle().intersectsHorizontally(graphicsObject.getRectangle()) > Globals.THRESHOLD)
                            .collect(Collectors.toList());
                    if (intersectedLines.isEmpty()) {
                        // find the line after which the line with image will go
                    } else {
                        // find the line that contains image upper left point or the closest line
                    }
                }
            }
        }
    }

    private void processSkeletons() {
        cutCrossingDividers();
        filterOneLineDividers();
        createDividersBorderRectangles();
        createSkeletons();
        resolveInnerSkeletons();
        fillSkeletons();
    }

    /** Fill skeletons with blocks with content, also determining its type (PAGE, LIST, TABLE) */
    private void fillSkeletons() {
        Set<PageLine> linesToDelete = new HashSet<>();
        Set<PdfDocumentObject> objectsInLineToDelete;
        for (Skeleton skeleton : this.skeletons) {
            boolean isListType = true;
            for (Divider firstDivider : skeleton.getDividers()) {
                Block leftBlock = new Block();
                Block rightBlock = new Block();
                int countLeftEmptyLines = 0;
                int countRightEmptyLines = 0;
                for (int lineIndex = skeleton.getNumberOfFirstLine();
                     lineIndex <= skeleton.getNumberOfLastLine();
                     lineIndex++) {
                    PageLine line = this.pageLines.get(lineIndex);
                    PageLine leftSkeletonBlockLine = new PageLine();
                    PageLine rightSkeletonBlockLine = new PageLine();
                    objectsInLineToDelete = new HashSet<>();
                    for (PdfDocumentObject object : line.getObjects()) {
                        if (skeleton
                                .getRectangle()
                                .containsWithXYInaccuracies(
                                        object.getRectangle(), Properties.xInaccuracy, Properties.yInaccuracy)) {
                            if (object.getRectangle().isBeforeHorizontallyWithXInaccuracy(
                                    firstDivider.getRectangle(), Properties.xInaccuracy)) {
                                leftSkeletonBlockLine.addObject(object);
                                leftSkeletonBlockLine.setLineNumber(leftBlock.getLines().size());
                                objectsInLineToDelete.add(object);
                                isListType = isObjectListBullet(object) && isListType;
                            } else if (firstDivider.getRectangle().isBeforeHorizontallyWithXInaccuracy(
                                    firstDivider.getRectangle(), Properties.xInaccuracy)) {
                                rightSkeletonBlockLine.addObject(object);
                                rightSkeletonBlockLine.setLineNumber(rightBlock.getLines().size());
                                objectsInLineToDelete.add(object);
                            }
                        }
                    }
                    deleteObjects(objectsInLineToDelete, line);

                    if (line.getObjects().isEmpty()
                            && line.getLineNumber() != skeleton.getNumberOfFirstLine()) {
                        linesToDelete.add(line);
                    }

                    if (leftSkeletonBlockLine.getObjects().isEmpty()) ++countLeftEmptyLines;

                    if (rightSkeletonBlockLine.getObjects().isEmpty()) ++countRightEmptyLines;

                    leftBlock.addLine(leftSkeletonBlockLine);
                    rightBlock.addLine(rightSkeletonBlockLine);
                }
                if (skeleton.getNumberOfLastLine() - skeleton.getNumberOfFirstLine() + 1
                        != countLeftEmptyLines) skeleton.addSkeletonDataBlock(leftBlock);
                if (skeleton.getNumberOfLastLine() - skeleton.getNumberOfFirstLine() + 1
                        != countRightEmptyLines) skeleton.addSkeletonDataBlock(rightBlock);
            }
            // determine skeleton type LIST
            if (!skeleton.getType().equals(SkeletonType.PAGE) && isListType)
                skeleton.setType(SkeletonType.LIST);

            // insert filled skeleton in line, replacing all objects
            // that this skeleton took
            this.pageLines.get(skeleton.getNumberOfFirstLine()).addObject(skeleton);
        }
        deleteLines(linesToDelete);
    }

    private boolean isObjectListBullet(PdfDocumentObject object) {
        return object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT)
                && ((TextObject) object).getTextObjectType().equals(TextObjectType.LIST_BULLET);
    }

    private void deleteObjects(Set<PdfDocumentObject> objectsInLineToDelete, PageLine line) {
        line.setObjects(
                line.getObjects().stream()
                        .filter(object -> !objectsInLineToDelete.contains(object))
                        .collect(Collectors.toList()));
    }

    private void deleteLines(Set<PageLine> linesToDelete) {
        this.pageLines =
                this.pageLines.stream()
                        .filter(line -> !linesToDelete.contains(line))
                        .collect(Collectors.toList());
    }

    /**
     * Set level of deep to all skeletons (level of deep represents the number of skeletons which
     * contain this one skeleton inside)
     */
    private void resolveInnerSkeletons() {
        for (int i = 0; i < this.skeletons.size(); i++) {
            for (int j = 0; j < this.skeletons.size(); j++) {
                if (i != j) {
                    Skeleton currentSkeleton = this.skeletons.get(i);
                    Skeleton comparedSkeleton = this.skeletons.get(j);
                    if (currentSkeleton.getRectangle()
                            .containsWithXYInaccuracies(
                                    comparedSkeleton.getRectangle(),
                                    Properties.xInaccuracy,
                                    Properties.yInaccuracy)) {
                        comparedSkeleton.setLevel(currentSkeleton.getLevel() + 1);
                    }
                }
            }
        }
        this.skeletons.sort(Skeleton::compareToDesc);
    }

    /**
     * Create skeletons (skeleton represents rectangle area with dividers, which have the same height
     * and take the same lines numbers)
     */
    private void createSkeletons() {
        List<Rectangle> dividersBorderRectangles;
        Set<Integer> usedDividers = new HashSet<>();
        boolean isPage;
        for (int i = 0; i < this.dividers.size(); i++) {
            Skeleton skeleton = new Skeleton();
            isPage = false;
            Divider leftDivider = this.dividers.get(i);
            if (leftDivider.isPageBorder()) isPage = true;
            skeleton.addDivider(leftDivider);
            skeleton.setNumberOfFirstLine(leftDivider.getNumberOfFirstLine());
            skeleton.setNumberOfLastLine(leftDivider.getNumberOfLastLine());
            dividersBorderRectangles = new ArrayList<>();
            dividersBorderRectangles.add(leftDivider.getBorderRectangle());
            if (!usedDividers.contains(i)) {
                for (int j = i + 1; j < this.dividers.size(); j++) {
                    Divider rightDivider = this.dividers.get(j);
                    if (leftDivider.getNumberOfFirstLine() == rightDivider.getNumberOfFirstLine()
                            && leftDivider.getNumberOfLastLine() == rightDivider.getNumberOfLastLine()) {
                        if (rightDivider.isPageBorder()) isPage = true;
                        skeleton.addDivider(rightDivider);
                        dividersBorderRectangles.add(rightDivider.getBorderRectangle());
                        usedDividers.add(j);
                    }
                }
                skeleton.setRectangle(this.helper.combineRectangles(dividersBorderRectangles));
                if (isPage) skeleton.setType(SkeletonType.PAGE);
                this.skeletons.add(skeleton);
            }
        }
    }

    /**
     * Create all the border rectangle for all dividers, depending on if one divider is a border for
     * border rectangle of another divider
     *
     * <p>Example: (d - divider, --- - border rectangle)
     *
     * <p>-------------- | d--------| | d d | | d d | | d--------| | d | --------------
     */
    private void createDividersBorderRectangles() {
        for (int i = 0; i < this.dividers.size(); i++) {
            Divider currentDivider = this.dividers.get(i);
            for (int j = 0; j < this.dividers.size(); j++) {
                if (i != j) {
                    Divider nextDivider = this.dividers.get(j);
                    setDividersBorderRectangle(currentDivider, nextDivider);
                }
            }
        }
    }

    /**
     * Set border dividers rectangle
     *
     * @param currentDivider - first taken divider
     * @param nextDivider - divider that is larger by number of lines than currentDivider
     */
    private void setDividersBorderRectangle(Divider currentDivider, Divider nextDivider) {
        if (currentDivider.isDividerLessOtherDivider(nextDivider)) {
            if (nextDivider.getRectangle().isBeforeHorizontallyWithXInaccuracy(
                    currentDivider.getRectangle(), Properties.xInaccuracy)) {
                // if nextDivider is on the left side of the current divider
                currentDivider.setBorderRectangle(
                        createLeftBorderRectangle(
                                nextDivider.getRectangle(), currentDivider.getBorderRectangle()));
            } else if (currentDivider.getRectangle().isBeforeHorizontallyWithXInaccuracy(
                    currentDivider.getRectangle(), Properties.xInaccuracy)) {
                // if nextDivider is on the right side of the current divider
                currentDivider.setBorderRectangle(
                        createRightBorderRectangle(
                                currentDivider.getBorderRectangle(), nextDivider.getRectangle()));
            }
        }
    }

    /** | --> | | | | <-- | */
    private void cutCrossingDividers() {
        // TODO
    }

    private Rectangle createLeftBorderRectangle(Rectangle rectangle1, Rectangle rectangle2) {
        return new Rectangle(
                rectangle1.getMaxX(),
                rectangle2.getMinY(),
                rectangle2.getMaxX() - rectangle1.getMaxX(),
                rectangle2.getHeight());
    }

    private Rectangle createRightBorderRectangle(Rectangle rectangle1, Rectangle rectangle2) {
        return new Rectangle(
                rectangle1.getMinX(),
                rectangle1.getMinY(),
                rectangle2.getMinX() - rectangle1.getMinX(),
                rectangle1.getHeight());
    }

    /** Filter one line dividers */
    private void filterOneLineDividers() {
        this.dividers =
                this.dividers.stream()
                        .filter(divider -> (divider.getNumberOfLastLine() - divider.getNumberOfFirstLine()) > 1)
                        .collect(Collectors.toList());
    }

    public List<Skeleton> getSkeletons() {
        return skeletons;
    }
}
