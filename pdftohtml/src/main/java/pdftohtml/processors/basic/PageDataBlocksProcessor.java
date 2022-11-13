package pdftohtml.processors.basic;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Properties;
import pdftohtml.domain.common.DocumentMetadata;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.domain.pdf.object.process.*;
import pdftohtml.domain.pdf.object.process.complex.Skeleton;
import pdftohtml.domain.pdf.object.process.container.Block;
import pdftohtml.domain.pdf.object.process.container.PageLine;
import pdftohtml.domain.pdf.object.process.template.Divider;
import pdftohtml.helpers.testing.LineObjectsPrinter;
import pdftohtml.helpers.testing.PdfPageDrawer;
import pdftohtml.processors.basic.objects.dividers.PageObjectsDividersProcessor;
import pdftohtml.processors.basic.objects.graphics.GraphicsProcessor;
import pdftohtml.processors.basic.objects.paths.StrokePathRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static pdftohtml.helpers.RectangleHelper.combineRectangles;

/**
 * Creates skeletons of all page data from dividers border rectangles and lines of the page.
 * Skeleton is a rectangle with inner content, that can have the whole page content, to be a list
 * (list in a text like 1. something 2. something, etc.) and a table (table in a text with rows and
 * cells)
 *
 * @author Daria Pleshchankova
 */
public class PageDataBlocksProcessor {

  /**
   * Main document for processing
   */
  private PDDocument document;

  /**
   * Processors
   */
  private PageTextObjectsProcessor pageTextObjectsProcessor;
  private GraphicsProcessor graphicsProcessor;
  private StrokePathRenderer strokePathRenderer;
  private PageObjectsDividersProcessor pageObjectsDividersProcessor;

  private List<PageLine> pageLines;
  private List<Divider> dividers;
  private List<Skeleton> skeletons;
  private List<Block> blocks;
  private List<Block> currentPageBlocks;

  /**
   * Test mode flags
   */
  private boolean linesTestMode = false;
  private boolean blocksTestMode = false;
  private boolean graphicsTestMode = false;
  private boolean strokePathsTestMode = false;
  private boolean dividersTestMode = false;
  private boolean skeletonsTestMode = false;

