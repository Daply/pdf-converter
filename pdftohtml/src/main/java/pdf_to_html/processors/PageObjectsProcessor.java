package pdf_to_html.processors;

import lombok.Getter;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import pdf_to_html.entities.framework.Point2D;
import pdf_to_html.entities.framework.Rectangle2D;
import pdf_to_html.entities.pdfdocument.object.process.*;
import pdf_to_html.entities.pdfdocument.object.process.container.Block;
import pdf_to_html.entities.pdfdocument.object.process.container.PageLine;
import pdf_to_html.entities.pdfdocument.object.process.template.Divider;
import pdf_to_html.helpers.Rectangle2DHelper;
import pdf_to_html.helpers.testing.PdfPageDrawer;
import pdf_to_html.processors.creators.DividerRectanglesCreator;
import pdf_to_html.processors.creators.objects.TextObjectsCreationFactory;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static pdf_to_html.helpers.Globals.*;
import static pdf_to_html.helpers.Stats.MAXIMUM_DISTANCE_BETWEEN_LINES;
import static pdf_to_html.helpers.Stats.MINIMUM_DISTANCE_BETWEEN_LINES;

/**
 * @author Pleshchankova Daria
 *
 * Parses pdf document page and gets all text data from it,
 * all simple objects are created such as sentences, some
 * words with special formatting, links and dividers of
 * content blocks (dividers are rather big empty spaces
 * between the objects).
 */
@Getter
public class PageObjectsProcessor extends PDFTextStripper {

  private boolean testMode = true;

  private PDPage page;

  private Block currentLinesBlock;
  private List<Block> blocks;

  private Rectangle2D prevLineRectangle;
  private List<PageLine> pageLines;

  private List<Divider> dividers;

  private List<Rectangle2D> currentBetweenObjectsRectangles;
  private List<PDAnnotation> allPageLinks;

  private final Map<TextPosition, TextPositionStyleWrapper> lineCharacters;
  private final List<TransformedRectangle> rectangles;

  /**
   * Helpers
   */
  Rectangle2DHelper rectangle2DHelper;

  /**
   * Creators
   */
  DividerRectanglesCreator dividerRectanglesCreator;

  private TextObjectsCreationFactory textObjectsCreationFactory;

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

  public PageObjectsProcessor() throws IOException {
    super();
    addOperators();

    this.blocks = new ArrayList<>();
    this.currentLinesBlock = new Block();

    this.lineCharacters = new HashMap<>();
    this.pageLines = new ArrayList<>();
    this.dividers = new ArrayList<>();
    this.currentBetweenObjectsRectangles = new ArrayList<>();
    this.allPageLinks = new ArrayList<>();
    this.rectangles = new ArrayList<>();

    this.rectangle2DHelper = new Rectangle2DHelper();
    this.dividerRectanglesCreator = new DividerRectanglesCreator();
  }

  @Override
  public void processPage(PDPage page) throws IOException {
    super.processPage(page);
    processBlock();
  }

  /**
   * Page line processing
   *
   * @param text - text of the line
   * @param textPositions - list of text positions of all characters in a line
   */
  @Override
  protected void writeString(String text, List<TextPosition> textPositions) {
    this.textObjectsCreationFactory = new TextObjectsCreationFactory(page);
    this.currentBetweenObjectsRectangles = new ArrayList<>();
    createLines(textPositions);
  }

  private void createLines(List<TextPosition> textPositions) {
    StringBuilder lineText = new StringBuilder();
    List<PdfDocumentObject> lineObjects = new ArrayList<>();

    PdfDocumentObject currentObject = null;
    Rectangle2D currentObjectRectangle = null;
    Rectangle2D previousObjectRectangle = null;
    boolean differentObjects = false;
    for (TextPosition textPosition: textPositions) {
      if (currentObject == null) {
        this.currentBetweenObjectsRectangles.add(this.dividerRectanglesCreator
                .createRectangleBefore(textPosition));
      }
      else {
        previousObjectRectangle = currentObject.getRectangle();
      }
      currentObjectRectangle = this.dividerRectanglesCreator
              .createTextPositionRectangle(textPosition);

      if (previousObjectRectangle != null &&
              rectangle2DHelper.checkXSpaceBetweenTwoRectangles(previousObjectRectangle,
                      currentObjectRectangle, MINIMUM_DIVIDER_WIDTH)) {
        differentObjects = true;
        currentObject = null;
      }
      currentObject = createObject(lineObjects, textPosition, currentObjectRectangle, currentObject);
      if (differentObjects) {
        processLine(currentObject, lineObjects, lineText);
        lineObjects = new ArrayList<>();
        lineText = new StringBuilder();
      }
      differentObjects = false;
      lineText.append(textPosition.getUnicode());
    }
    processLine(currentObject, lineObjects, lineText);
  }

