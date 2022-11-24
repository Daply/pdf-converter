package pdftohtml.domain.pdf.object.mediate;

public class MediateObject {

    public MediateObjectType type = MediateObjectType.NOT_SET;

    public MediateObjectType getType() {
        return type;
    }

    public void setType(MediateObjectType type) {
        this.type = type;
    }
}
