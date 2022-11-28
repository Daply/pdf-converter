package pdftohtml.processors.pdf.objects.text;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import pdftohtml.domain.common.DocumentMetadata;
import pdftohtml.domain.common.Point2D;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;
import pdftohtml.domain.pdf.object.container.Block;
import pdftohtml.domain.pdf.object.container.PageLine;
import pdftohtml.domain.pdf.object.text.LinkObject;
import pdftohtml.domain.pdf.object.text.TextObject;
import pdftohtml.domain.pdf.object.text.TextPositionStyleWrapper;
import pdftohtml.helpers.testing.PdfPageDrawer;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static pdftohtml.common.Globals.*;
import static pdftohtml.common.Stats.maximumDistanceBetweenLines;
import static pdftohtml.common.Stats.minimumDistanceBetweenLines;
import static pdftohtml.helpers.RectangleHelper.*;

/**
 * Parses pdf document page and gets all text data from it.
 * All simple objects (characters) are gathered to separate
 * text objects as words with special formatting and links {@link LinkObject}.
 * Words objects are gathered to line objects {@link PageLine} and afterwards
 * all the lines gathered to data blocks {@link Block}.
 *
 * @author Daria Pleshchankova
 */
@Log4j
public class PageTextObjectsProcessor extends PDFTextStripper {

    private final boolean testMode = false;

    /**
     * Current PDF document page to extract text data from
     */
    private PDPage page;

    @Getter
    @Setter
    private int pageIndex;

    private Block currentLinesBlock;

    @Getter
    private List<Block> blocks;
    private Set<Block> existingBlocks;

    private FrameworkRectangle previousLineRectangle;

    @Getter
    private List<PageLine> pageLines;

    private List<PDAnnotation> allPageLinks;

    private Map<TextPosition, TextPositionStyleWrapper> lineCharacters;
    private List<TransformedRectangle> rectangles;

    private PdfTextObjectsCreationFactory pdfTextObjectsCreationFactory;

    public PageTextObjectsProcessor() throws IOException {
        super();
        addOperators();
        init();
    }

    public void setPage(PDPage page) throws IOException {
        this.page = page;
        this.allPageLinks = page.getAnnotations();
    }

    private void addOperators() {
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
        addOperator(new AppendRectangleToPath());
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        super.processPage(page);
    }

    @Override
    public String getText(PDDocument doc) throws IOException {
        init();
        String text = super.getText(doc);
        processBlock();
        return text;
    }

    /**
     * Page one line processing
     *
     * @param text          - text of the line
     * @param textPositions - list of text positions of all characters in a line
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {
        this.pdfTextObjectsCreationFactory = new PdfTextObjectsCreationFactory(page);
        gatherTextObjectsToLine(textPositions);
    }

    private void init() {
        this.blocks = new ArrayList<>();
        this.existingBlocks = new HashSet<>();
        this.currentLinesBlock = new Block();

        this.lineCharacters = new HashMap<>();
        this.pageLines = new ArrayList<>();

        this.allPageLinks = new ArrayList<>();

        this.rectangles = new ArrayList<>();
    }

    /**
     * Create a line with gathered words and sentences from a
     * list of text position objects (characters with metadata).
     * <p>
     * Algorithm:
     * Taking text position objects by two and deciding if they
     * belong to the same word by checking their styles and
     * space between them.
     *
     * @param textPositions - list of text positions (characters with metadata)
     */
    private void gatherTextObjectsToLine(List<TextPosition> textPositions) {
        StringBuilder lineText = new StringBuilder();
        List<PdfDocumentObject> lineObjects = new ArrayList<>();

        PdfDocumentObject currentObject = null;
        PdfDocumentObject previousObject = null;
        FrameworkRectangle currentObjectRectangle = null;
        FrameworkRectangle previousObjectRectangle = null;
        boolean belongToDifferentObjects = false;
        for (TextPosition textPosition : textPositions) {

            // if currentObject is not null
            // at the very start of the cycle
            // then the current textPosition
            // is not first in the text line
            if (Objects.nonNull(currentObject)) {
                previousObjectRectangle = currentObject.getRectangle();
            }
            currentObjectRectangle = createTextPositionRectangle(textPosition);

            // set flag that two neighbour objects (ex. letters)
            // belong to different text objects (ex. words, links or sentences)
            // if the space between them is too big
            if (checkXSpaceBetweenTwoRectangles(
                    previousObjectRectangle, currentObjectRectangle, MINIMUM_DIVIDER_WIDTH
            )) {
                belongToDifferentObjects = true;
                previousObject = currentObject;
                currentObject = null;
            }

            // create a text object from current text position object
            currentObject = createObject(
                    lineObjects,
                    textPosition,
                    currentObjectRectangle,
                    currentObject
            );

            // if two neighbour objects belong
            // to different text objects
            // then complete the current line
            if (belongToDifferentObjects) {
                processLine(previousObject, lineObjects, lineText);
                lineObjects = new ArrayList<>();
                lineText = new StringBuilder();
            }

            belongToDifferentObjects = false;
            lineText.append(textPosition.getUnicode());
        }
        processLine(currentObject, lineObjects, lineText);
    }

