package pdftohtml.domain.pdf.object.basic;

public class LinkObject extends TextObject {

    private String linkSource;

    public LinkObject() {
        super();
        this.objectType = PdfDocumentObjectType.LINK;
    }

    public String getLinkSource() {
        return linkSource;
    }

    public void setLinkSource(String linkSource) {
        this.linkSource = linkSource;
    }

}
