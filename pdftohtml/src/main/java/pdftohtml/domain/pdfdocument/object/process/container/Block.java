package pdftohtml.domain.pdfdocument.object.process.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pdftohtml.domain.framework.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@AllArgsConstructor
public class Block {

    private List<PageLine> lines;

    private Rectangle borderRectangle;

    private Rectangle contentRectangle;

    public Block() {
        this.lines = new ArrayList<>();
        this.borderRectangle = new Rectangle();
        this.contentRectangle = new Rectangle();
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
        return new Block(this.getLines(), this.getBorderRectangle(), this.getContentRectangle());
    }
}
