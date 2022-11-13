package pdftohtml.processors.html;

import pdftohtml.domain.pdf.object.mediate.MiddlewareObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MiddlewareObject middlewareObject);

}
