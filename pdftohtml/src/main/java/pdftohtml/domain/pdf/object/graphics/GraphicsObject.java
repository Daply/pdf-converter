package pdftohtml.domain.pdf.object.graphics;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.PdfDocumentObjectType;

import java.awt.image.BufferedImage;

@Getter
@Setter
public class GraphicsObject extends PdfDocumentObject {

  private BufferedImage image;

  public GraphicsObject() {
    this.objectType = PdfDocumentObjectType.GRAPHIC;
  }

  public GraphicsObject(BufferedImage image) {
    this.image = image;
    this.objectType = PdfDocumentObjectType.GRAPHIC;
  }
}
