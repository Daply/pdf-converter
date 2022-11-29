package pdftohtml.domain.pdf.object.composite.table;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pdftohtml.helpers.RectangleHelper.combineTwoRectangles;

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
}
