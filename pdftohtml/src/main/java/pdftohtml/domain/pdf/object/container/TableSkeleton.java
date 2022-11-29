package pdftohtml.domain.pdf.object.container;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pdftohtml.common.Properties;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.composite.table.Table;
import pdftohtml.domain.pdf.object.composite.table.TableCell;
import pdftohtml.domain.pdf.object.composite.table.TableRow;
import pdftohtml.domain.pdf.object.template.Divider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@NoArgsConstructor
public class TableSkeleton extends PdfDocumentObject {

    private List<Divider> dividers;

    private List<Block> blocks;

    public void resolveRectangle() {
        AtomicReference<Double> yMin = new AtomicReference<>((double) 0);
        AtomicReference<Double> yMax = new AtomicReference<>((double) 0);
        this.dividers.forEach(d -> {
            yMin.set(
                    yMin.get() == 0 ?
                            d.getRectangle().getMinY() :
                            Math.min(yMin.get(), d.getRectangle().getMinY())
            );
            yMax.set(
                    yMax.get() == 0 ?
                            d.getRectangle().getMaxY() :
                            Math.max(yMax.get(), d.getRectangle().getMaxY())
            );
        });
        AtomicReference<Double> xMin = new AtomicReference<>((double) 0);
        AtomicReference<Double> xMax = new AtomicReference<>((double) 0);
        this.blocks.forEach(b -> {
            xMin.set(
                    xMin.get() == 0 ?
                            b.getRectangle().getMinX() :
                            Math.min(xMin.get(), b.getRectangle().getMinX())
            );
            xMax.set(
                    xMax.get() == 0 ?
                            b.getRectangle().getMaxX() :
                            Math.max(xMax.get(), b.getRectangle().getMaxX())
            );
        });
        this.rectangle = new FrameworkRectangle(
                xMin.get(),
                yMin.get(),
                xMax.get() - xMin.get(),
                yMax.get() - yMin.get()
        );
    }

    public Table convertToTable() {
        Divider lastDivider = this.dividers.get(this.dividers.size() - 1);
        Table table = new Table();
        List<TableRow> rows = new ArrayList<>();
        this.blocks.forEach(block -> {
            List<Block> sameYMinBlocks = new ArrayList<>();
            sameYMinBlocks.add(block);
            for (Block sameYMinBlock: this.blocks) {
                if (block.getRectangle()
                        .hasSameYCoordinatesWithYInaccuracy(
                                sameYMinBlock.getRectangle(),
                                Properties.yInaccuracy
                        ) && !block.equals(sameYMinBlock)) {
                    sameYMinBlocks.add(sameYMinBlock);
                }
            }
            List<TableCell> cells = new ArrayList<>();
            for (Divider divider: this.dividers) {
                TableCell cell = new TableCell();
                sameYMinBlocks.forEach(b -> {
                    if (b.getRectangle()
                            .isBeforeHorizontallyWithXInaccuracy(
                                    divider.getRectangle(),
                                    Properties.xInaccuracy
                            )
                    ) {
                       cell.addObject(b);
                   }
                });
                if (!cell.isEmpty()) {
                    cell.resolveRectangle();
                    cells.add(cell);
                }
            }

            ////
            TableCell cell = new TableCell();
            sameYMinBlocks.forEach(b -> {
                if (b.getRectangle()
                        .isAfterHorizontallyWithXInaccuracy(
                                lastDivider.getRectangle(),
                                Properties.xInaccuracy
                        )
                ) {
                    cell.addObject(b);
                }
            });
            if (!cell.isEmpty()) {
                cell.resolveRectangle();
                cells.add(cell);
            }
            ////



            if (!cells.isEmpty()) {
                TableRow row = new TableRow();
                row.setCells(cells);
                row.resolveRectangle();
                rows.add(row);
            }
        });
        table.setRows(rows);
        table.resolveRectangle();
        return table;
    }
}
