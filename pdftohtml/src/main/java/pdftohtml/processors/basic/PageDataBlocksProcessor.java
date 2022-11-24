package pdftohtml.processors.basic;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.domain.common.DocumentMetadata;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.basic.*;
import pdftohtml.domain.pdf.object.basic.container.Block;
import pdftohtml.domain.pdf.object.basic.container.PageLine;
import pdftohtml.domain.pdf.object.basic.template.Divider;
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
  private List<FrameworkRectangle> strokePaths;
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

  public PageDataBlocksProcessor(PDDocument document) {
    init();
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

  public void init() {
    this.pageLines = new ArrayList<>();
    this.dividers = new ArrayList<>();
    this.strokePaths = new ArrayList<>();
    this.blocks = new ArrayList<>();
    this.currentPageBlocks = new ArrayList<>();
  }

  public void processPage(int pageIndex) {
    init();

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
      this.strokePaths = strokePathRenderer.getPaths();
      this.strokePaths.forEach(path -> {
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
   *  | 1 |  -----   |2 |
   *  |   |  | 3 |   |  |
   *  |   |  -----   |  |
   *  |   |          ----
   *  |   |
   *  |   |  ------------
   *  |   |  |    4     |
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
