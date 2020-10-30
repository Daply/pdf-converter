package pdf_to_html.processors.html;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObjectType;

import java.util.List;

public class HtmlProcessor {

    public String process(List<MiddlewareObject> middlewareObjects) {
        StringBuilder content = new StringBuilder();
         for (MiddlewareObject object: middlewareObjects) {
             content.append(processObject(object));
         }
         return content.toString();
    }

    private String processObject(MiddlewareObject middlewareObject) {
        HtmlTagProcessor htmlTagProcessor;
        if (middlewareObject.getType().equals(MiddlewareObjectType.PARAGRAPH)) {
            htmlTagProcessor = new ParagraphProcessor();
            return htmlTagProcessor.process(middlewareObject);
        }
        if (middlewareObject.getType().equals(MiddlewareObjectType.LIST)) {
            htmlTagProcessor = new ItemsListProcessor();
            return htmlTagProcessor.process(middlewareObject);
        }
        return "";
    }


}
