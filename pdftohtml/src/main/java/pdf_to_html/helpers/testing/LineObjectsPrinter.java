package pdf_to_html.helpers.testing;

import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.container.PageLine;

import java.util.List;

public class LineObjectsPrinter {

    public static void printLinesObjects(List<PageLine> pageLines) {
        for (PageLine line: pageLines) {
            printLineObjects(line);
        }
    }

    public static void printLineObjects(PageLine line) {
        for (PdfDocumentObject obj: line.getObjects()) {
            System.out.println(obj);
        }
    }

}