    private void processLine(
            PdfDocumentObject currentObject,
            List<PdfDocumentObject> lineObjects,
            StringBuilder lineText
    ) {

        // add current text object to line
        if (currentObject != null) {
            lineObjects.add(currentObject);
        }

        // if whole the line is not empty
        if (!lineText.toString().isEmpty() && !lineText.toString().isBlank()) {
            createLine(lineObjects);
        }
    }

    /**
     * Create new page text line
     *
     * @param lineObjects - list of line objects
     */
    private void createLine(List<PdfDocumentObject> lineObjects) {
        PageLine currentLine = new PageLine();
        currentLine.addAllObjects(lineObjects);

        if (previousLineRectangle != null &&
                (!haveTheSameStart(previousLineRectangle, currentLine.getRectangle()) ||
                        spaceByYBetweenTwoRectanglesIsMore(
                                previousLineRectangle,
                                currentLine.getRectangle(),
                                (float) (2 * currentLine.getRectangle().getHeight()))
                )
        ) {
            processBlock();
        }

        previousLineRectangle = currentLine.getRectangle();

        this.pageLines.add(currentLine);
        addLineParameterStatistics();
    }

    private void processBlock() {
        if (!this.pageLines.isEmpty()) {
            this.currentLinesBlock.setLines(this.pageLines);
            this.currentLinesBlock.setDocumentMetadata(
                    new DocumentMetadata(
                            "",
                            this.pageIndex
                    )
            );
        }

        if (!this.existingBlocks.contains(this.currentLinesBlock)) {
            this.blocks.add(currentLinesBlock.copy());
            this.existingBlocks.add(currentLinesBlock);
        }

        this.currentLinesBlock = new Block();
        this.pageLines = new ArrayList<>();
    }

    private void addLineParameterStatistics() {
        if (this.pageLines.size() > 1) {
            PageLine firstLine = this.pageLines.get(this.pageLines.size() - 2);
            PageLine secondLine = this.pageLines.get(this.pageLines.size() - 1);
            float distanceBetweenLines =
                    (float) (secondLine.getRectangle().getMinY() - firstLine.getRectangle().getMaxY());
            if (distanceBetweenLines > 0 &&
                    (distanceBetweenLines < minimumDistanceBetweenLines ||
                            minimumDistanceBetweenLines == 0f)) {
                minimumDistanceBetweenLines = distanceBetweenLines;
            }
            if (distanceBetweenLines > 0 &&
                    (distanceBetweenLines > maximumDistanceBetweenLines ||
                            maximumDistanceBetweenLines == 0f)) {
                maximumDistanceBetweenLines = distanceBetweenLines;
            }
        }
    }

