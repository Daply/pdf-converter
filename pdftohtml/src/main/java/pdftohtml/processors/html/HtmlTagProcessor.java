package pdftohtml.processors.html;

import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MiddlewareObject middlewareObject);

}
