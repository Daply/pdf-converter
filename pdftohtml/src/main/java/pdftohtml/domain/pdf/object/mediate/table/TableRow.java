package pdftohtml.domain.pdf.object.mediate.table;

import java.util.ArrayList;
import java.util.List;

public class TableRow {

    private List<TableCell> cells;

    public TableRow() {
        this.cells = new ArrayList<>();
    }

    public List<TableCell> getCells() {
        return cells;
    }

    public void addCell(TableCell cell) {
        this.cells.add(cell);
    }

    public void setCells(List<TableCell> cells) {
        this.cells = cells;
    }

}