    /**
     * Create pdf object (text, link)
     *
     * @param lineObjects   - previously processed ans already added pdf objects
     * @param textPosition  - text position of current character
     * @param currentObject - current processed pdf object
     * @return created object
     */
    private PdfDocumentObject createObject(
            List<PdfDocumentObject> lineObjects,
            TextPosition textPosition,
            FrameworkRectangle textPositionRectangle,
            PdfDocumentObject currentObject
    ) {
        TextPositionStyleWrapper wrapper = this.lineCharacters.get(textPosition);
        currentObject = createLink(
                lineObjects,
                textPosition,
                wrapper,
                currentObject,
                textPositionRectangle
        );
        currentObject = createTextObject(
                lineObjects,
                textPosition,
                wrapper,
                currentObject,
                textPositionRectangle
        );
        return currentObject;
    }

    /**
     * Create text pdf object.
     *
     * @param lineObjects      - previously processed ans already added pdf objects
     * @param textPosition     - text position of current character
     * @param wrapper          - style wrapper for text position
     * @param currentObject    - current processed pdf object
     * @param currentRectangle - current processed text position rectangle
     * @return created object
     */
    private PdfDocumentObject createTextObject(
            List<PdfDocumentObject> lineObjects,
            TextPosition textPosition,
            TextPositionStyleWrapper wrapper,
            PdfDocumentObject currentObject,
            FrameworkRectangle currentRectangle
    ) {
        // if current object is first one
        // to process, then create
        // from it a new text object,
        // otherwise compare it with
        // previous object
        if (currentObject == null) {
            currentObject = this.pdfTextObjectsCreationFactory.create(textPosition, wrapper, null);
            currentObject.setRectangle(currentRectangle);
        } else {
            // if previously processed object
            // also a text and has the same styling
            // then combine current text object
            // with the previous one, otherwise
            // put previous object to current line
            // and create new text object from current object
            if (currentObject.getObjectType().equals(PdfDocumentObjectType.TEXT) &&
                    this.pdfTextObjectsCreationFactory.equalsByStyle(
                            (TextObject) currentObject, textPosition, wrapper)
            ) {
                ((TextObject) currentObject).addToTextContent(textPosition.getUnicode());
                currentObject.addToRectangle(currentRectangle);
            } else {
                lineObjects.add(currentObject);
                currentObject = this.pdfTextObjectsCreationFactory.create(textPosition, wrapper, null);
                currentObject.setRectangle(currentRectangle);
            }
        }
        return currentObject;
    }

    /**
     * Create pdf object as link
     *
     * @param lineObjects      - previously processed ans already added pdf objects
     * @param textPosition     - text position of current character
     * @param wrapper          - style wrapper for text position
     * @param currentObject    - current processed pdf object
     * @param currentRectangle - current processed text position rectangle
     * @return created object
     */
    private PdfDocumentObject createLink(
            List<PdfDocumentObject> lineObjects,
            TextPosition textPosition,
            TextPositionStyleWrapper wrapper,
            PdfDocumentObject currentObject,
            FrameworkRectangle currentRectangle) {
        for (PDAnnotation link : this.allPageLinks) {
            if (this.pdfTextObjectsCreationFactory.isLink(link, textPosition)) { // TODO refactor
                // if current object is first one
                // to process, then create
                // from it a new link,
                // otherwise compare it with
                // previous object
                if (currentObject == null) {
                    currentObject = this.pdfTextObjectsCreationFactory.create(textPosition, wrapper, link);
                    currentObject.setRectangle(currentRectangle);
                } else {
                    // if previously processed object
                    // also a link then combine current
                    // link with the previous one, otherwise
                    // put previous object to current line
                    // and create new link from current object
                    if (currentObject.getObjectType().equals(PdfDocumentObjectType.LINK)) { // TODO test fo two different link standing together
                        ((LinkObject) currentObject).addToTextContent(textPosition.getUnicode());
                        currentObject.addToRectangle(currentRectangle);
                    } else {
                        lineObjects.add(currentObject);
                        currentObject = this.pdfTextObjectsCreationFactory.create(textPosition, wrapper, link);
                        currentObject.setRectangle(currentRectangle);
                    }
                }
                break;
            }
        }
        return currentObject;
    }

