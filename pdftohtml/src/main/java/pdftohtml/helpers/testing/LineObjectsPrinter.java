package pdftohtml.helpers.testing;

import pdftohtml.domain.pdf.object.process.PdfDocumentObject;
import pdftohtml.domain.pdf.object.process.container.PageLine;

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
