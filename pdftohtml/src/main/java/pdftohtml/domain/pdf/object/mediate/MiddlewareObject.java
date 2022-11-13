package pdftohtml.domain.pdf.object.mediate;

public class MiddlewareObject {

    public MiddlewareObjectType type = MiddlewareObjectType.NOT_SET;

    public MiddlewareObjectType getType() {
        return type;
    }

    public void setType(MiddlewareObjectType type) {
        this.type = type;
    }
}