  private void processLine(PdfDocumentObject currentObject, List<PdfDocumentObject> lineObjects, StringBuilder lineText) {
    // adding last processed object
    if (currentObject != null) {
      lineObjects.add(currentObject);
      this.currentBetweenObjectsRectangles.add(this.dividerRectanglesCreator
              .createRectangleAfter(currentObject.getRectangle(), this.page.getCropBox().getWidth()));
    }
    // if whole the line is not empty
    if (!lineText.toString().matches(EMPTY_SPACE_PATTERN)) {
      createLine(lineObjects);
      for (Divider div: this.dividers) {
        try {
          PdfPageDrawer.drawRectangle(this.document,
                  this.page,
                  div.getRectangle(), Color.RED);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      uniteDividers(this.pageLines.size() - 1);
    }
  }

  /**
   * Create new page line
   *
   * @param lineObjects - list of line objects
   */
  private void createLine(List<PdfDocumentObject> lineObjects) {
    PageLine currentLine = new PageLine();
    currentLine.addAllObjects(lineObjects);

    if (prevLineRectangle != null &&
            !rectangle2DHelper.haveTheSameStart(prevLineRectangle, currentLine.getRectangle())) {
      processBlock();
    }

    prevLineRectangle = currentLine.getRectangle();

    this.pageLines.add(currentLine);
    addLineStats();
  }

  private void processBlock() {
    this.currentLinesBlock.setLines(this.pageLines);
    this.blocks.add(currentLinesBlock);
    this.currentLinesBlock = new Block();
    this.pageLines = new ArrayList<>();
  }

  private boolean uniteLines(PageLine newLine) {
    for (PageLine line: this.pageLines) {
      if (rectangle2DHelper.areOneLine(line.getRectangle(), newLine.getRectangle())) {
        Rectangle2D betweenRectangle = this.rectangle2DHelper
                .createRectangleBetweenTwoRectangles(line.getRectangle(), newLine.getRectangle());
        if (betweenRectangle != null)
          this.currentBetweenObjectsRectangles.add(betweenRectangle);
        line.addAllObjects(newLine.getObjects());
        return true;
      }
    }
    return false;
  }

  private void addLineStats() {
    if (this.pageLines.size() > 1) {
      PageLine firstLine = this.pageLines.get(this.pageLines.size() - 2);
      PageLine secondLine = this.pageLines.get(this.pageLines.size() - 1);
      float distanceBetweenLines = (float) (secondLine.getRectangle().getMinY() -
              firstLine.getRectangle().getMaxY());
      if (distanceBetweenLines > 0 && (distanceBetweenLines < MINIMUM_DISTANCE_BETWEEN_LINES ||
              MINIMUM_DISTANCE_BETWEEN_LINES == 0f)) {
        MINIMUM_DISTANCE_BETWEEN_LINES = distanceBetweenLines;
      }
      if (distanceBetweenLines > 0 && (distanceBetweenLines > MAXIMUM_DISTANCE_BETWEEN_LINES ||
              MAXIMUM_DISTANCE_BETWEEN_LINES == 0f)) {
        MAXIMUM_DISTANCE_BETWEEN_LINES = distanceBetweenLines;
      }
    }
  }

  /**
   * Unite all found dividers in text
   */
  private void uniteDividers(int currentLineNumber) {
    validateLineNumber(currentLineNumber);
    for (Rectangle2D rectangle: this.currentBetweenObjectsRectangles) {
      Rectangle2D borderRectangle = new Rectangle2D(0, rectangle.getMinY(),
              this.page.getCropBox().getWidth(), rectangle.getHeight());
      if (!uniteDividerWithNewRectangle(rectangle, borderRectangle, currentLineNumber)) {
        createDivider(rectangle, borderRectangle, currentLineNumber);
      }
    }
  }

  /**
   * Combine new rectangle with existing divider
   *
   * @param rectangle - dividers rectangle
   * @param borderRectangle - dividers possible content border rectangle
   * @param currentLineNumber - current line number
   * @return true - if divider rectangle was combined with new rectangle, false otherwise
   */
  private boolean uniteDividerWithNewRectangle(Rectangle2D rectangle, Rectangle2D borderRectangle,
                                               int currentLineNumber) {
    validateLineNumber(currentLineNumber);
    List<Divider> changedDividers = new ArrayList<>();
    this.dividers.forEach(existingDivider -> {
      Rectangle2D existingRectangle = existingDivider.getRectangle();
      float intersection = rectangle2DHelper.intersectsHorizontally(rectangle, existingRectangle);
      if ((existingDivider.getNumberOfLastLine() == currentLineNumber - 1) &&
              (intersection >= (0.75 * rectangle.getWidth()) ||
                      intersection >= (0.75 * existingRectangle.getWidth()))) {
        Rectangle2D unit = rectangle2DHelper.uniteTwoRectanglesByXMinimally(existingRectangle, rectangle);
        existingDivider.setRectangle(unit);
        existingDivider.setBorderRectangle(rectangle2DHelper.combineTwoRectangles(borderRectangle,
                existingDivider.getBorderRectangle()));
        existingDivider.setNumberOfLastLine(currentLineNumber);
        changedDividers.add(existingDivider);
      }
    });
    return !changedDividers.isEmpty();
  }

  /**
   * Create divider (rectangle which represents empty space between objects)
   *
   * @param rectangle - dividers rectangle
   * @param borderRectangle - dividers possible content border rectangle
   */
  private void createDivider(Rectangle2D rectangle, Rectangle2D borderRectangle,
                             int currentLineNumber) {
    validateLineNumber(currentLineNumber);
    Divider divider = new Divider();
    divider.setRectangle(rectangle);
    if (Math.abs(rectangle.getMaxX() - this.page.getCropBox().getUpperRightX()) <= THRESHOLD ||
            Math.abs(rectangle.getMinX() - 0) <= THRESHOLD)
      divider.setPageBorder(true);
    divider.setBorderRectangle(borderRectangle);
    divider.setNumberOfFirstLine(currentLineNumber);
    divider.setNumberOfLastLine(currentLineNumber);
    this.dividers.add(divider);
    //drawForTest(divider.getRectangle());
  }

  /**
   * Create pdf object (text, link)
   *
   * @param lineObjects - previously processed ans already added pdf objects
   * @param textPosition - text position of current character
   * @param currentObject - current processed pdf object
   * @return created object
   */
  private PdfDocumentObject createObject(List<PdfDocumentObject> lineObjects,
                                         TextPosition textPosition,
                                         Rectangle2D textPositionRectangle,
                                         PdfDocumentObject currentObject) {
    TextPositionStyleWrapper wrapper = this.lineCharacters.get(textPosition);
    currentObject = createObjectAsLink(lineObjects, textPosition, wrapper, currentObject, textPositionRectangle);
    currentObject = createObjectAsText(lineObjects, textPosition, wrapper, currentObject, textPositionRectangle);
    return currentObject;
  }

  /**
   * Create pdf object as text
   *
   * @param lineObjects - previously processed ans already added pdf objects
   * @param textPosition - text position of current character
   * @param wrapper - style wrapper for text position
   * @param currentObject - current processed pdf object
   * @param currentRectangle - current processed text position rectangle
   * @return created object
   */
  private PdfDocumentObject createObjectAsText(List<PdfDocumentObject> lineObjects,
                                               TextPosition textPosition,
                                               TextPositionStyleWrapper wrapper,
                                               PdfDocumentObject currentObject,
                                               Rectangle2D currentRectangle) {
    if (currentObject == null) {
      currentObject = this.textObjectsCreationFactory.create(textPosition, wrapper, null);
      currentObject.setRectangle(currentRectangle);
    }
    else {
      if (currentObject.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) &&
              this.textObjectsCreationFactory.equalsByStyle((TextObject) currentObject, textPosition, wrapper)) {
        ((TextObject)currentObject).addToTextContent(textPosition.getUnicode());
        currentObject.addToRectangle(currentRectangle);
      }
      else if (!currentObject.getObjectType().equals(PdfDocumentObjectType.LINK)) {
        lineObjects.add(currentObject);
        currentObject = this.textObjectsCreationFactory.create(textPosition, wrapper, null);
        currentObject.setRectangle(currentRectangle);
      }
    }
    return currentObject;
  }

  /**
   * Create pdf object as link
   *
   * @param lineObjects - previously processed ans already added pdf objects
   * @param textPosition - text position of current character
   * @param wrapper - style wrapper for text position
   * @param currentObject - current processed pdf object
   * @param currentRectangle - current processed text position rectangle
   * @return created object
   */
  private PdfDocumentObject createObjectAsLink(List<PdfDocumentObject> lineObjects,
                                               TextPosition textPosition,
                                               TextPositionStyleWrapper wrapper,
                                               PdfDocumentObject currentObject,
                                               Rectangle2D currentRectangle) {
    for (PDAnnotation link: this.allPageLinks) {
      if (this.textObjectsCreationFactory.isLink(link, textPosition)) {
        if (currentObject == null) {
          currentObject = this.textObjectsCreationFactory.create(textPosition, wrapper, link);
          currentObject.setRectangle(currentRectangle);
        }
        else {
          if (currentObject.getObjectType().equals(PdfDocumentObjectType.LINK)) {
            ((LinkObject)currentObject).addToTextContent(textPosition.getUnicode());
            currentObject.addToRectangle(currentRectangle);
          }
          else {
            lineObjects.add(currentObject);
            currentObject = this.textObjectsCreationFactory.create(textPosition, wrapper, link);
            currentObject.setRectangle(currentRectangle);
          }
        }
        break;
      }
    }
    return currentObject;
  }

  public void setPage(PDPage page) throws IOException {
    this.page = page;
    this.allPageLinks = page.getAnnotations();
  }

  private TextPositionStyleWrapper determineStyle(TextPosition textPosition)
  {
    TextPositionStyleWrapper character = new TextPositionStyleWrapper();

    if (textPosition.getFont() != null && textPosition.getFont().getName() != null) {
      if (textPosition.getFont().getName().toLowerCase().contains("bold"))
        character.setBoldText(true);

      if (textPosition.getFont().getName().toLowerCase().contains("italic"))
        character.setItalicText(true);
    }
    else {
      System.out.println(textPosition.getUnicode());
    }

    if (this.rectangles.stream().anyMatch(r -> r.underlines(textPosition)))
      character.setUnderlinedText(true);

    if (this.rectangles.stream().anyMatch(r -> r.strikesThrough(textPosition)))
      character.setStrikeThroughText(true);

    return character;
  }

  @Override
  protected void processTextPosition(TextPosition text)
  {
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

    Point2D transformedPoint(double x, double y)
    {
      double[] position = {x,y};
      getGraphicsState().getCurrentTransformationMatrix().createAffineTransform().transform(
              position, 0, position, 0, 1);
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

    boolean strikesThrough(TextPosition textPosition)
    {
      Matrix matrix = textPosition.getTextMatrix();
      if (p0.getX() > matrix.getTranslateX() || p2.getX() < matrix.getTranslateX() + textPosition.getWidth() - textPosition.getFontSizeInPt() / 10.0)
        return false;
      double vertDiff = p0.getY() - matrix.getTranslateY();
      if (vertDiff < 0 || vertDiff > textPosition.getFont().getFontDescriptor().getAscent() * textPosition.getFontSizeInPt() / 1000.0)
        return false;
      return Math.abs(p2.getY() - p0.getY()) < 2;
    }

    boolean underlines(TextPosition textPosition)
    {
      Matrix matrix = textPosition.getTextMatrix();
      if (p0.getX() > matrix.getTranslateX() || p2.getX() < matrix.getTranslateX() + textPosition.getWidth() - textPosition.getFontSizeInPt() / 10.0)
        return false;
      double vertDiff = p0.getY() - matrix.getTranslateY();
      if (vertDiff > 0 || vertDiff < textPosition.getFont().getFontDescriptor().getDescent() * textPosition.getFontSizeInPt() / 500.0)
        return false;
      return Math.abs(p2.getY() - p0.getY()) < 2;
    }

  }

  private void validateLineNumber(int lineNumber) {
    if (lineNumber < 0)
      throw new IllegalArgumentException("line number" + lineNumber +
              " is less than zero");
    if (lineNumber >= this.pageLines.size())
      throw new IllegalArgumentException("line number" + lineNumber +
              " is more or equal than number of lines");
  }

  public List<PageLine> getPageLines() {
    return this.pageLines;
  }

  public List<Divider> getDividers() {
    return this.dividers;
  }

  private void drawForTest(Rectangle2D rectangle2D) {
    if (this.testMode) {
      try {
        PdfPageDrawer.drawRectangle(this.document,
                this.page, rectangle2D, Color.BLUE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
