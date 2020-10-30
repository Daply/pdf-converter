package pdf_to_html.processors.html;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MiddlewareObject middlewareObject);

}
