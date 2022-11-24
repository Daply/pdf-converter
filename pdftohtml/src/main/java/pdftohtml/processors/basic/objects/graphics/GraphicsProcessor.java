package pdftohtml.processors.basic.objects.graphics;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.basic.GraphicsObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GraphicsProcessor extends PDFStreamEngine {

  @Setter
  private int pageIndex;

  private double pageWidth = 0;
  private double pageHeight = 0;

  private List<GraphicsObject> graphicsObjects = new ArrayList<>();

  public GraphicsProcessor() throws IOException {
    addOperator(new Concatenate());
    addOperator(new DrawObject());
    addOperator(new SetGraphicsStateParameters());
    addOperator(new Save());
    addOperator(new Restore());
    addOperator(new SetMatrix());
  }

  @Override
  public void processPage(PDPage page) throws IOException {
    this.pageWidth = page.getCropBox().getWidth();
    this.pageHeight = page.getCropBox().getHeight();
    super.processPage(page);
  }

  @Override
  protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
    String operation = operator.getName();
    if ("Do".equals(operation)) {
      COSName objectName = (COSName) operands.get(0);
      PDXObject xObject = getResources().getXObject(objectName);
      if (xObject instanceof PDImageXObject) {
        PDImageXObject imageXObject = (PDImageXObject) xObject;
        Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();

        float imageXScale = ctmNew.getScalingFactorX();
        float imageYScale = ctmNew.getScalingFactorY();
        GraphicsObject graphicsObject = new GraphicsObject();
        graphicsObject.setRectangle(
                new FrameworkRectangle(
                        ctmNew.getTranslateX(),
                        this.pageHeight - ctmNew.getTranslateY() - imageYScale,
                        imageXScale, imageYScale)
        );
        graphicsObject.setImage(imageXObject.getImage());
        graphicsObjects.add(graphicsObject);

        // position in user space units. 1 unit = 1/72 inch at 72 dpi
        //System.out.println("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
        // displayed size in user space units
        //System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in user space units");
      } else if (xObject instanceof PDFormXObject) {
        PDFormXObject form = (PDFormXObject) xObject;
        showForm(form);
      }
    } else {
      super.processOperator(operator, operands);
    }
  }
}
