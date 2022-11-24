package pdftohtml.helpers.testing;

import pdftohtml.domain.pdf.object.basic.PdfDocumentObject;
import pdftohtml.domain.pdf.object.basic.container.PageLine;

import java.util.List;

public class LineObjectsPrinter {

    public static void printLinesObjects(List<PageLine> pageLines) {
        for (PageLine line: pageLines) {
            printLineObjects(line);
        }
    }

    public static void printLineObjects(PageLine line) {
        for (PdfDocumentObject obj: line.getObjects()) {
            //System.out.println(obj);
        }
    }

}
