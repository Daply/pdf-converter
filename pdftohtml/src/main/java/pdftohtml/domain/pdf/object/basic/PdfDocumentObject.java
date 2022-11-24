package pdftohtml.domain.pdf.object.basic;

import pdftohtml.domain.common.FrameworkRectangle;

import static pdftohtml.helpers.RectangleHelper.combineTwoRectangles;

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
    this.rectangle = combineTwoRectangles(this.rectangle, rectangle);
  }

  public PdfDocumentObjectType getObjectType() {
    return objectType;
  }

  public void setObjectType(PdfDocumentObjectType objectType) {
    this.objectType = objectType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PdfDocumentObject that = (PdfDocumentObject) o;
    return rectangle.equals(that.rectangle) && objectType == that.objectType;
  }

  @Override
  public int hashCode() {
    return rectangle.hashCode() +
            objectType.ordinal();
  }
}
