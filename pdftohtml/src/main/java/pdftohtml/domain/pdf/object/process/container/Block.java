package pdftohtml.domain.pdf.object.process.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pdftohtml.domain.common.DocumentMetadata;
import pdftohtml.domain.framework.FrameworkRectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class Block {

    private List<PageLine> lines;

    private FrameworkRectangle borderRectangle;

    private FrameworkRectangle contentRectangle;

    private DocumentMetadata documentMetadata;

    public Block() {
        this.lines = new ArrayList<>();
        this.borderRectangle = new FrameworkRectangle();
        this.contentRectangle = new FrameworkRectangle();
    }

    /**
     * Adding a line {@link PageLine} to the block
     *
     * <p>1) If the line rectangle intersects with block
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
        if (this.contentRectangle.getMaxY() >= line.getRectangle().getMinY()) {
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
                        .orElse(this.borderRectangle.getMinX());
        double maxX =
                lines.stream()
                        .max(Comparator.comparingDouble(line -> line.getRectangle().getMaxX()))
                        .map(line -> line.getRectangle().getMaxX())
                        .orElse(this.borderRectangle.getMaxX());
        double minY =
                lines.stream()
                        .min(Comparator.comparingDouble(line -> line.getRectangle().getMinY()))
                        .map(line -> line.getRectangle().getMinY())
                        .orElse(this.borderRectangle.getMinY());
        double maxY =
                lines.stream()
                        .max(Comparator.comparingDouble(line -> line.getRectangle().getMaxY()))
                        .map(line -> line.getRectangle().getMaxY())
                        .orElse(this.getBorderRectangle().getMaxY());
        this.contentRectangle.setRectangleCoordinates(minX, minY, maxX, maxY);
    }

    public Block copy() {
        return new Block(
                this.getLines(),
                this.getBorderRectangle(),
                this.getContentRectangle(),
                this.getDocumentMetadata()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return lines.equals(block.lines) &&
                borderRectangle.equals(block.borderRectangle) &&
                contentRectangle.equals(block.contentRectangle);
    }

    @Override
    public int hashCode() {
        int linesHashcode = this.lines.stream().mapToInt(PageLine::hashCode).sum();
        return linesHashcode +
                borderRectangle.hashCode() +
                contentRectangle.hashCode();
    }
}