    private TextPositionStyleWrapper determineStyle(TextPosition textPosition) {
        TextPositionStyleWrapper character = new TextPositionStyleWrapper();

        if (textPosition.getFont() != null && textPosition.getFont().getName() != null) {
            if (textPosition.getFont().getName().toLowerCase().contains("bold"))
                character.setBoldText(true);

            if (textPosition.getFont().getName().toLowerCase().contains("italic"))
                character.setItalicText(true);
        }

        if (this.rectangles.stream().anyMatch(r -> r.underlines(textPosition)))
            character.setUnderlinedText(true);

        if (this.rectangles.stream().anyMatch(r -> r.strikesThrough(textPosition)))
            character.setStrikeThroughText(true);

        return character;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        super.processTextPosition(text);

        PDColor strokingColor = getGraphicsState().getStrokingColor();
        PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
        RenderingMode renderingMode = getGraphicsState().getTextState().getRenderingMode();
        TextPositionStyleWrapper character = determineStyle(text);
        character.setStrokingColor(strokingColor);
        character.setNonStrokingColor(nonStrokingColor);
        character.setRenderingMode(renderingMode);
        this.lineCharacters.put(text, character);
    }

    class AppendRectangleToPath extends OperatorProcessor {

        public void process(Operator operator, List<COSBase> arguments) {
            COSNumber x = (COSNumber) arguments.get(0);
            COSNumber y = (COSNumber) arguments.get(1);
            COSNumber w = (COSNumber) arguments.get(2);
            COSNumber h = (COSNumber) arguments.get(3);

            double x1 = x.doubleValue();
            double y1 = y.doubleValue();

            double x2 = w.doubleValue() + x1;
            double y2 = h.doubleValue() + y1;

            Point2D p0 = transformedPoint(x1, y1);
            Point2D p1 = transformedPoint(x2, y1);
            Point2D p2 = transformedPoint(x2, y2);
            Point2D p3 = transformedPoint(x1, y2);

            rectangles.add(new TransformedRectangle(p0, p1, p2, p3));
        }

        @Override
        public String getName() {
            return "AppendRectangleToPath";
        }

        Point2D transformedPoint(double x, double y) {
            double[] position = {x, y};
            getGraphicsState()
                    .getCurrentTransformationMatrix()
                    .createAffineTransform()
                    .transform(position, 0, position, 0, 1);
            return new Point2D(position[0], position[1]);
        }
    }

    static class TransformedRectangle {

        final Point2D p0;
        final Point2D p1;
        final Point2D p2;
        final Point2D p3;

        public TransformedRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        boolean strikesThrough(TextPosition textPosition) {
            Matrix matrix = textPosition.getTextMatrix();
            if (p0.getX() > matrix.getTranslateX()
                    || p2.getX()
                    < matrix.getTranslateX()
                    + textPosition.getWidth()
                    - textPosition.getFontSizeInPt() / 10.0) return false;
            double vertDiff = p0.getY() - matrix.getTranslateY();
            if (vertDiff < 0
                    || vertDiff
                    > textPosition.getFont().getFontDescriptor().getAscent()
                    * textPosition.getFontSizeInPt()
                    / 1000.0) return false;
            return Math.abs(p2.getY() - p0.getY()) < 2;
        }

        boolean underlines(TextPosition textPosition) {
            Matrix matrix = textPosition.getTextMatrix();
            if (p0.getX() > matrix.getTranslateX()
                    || p2.getX()
                    < matrix.getTranslateX()
                    + textPosition.getWidth()
                    - textPosition.getFontSizeInPt() / 10.0) return false;
            double vertDiff = p0.getY() - matrix.getTranslateY();
            if (vertDiff > 0
                    || vertDiff
                    < textPosition.getFont().getFontDescriptor().getDescent()
                    * textPosition.getFontSizeInPt()
                    / 500.0) return false;
            return Math.abs(p2.getY() - p0.getY()) < 2;
        }
    }

    private void drawForTest(FrameworkRectangle rectangle) {
        if (this.testMode) {
            PdfPageDrawer.drawRectangle(
                    this.document,
                    this.page,
                    rectangle,
                    Color.BLUE,
                    null
            );
        }
    }
}
