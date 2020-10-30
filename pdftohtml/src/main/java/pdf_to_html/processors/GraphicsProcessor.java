package pdf_to_html.processors;

import lombok.Data;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import pdf_to_html.entities.pdfdocument.object.process.GraphicsObject;
import pdf_to_html.helpers.Rectangle2DHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class GraphicsProcessor {

  private Rectangle2DHelper rectangle2DHelper = new Rectangle2DHelper();

  private List<GraphicsObject> images = new ArrayList<>();

  public void getAllGraphics(PDDocument document) throws IOException {
    for (PDPage page : document.getPages()) {
      images.addAll(getImagesFromResources(page.getResources()));
    }
  }

  private List<GraphicsObject> getImagesFromResources(PDResources resources) throws IOException {
    List<GraphicsObject> images = new ArrayList<>();

    for (COSName xObjectName : resources.getXObjectNames()) {
      PDXObject xObject = resources.getXObject(xObjectName);
      PDRectangle pdRectangle = getBBox(xObject);
      if (xObject instanceof PDFormXObject) {
        images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
      } else if (xObject instanceof PDImageXObject) {
        GraphicsObject graphicsObject = new GraphicsObject();
        graphicsObject.setRectangle(rectangle2DHelper.convertPDRectangleToRectangle2D(pdRectangle));
        graphicsObject.setImage(((PDImageXObject) xObject).getImage());
        images.add(graphicsObject);
      }
    }

    return images;
  }

  private PDRectangle getBBox(PDXObject object) {
    PDRectangle retval = null;
    COSArray array = (COSArray)object.getCOSObject().getDictionaryObject(COSName.BBOX);
    if (array != null) {
      retval = new PDRectangle(array);
    }

    return retval;
  }

}
