package pdftohtml.domain.pdf.object.container;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.common.DocumentMetadata;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class Block extends PdfDocumentObject {

    private List<PageLine> lines;

    private DocumentMetadata documentMetadata;

    public Block() {
        this.lines = new ArrayList<>();
        this.rectangle = new FrameworkRectangle();
    }

    public Block(
            List<PageLine> lines,
            FrameworkRectangle rectangle,
            DocumentMetadata documentMetadata
    ) {
        this.lines = lines;
        this.rectangle = rectangle;
        this.documentMetadata = documentMetadata;
    }

    /**
     * Adding a line {@link PageLine} to the block
     *
     * 1) If the line rectangle intersects with block
     * rectangle, it means that this line should be inserted in the middle of
     * block lines list:
     * - checking the place of the line in a block by comparing Y coordinates
     * - if found, insert the line to the specifying place
     *
     * 2) If line rectangle does not intersect block rectangle, then add
     * line to the end of block lines list
     *
     * @param line - line to add to block lines list
     */
    public void addLine(PageLine line) {
        if (this.rectangle.getMaxY() >= line.getRectangle().getMinY()) {
            for (int lineIndex = 0; lineIndex < this.lines.size(); lineIndex++) {
                PageLine lineFromBlock = this.lines.get(lineIndex);
                if (lineFromBlock.getRectangle().getMinY() >= line.getRectangle().getMinY()) {
                    this.lines.add(lineIndex, line);
                    break;
                }
            }
        } else this.lines.add(line);
        instantiateContentRectangle();
    }

    public void setLines(List<PageLine> lines) {
        this.lines = lines;
        instantiateContentRectangle();
    }

    private void instantiateContentRectangle() {
        double minX =
                lines.stream()
                        .min(Comparator.comparingDouble(line -> line.getRectangle().getMinX()))
                        .map(line -> line.getRectangle().getMinX())
                        .orElse(0.0);
        double maxX =
                lines.stream()
                        .max(Comparator.comparingDouble(line -> line.getRectangle().getMaxX()))
                        .map(line -> line.getRectangle().getMaxX())
                        .orElse(0.0);
        double minY =
                lines.stream()
                        .min(Comparator.comparingDouble(line -> line.getRectangle().getMinY()))
                        .map(line -> line.getRectangle().getMinY())
                        .orElse(0.0);
        double maxY =
                lines.stream()
                        .max(Comparator.comparingDouble(line -> line.getRectangle().getMaxY()))
                        .map(line -> line.getRectangle().getMaxY())
                        .orElse(0.0);
        this.rectangle.setRectangleCoordinates(minX, minY, maxX, maxY);
    }

    public Block copy() {
        return new Block(
                this.getLines(),
                this.getRectangle(),
                this.getDocumentMetadata()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return lines.equals(block.lines) &&
                rectangle.equals(block.rectangle);
    }

    @Override
    public int hashCode() {
        int linesHashcode = this.lines.stream().mapToInt(PageLine::hashCode).sum();
        return linesHashcode +
                rectangle.hashCode();
    }
}
