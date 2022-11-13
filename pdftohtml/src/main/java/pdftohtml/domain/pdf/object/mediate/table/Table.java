package pdftohtml.domain.pdf.object.mediate.table;

import pdftohtml.domain.pdf.object.mediate.MiddlewareObject;
import pdftohtml.domain.pdf.object.mediate.MiddlewareObjectType;

import java.util.ArrayList;
import java.util.List;

public class Table extends MiddlewareObject {

    private List<TableRow> rows;

    public Table() {
        this.rows = new ArrayList<>();
        type = MiddlewareObjectType.TABLE;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public void addRow(TableRow row) {
        this.rows.add(row);
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }
}
