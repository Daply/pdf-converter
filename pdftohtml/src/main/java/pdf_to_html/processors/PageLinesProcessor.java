package pdf_to_html.processors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.entities.pdfdocument.object.process.*;
import pdf_to_html.entities.pdfdocument.object.process.complex.Skeleton;
import pdf_to_html.entities.pdfdocument.object.process.container.Block;
import pdf_to_html.entities.pdfdocument.object.process.container.PageLine;
import pdf_to_html.entities.pdfdocument.object.process.template.Divider;
import pdf_to_html.helpers.Rectangle2DHelper;
import pdf_to_html.helpers.testing.LineObjectsPrinter;
import pdf_to_html.helpers.testing.PdfPageDrawer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Pleshchankova Daria
 *
 * Creates skeletons of all page data from
 * dividers border rectangles and lines of
 * the page. Skeleton is a rectangle with
 * inner content, that can have the whole
 * page content, to be a list (list in a text
 * like 1. something 2. something, etc.) and
 * a table (table in a text with rows and cells)
 */
public class PageLinesProcessor {

    private boolean linesTestMode = false;
    private boolean dividersTestMode = false;
    private boolean skeletonsTestMode = false;

    private PDDocument document;

    private List<PageLine> pageLines = new ArrayList<>();
    private List<Divider> dividers = new ArrayList<>();
    private List<Skeleton> skeletons = new ArrayList<>();
    /**
     * helpers
     */
    private Rectangle2DHelper helper;

    public PageLinesProcessor(PDDocument document) {
        this.document = document;
        this.helper = new Rectangle2DHelper();
    }

