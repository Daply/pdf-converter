package pdftohtml.processors.html;

import pdftohtml.domain.pdf.object.mediate.MediateObject;
import pdftohtml.domain.pdf.object.mediate.MediateObjectType;

import java.util.List;

public class HtmlProcessor {

    public String process(List<MediateObject> mediateObjects) {
        StringBuilder content = new StringBuilder();
         for (MediateObject object: mediateObjects) {
             content.append(processObject(object));
         }
         return content.toString();
    }

    private String processObject(MediateObject mediateObject) {
        HtmlTagProcessor htmlTagProcessor;
        if (mediateObject.getType().equals(MediateObjectType.PARAGRAPH)) {
            htmlTagProcessor = new ParagraphProcessor();
            return htmlTagProcessor.process(mediateObject);
        }
        if (mediateObject.getType().equals(MediateObjectType.LIST)) {
            htmlTagProcessor = new ItemsListProcessor();
            return htmlTagProcessor.process(mediateObject);
        }
        return "";
    }


}