  public PageDataBlocksProcessor(PDDocument document) {
    this.pageLines = new ArrayList<>();
    this.dividers = new ArrayList<>();
    this.skeletons = new ArrayList<>();
    this.blocks = new ArrayList<>();
    this.currentPageBlocks = new ArrayList<>();
    this.document = document;
    try {
      this.pageTextObjectsProcessor = new PageTextObjectsProcessor();
      this.graphicsProcessor = new GraphicsProcessor();
      this.strokePathRenderer = new StrokePathRenderer(document);
      this.pageObjectsDividersProcessor = new PageObjectsDividersProcessor(document);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void processPage(int pageIndex) {
    this.currentPageBlocks = new ArrayList<>();

    PDPage page = this.document.getPage(pageIndex - 1);
    processPageText(pageIndex, page);

    // get text data blocks
    this.currentPageBlocks.addAll(pageTextObjectsProcessor.getBlocks());

    // extract all page graphics
    processPageGraphics(pageIndex, page);

    this.blocks.addAll(this.currentPageBlocks);
    pageObjectsDividersProcessor.findDividersOnPage(pageIndex, this.currentPageBlocks);

    // get all page data dividers
    // TODO

    // stroke paths (table borders) extractor
    processPageStrokePaths(pageIndex);

    // modify dividers with stroke paths priority
    // TODO

//    this.pageLines.addAll(processor.getPageLines());
//    this.dividers.addAll(processor.getDividers());

    drawObjects(pageIndex);
  }

  private void processPageText(int pageIndex, PDPage page) {
    pageTextObjectsProcessor.setStartPage(pageIndex);
    pageTextObjectsProcessor.setEndPage(pageIndex);
    try {
      pageTextObjectsProcessor.setPage(page);
      pageTextObjectsProcessor.setPageIndex(pageIndex);
      pageTextObjectsProcessor.processPage(page);
      pageTextObjectsProcessor.getText(document);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processPageGraphics(int pageIndex, PDPage page) {
    try {
      graphicsProcessor.setPageIndex(pageIndex);
      graphicsProcessor.processPage(page);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<GraphicsObject> graphics = graphicsProcessor.getGraphicsObjects();
    currentPageBlocks.addAll(gatherGraphicsToBlocksAndLines(graphics, pageIndex));

    if (this.graphicsTestMode) {
      for (GraphicsObject graphicsObject : graphics) {
        PdfPageDrawer.drawRectangle(
                this.document,
                this.document.getPages().get(pageIndex - 1),
                graphicsObject.getRectangle(),
                Color.green,
                null
        );
      }
    }
  }

  /**
   * @param graphics
   */
  private List<Block> gatherGraphicsToBlocksAndLines(
          List<GraphicsObject> graphics,
          int pageIndex
  ) {
    return graphics.stream().map(graphicsObject -> {
      if (!isTransparentOrMonotoneColor(graphicsObject.getImage())) {

        File outputfile =
                new File("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\image.jpg");
        try {
          ImageIO.write(graphicsObject.getImage(), "jpg", outputfile);
        } catch (IOException e) {
          e.printStackTrace();
        }

        Block imageBlock = new Block();
        PageLine pageLine = new PageLine();
        pageLine.addObject(graphicsObject);
        imageBlock.addLine(pageLine);
        imageBlock.setDocumentMetadata(
                new DocumentMetadata(
                        document.getDocumentInformation()
                                .getTitle(),
                        pageIndex
                )
        );
        return imageBlock;
      } else {
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  private boolean isTransparentOrMonotoneColor(BufferedImage image) {
    int color = 0;
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int pixel = image.getRGB(x, y);
        if (x == 0 && y == 0) {
          color = pixel;
        }
        if (pixel != color) {
          return false;
        }
      }
    }
    return true;
  }

  private void processPageStrokePaths(int pageIndex) {
    try {
      strokePathRenderer.extractPaths(pageIndex - 1);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (this.strokePathsTestMode) {
      strokePathRenderer.getPaths().forEach(path -> {
        PdfPageDrawer.drawRectangle(
                this.document,
                this.document.getPages().get(pageIndex - 1),
                path,
                Color.BLUE,
                null
        );
      });
    }
  }

  /**
   * Sort blocks of content on a page:
   * firstly by Y minimum then by X minimum
   *
   * For example:
   *
   *  -----          ----
   *  |   |  -----   |  |
   *  |   |  |   |   |  |
   *  |   |  -----   |  |
   *  |   |          ----
   *  |   |
   *  |   |  ------------
   *  |   |  |          |
   *  -----  |          |
   *         ------------
   *
   *
   */
  private void processBlocks(List<Block> blocks) {
    // sort blocks firstly by y and then by x
    Comparator<Block> blockByYminXminComparator
            = Comparator.comparing(
                    Block::getContentRectangle,
                    Comparator.comparingDouble(FrameworkRectangle::getMinY)
              ).thenComparing(
                    Block::getContentRectangle,
                    Comparator.comparingDouble(FrameworkRectangle::getMinX)
            );
    blocks.sort(blockByYminXminComparator);
  }

  private void processSkeletons() {
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
    List<FrameworkRectangle> dividersBorderRectangles;
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
        skeleton.setRectangle(combineRectangles(dividersBorderRectangles));
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

  private FrameworkRectangle createLeftBorderRectangle(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
    return new FrameworkRectangle(
            rectangle1.getMaxX(),
            rectangle2.getMinY(),
            rectangle2.getMaxX() - rectangle1.getMaxX(),
            rectangle2.getHeight());
  }

  private FrameworkRectangle createRightBorderRectangle(FrameworkRectangle rectangle1, FrameworkRectangle rectangle2) {
    return new FrameworkRectangle(
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

  private void drawObjects(int pageIndex) {
    if (this.linesTestMode) {
      LineObjectsPrinter.printLinesObjects(this.pageLines);
      for (PageLine line : this.pageLines) {
        PdfPageDrawer.drawRectangle(
                this.document,
                this.document.getPages().get(pageIndex - 1),
                line.getRectangle(),
                Color.BLACK,
                null
        );
      }
    }

    if (this.blocksTestMode) {
      for (Block block : blocks) {
        if (block.getLines().size() == 1 && block.getLines().get(0).getText().isEmpty()) {
          PdfPageDrawer.drawRectangle(
                  this.document,
                  this.document.getPages().get(pageIndex - 1),
                  block.getContentRectangle(),
                  Color.CYAN,
                  null
          );
        } else {
          PdfPageDrawer.drawRectangle(
                  this.document,
                  this.document.getPages().get(pageIndex - 1),
                  block.getContentRectangle(),
                  Color.MAGENTA,
                  null
          );
        }
      }
    }

    if (this.dividersTestMode) {
      for (Divider div : this.dividers) {
        PdfPageDrawer.drawRectangle(
                this.document,
                this.document.getPages().get(pageIndex - 1),
                div.getRectangle(),
                Color.RED,
                null
        );
      }
    }
  }
}