    public void processPage(int pageIndex) throws IOException {
        PageObjectsProcessor processor = new PageObjectsProcessor();
        processor.setStartPage(pageIndex);
        processor.setEndPage(pageIndex);
        PDPage page = this.document.getPage(pageIndex - 1);
        processor.setPage(page);
        processor.processPage(page);
        processor.getText(document);
        List<Block> blocks = processor.getBlocks();

        this.pageLines.addAll(processor.getPageLines());
        this.dividers.addAll(processor.getDividers());

        if (this.linesTestMode) {
            LineObjectsPrinter.printLinesObjects(this.pageLines);
            for (PageLine line: this.pageLines) {
                PdfPageDrawer.drawRectangle(this.document,
                        this.document.getPages().get(pageIndex - 1),
                        line.getRectangle(), Color.BLACK);
            }
        }

        processSkeletons();

        if (this.dividersTestMode) {
            for (Divider div: this.dividers) {
                PdfPageDrawer.drawRectangle(this.document,
                        this.document.getPages().get(pageIndex - 1),
                        div.getRectangle(), Color.RED);
            }
        }

        if (this.skeletonsTestMode) {
            for (Skeleton skeleton : this.skeletons) {
                PdfPageDrawer.drawRectangle(this.document,
                        this.document.getPages().get(pageIndex - 1),
                        skeleton.getRectangle(), Color.GREEN);
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

    /**
     * Fill skeletons with blocks with content,
     * also determining its type (PAGE, LIST, TABLE)
     */
    private void fillSkeletons() {
        Set<PageLine> linesToDelete = new HashSet<>();
        Set<PdfDocumentObject> objectsInLineToDelete;
        for (Skeleton skeleton: this.skeletons) {
            boolean isListType = true;
            for (Divider firstDivider: skeleton.getDividers()) {
                Block leftBlock = new Block();
                Block rightBlock = new Block();
                int countLeftEmptyLines = 0;
                int countRightEmptyLines = 0;
                for (int lineIndex = skeleton.getNumberOfFirstLine(); lineIndex <= skeleton.getNumberOfLastLine(); lineIndex++) {
                    PageLine line = this.pageLines.get(lineIndex);
                    PageLine leftSkeletonBlockLine = new PageLine();
                    PageLine rightSkeletonBlockLine = new PageLine();
                    objectsInLineToDelete = new HashSet<>();
                    for (PdfDocumentObject object: line.getObjects()) {
                        if (this.helper.contains(skeleton.getRectangle(), object.getRectangle())) {
                            if (this.helper.isBeforeHorizontally(object.getRectangle(), firstDivider.getRectangle())) {
                                leftSkeletonBlockLine.addObject(object);
                                leftSkeletonBlockLine.setLineNumber(leftBlock.getLines().size());
                                objectsInLineToDelete.add(object);
                                isListType = isObjectListBullet(object) && isListType;
                            } else if (this.helper.isBeforeHorizontally(firstDivider.getRectangle(), object.getRectangle())) {
                                rightSkeletonBlockLine.addObject(object);
                                rightSkeletonBlockLine.setLineNumber(rightBlock.getLines().size());
                                objectsInLineToDelete.add(object);
                            }
                        }
                    }
                    deleteObjects(objectsInLineToDelete, line);

                    if (line.getObjects().isEmpty() &&
                            line.getLineNumber() != skeleton.getNumberOfFirstLine()) {
                        linesToDelete.add(line);
                    }

                    if (leftSkeletonBlockLine.getObjects().isEmpty())
                        ++countLeftEmptyLines;

                    if (rightSkeletonBlockLine.getObjects().isEmpty())
                        ++countRightEmptyLines;

                    leftBlock.addLine(leftSkeletonBlockLine);
                    rightBlock.addLine(rightSkeletonBlockLine);
                }
                if (skeleton.getNumberOfLastLine() - skeleton.getNumberOfFirstLine() + 1 != countLeftEmptyLines)
                    skeleton.addSkeletonDataBlock(leftBlock);
                if (skeleton.getNumberOfLastLine() - skeleton.getNumberOfFirstLine() + 1 != countRightEmptyLines)
                    skeleton.addSkeletonDataBlock(rightBlock);
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
        return object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) &&
                ((TextObject) object).getTextObjectType().equals(TextObjectType.LIST_BULLET);
    }

    private void deleteObjects(Set<PdfDocumentObject> objectsInLineToDelete, PageLine line) {
        line.setObjects(line.getObjects().stream()
                .filter(object -> !objectsInLineToDelete.contains(object))
                .collect(Collectors.toList()));
    }

    private void deleteLines(Set<PageLine> linesToDelete) {
        this.pageLines = this.pageLines.stream()
                .filter(line -> !linesToDelete.contains(line))
                .collect(Collectors.toList());
    }

    /**
     * Set level of deep to all skeletons
     * (level of deep represents the number of skeletons
     * which contain this one skeleton inside)
     */
    private void resolveInnerSkeletons() {
        for (int i = 0; i < this.skeletons.size(); i++) {
            for (int j = 0; j < this.skeletons.size(); j++) {
                if (i != j) {
                    Skeleton currentSkeleton = this.skeletons.get(i);
                    Skeleton comparedSkeleton = this.skeletons.get(j);
                    if (helper.contains(currentSkeleton.getRectangle(), comparedSkeleton.getRectangle())) {
                        comparedSkeleton.setLevel(currentSkeleton.getLevel() + 1);
                    }
                }
            }
        }
        this.skeletons.sort(Skeleton::compareToDesc);
    }

    /**
     * Create skeletons
     * (skeleton represents rectangle area with
     * dividers, which have the same height and take
     * the same lines numbers)
     */
    private void createSkeletons() {
        List<Rectangle2D> dividersBorderRectangles;
        Set<Integer> usedDividers = new HashSet<>();
        boolean isPage;
        for (int i = 0; i < this.dividers.size(); i++) {
            Skeleton skeleton = new Skeleton();
            isPage = false;
            Divider leftDivider = this.dividers.get(i);
            if (leftDivider.isPageBorder())
                isPage = true;
            skeleton.addDivider(leftDivider);
            skeleton.setNumberOfFirstLine(leftDivider.getNumberOfFirstLine());
            skeleton.setNumberOfLastLine(leftDivider.getNumberOfLastLine());
            dividersBorderRectangles = new ArrayList<>();
            dividersBorderRectangles.add(leftDivider.getBorderRectangle());
            if (!usedDividers.contains(i)) {
                for (int j = i + 1; j < this.dividers.size(); j++) {
                    Divider rightDivider = this.dividers.get(j);
                    if (leftDivider.getNumberOfFirstLine() == rightDivider.getNumberOfFirstLine() &&
                            leftDivider.getNumberOfLastLine() == rightDivider.getNumberOfLastLine()) {
                        if (rightDivider.isPageBorder())
                            isPage = true;
                        skeleton.addDivider(rightDivider);
                        dividersBorderRectangles.add(rightDivider.getBorderRectangle());
                        usedDividers.add(j);
                    }
                }
                skeleton.setRectangle(this.helper.combineRectangles(dividersBorderRectangles));
                if (isPage)
                    skeleton.setType(SkeletonType.PAGE);
                this.skeletons.add(skeleton);
            }
        }
    }

    /**
     * Create all the border rectangle for all
     * dividers, depending on if one divider is
     * a border for border rectangle of another divider
     *
     * Example: (d - divider, --- - border rectangle)
     *
     *   --------------
     *   |   d--------|
     *   |   d   d    |
     *   |   d   d    |
     *   |   d--------|
     *   |   d        |
     *   --------------
     *
     *
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
     * @param nextDivider - divider that is larger by number
     *                    of lines than currentDivider
     */
    private void setDividersBorderRectangle(Divider currentDivider, Divider nextDivider) {
        if (currentDivider.isDividerLessOtherDivider(nextDivider)) {
            if (this.helper.isBeforeHorizontally(nextDivider.getRectangle(), currentDivider.getRectangle())) {
                // if nextDivider is on the left side of the current divider
                currentDivider.setBorderRectangle(createLeftBorderRectangle(nextDivider.getRectangle(),
                        currentDivider.getBorderRectangle()));
            }
            else if (this.helper.isBeforeHorizontally(currentDivider.getRectangle(), nextDivider.getRectangle())) {
                // if nextDivider is on the right side of the current divider
                currentDivider.setBorderRectangle(createRightBorderRectangle(currentDivider.getBorderRectangle(),
                        nextDivider.getRectangle()));
            }
        }
    }

    /**
     *           |
     *        -->
     *           |   |
     *           |   |
     *                <--
     *               |
     */
    private void cutCrossingDividers() {
        // TODO
    }

    private Rectangle2D createLeftBorderRectangle(Rectangle2D rectangle1, Rectangle2D rectangle2) {
        return new Rectangle2D(rectangle1.getMaxX(), rectangle2.getMinY(),
                rectangle2.getMaxX() - rectangle1.getMaxX(), rectangle2.getHeight());
    }

    private Rectangle2D createRightBorderRectangle(Rectangle2D rectangle1, Rectangle2D rectangle2) {
        return new Rectangle2D(rectangle1.getMinX(), rectangle1.getMinY(),
                rectangle2.getMinX() - rectangle1.getMinX(), rectangle1.getHeight());
    }

    /**
     * Filter one line dividers
     */
    private void filterOneLineDividers() {
        this.dividers = this.dividers.stream()
                .filter(divider -> (divider.getNumberOfLastLine() - divider.getNumberOfFirstLine()) > 1)
                .collect(Collectors.toList());
    }

    public List<Skeleton> getSkeletons() {
        return skeletons;
    }
}
