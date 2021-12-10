package pdftohtml;

import pdftohtml.processors.MainProcessor;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class FileReader {

    public static void readFile(String path) throws IOException, ParserConfigurationException {
        MainProcessor mainProcessor = new MainProcessor();
        mainProcessor.process(path);
//        PDDocument pdf = PDDocument.load(new File(path));
//        Writer output = new PrintWriter("src//pdf_to_html//pdf.html", "utf-8");
//        new PDFDomTree().writeText(pdf, output);
//
//        output.close();
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException {
        readFile("src//main//resources//list_test.pdf");
    }

}
