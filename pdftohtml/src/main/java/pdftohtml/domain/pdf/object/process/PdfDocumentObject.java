package pdftohtml.domain.pdfdocument.object.process;

import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.helpers.RectangleHelper;

public abstract class PdfDocumentObject {

  protected FrameworkRectangle rectangle;

  protected PdfDocumentObjectType objectType;

  public FrameworkRectangle getRectangle() {
    return rectangle;
  }

  public void setRectangle(FrameworkRectangle rectangle) {
    this.rectangle = rectangle;
  }

  public void addToRectangle(FrameworkRectangle rectangle) {
    RectangleHelper helper = new RectangleHelper();
    this.rectangle = helper.combineTwoRectangles(this.rectangle, rectangle);
  }

  public PdfDocumentObjectType getObjectType() {
    return objectType;
  }

  public void setObjectType(PdfDocumentObjectType objectType) {
    this.objectType = objectType;
  }

}
