package pdf_to_html.entities.pdfdocument.object.process;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class GraphicsObject extends PdfDocumentObject {

  private BufferedImage image;

}
