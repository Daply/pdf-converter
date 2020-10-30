package pdf_to_html.entities.pdfdocument.object.process;

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
