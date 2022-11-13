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
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\image_in_text.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\image_rounded_by_text.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\image_in_line.pdf");
    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\list_test.pdf");//!
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\table_test.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\invisible_border_table_test.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\table_with_borders_test.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\tables_spaces_test.pdf");
//    readFile("D:\\Dashas stuff\\projects\\pdf-to-html\\pdftohtml\\src\\main\\resources\\test_image_n_fonts.pdf");
  }

}
