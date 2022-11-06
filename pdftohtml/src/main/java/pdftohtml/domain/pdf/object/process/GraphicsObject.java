package pdftohtml.domain.pdfdocument.object.process;

import lombok.Getter;
import lombok.Setter;

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
