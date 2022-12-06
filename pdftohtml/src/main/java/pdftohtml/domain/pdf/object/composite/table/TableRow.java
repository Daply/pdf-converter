package pdftohtml.domain.pdf.object.composite.table;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pdftohtml.common.helpers.RectangleHelper.combineTwoRectangles;

@Getter
@Setter
public class TableRow extends PdfDocumentObject {

    private List<TableCell> cells;

    /**
     * Row order number in a table
     */
    private int number;

    public TableRow() {
        this.cells = new ArrayList<>();
        this.objectType = PdfDocumentObjectType.TABLE_ROW;
    }

    public void addCell(TableCell cell) {
        this.cells.add(cell);
    }

    public boolean isEmpty() {
        return this.cells.isEmpty();
    }

    public void resolveRectangle() {
        AtomicReference<FrameworkRectangle> resultRectangle = new AtomicReference<>();
        cells.forEach(cell -> {
            if (resultRectangle.get() == null) {
                resultRectangle.set(cell.getRectangle());
            } else {
                resultRectangle.set(combineTwoRectangles(resultRectangle.get(), cell.getRectangle()));
            }
        });
        this.rectangle = resultRectangle.get();
    }
}
