package pdftohtml.processors.html;

import pdftohtml.domain.pdf.object.middleware.MiddlewareObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MiddlewareObject middlewareObject);

}
