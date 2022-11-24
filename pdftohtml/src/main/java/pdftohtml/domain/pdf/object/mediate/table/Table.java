package pdftohtml.domain.pdf.object.mediate.table;

import pdftohtml.domain.pdf.object.mediate.MediateObject;
import pdftohtml.domain.pdf.object.mediate.MediateObjectType;

import java.util.ArrayList;
import java.util.List;

public class Table extends MediateObject {

    private List<TableRow> rows;

    public Table() {
        this.rows = new ArrayList<>();
        type = MediateObjectType.TABLE;
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
