package pdftohtml.domain.pdf.object.composite.table;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.common.Properties;
import pdftohtml.common.collections.ListUtils;
import pdftohtml.common.collections.Pair;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pdftohtml.common.helpers.RectangleHelper.combineTwoRectangles;

public class Table extends PdfDocumentObject {

    @Getter
    @Setter
    private List<TableRow> rows;

    public Table() {
        this.rows = new ArrayList<>();
        this.objectType = PdfDocumentObjectType.TABLE;
    }

    public void addRow(TableRow row) {
        this.rows.add(row);
    }

    public void resolveRectangle() {
        AtomicReference<FrameworkRectangle> resultRectangle = new AtomicReference<>();
        rows.forEach(row -> {
            if (resultRectangle.get() == null) {
                resultRectangle.set(row.getRectangle());
            } else {
                resultRectangle.set(combineTwoRectangles(resultRectangle.get(), row.getRectangle()));
            }
        });
        this.rectangle = resultRectangle.get();
    }

    /**
     *
     */
    public void mergeCellsFromAdjacentRows() {
        ListUtils<TableCell, TableCell> utils = new ListUtils<>();
        Iterator<TableRow> rowIterator = this.rows.iterator();
        while (rowIterator.hasNext()) {
            TableRow currentRow = rowIterator.next();
            if (rowIterator.hasNext()) {
                TableRow nextRow = rowIterator.next();
                List<Pair<TableCell, TableCell>> cellsPairs = utils.zip(
                  currentRow.getCells(),
                  nextRow.getCells(),
                        (a, b) -> a.getRectangle()
                                .hasTheSameYMinCoordinate(
                                        b.getRectangle()
                                )
                );
                cellsPairs.forEach(cellsPair -> {
                    TableCell firstCell = cellsPair.getFirst();
                    TableCell secondCell = cellsPair.getSecond();
                    if (firstCell.getNumber() > secondCell.getNumber()) {
                        firstCell.addAllObjects(secondCell.getObjects());
                        secondCell.cleanObjects();
                    }
                });
            }
        }
    }
}
