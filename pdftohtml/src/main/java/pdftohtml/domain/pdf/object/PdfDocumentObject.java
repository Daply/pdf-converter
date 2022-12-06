package pdftohtml.domain.pdf.object;

import lombok.Getter;
import lombok.Setter;
import pdftohtml.domain.common.FrameworkRectangle;

import static pdftohtml.common.helpers.RectangleHelper.combineTwoRectangles;

@Getter
@Setter
public abstract class PdfDocumentObject {

  protected FrameworkRectangle rectangle = FrameworkRectangle.EMPTY;

  protected PdfDocumentObjectType objectType = PdfDocumentObjectType.NOT_SET;

  public void addToRectangle(FrameworkRectangle rectangle) {
    this.rectangle = combineTwoRectangles(this.rectangle, rectangle);
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
