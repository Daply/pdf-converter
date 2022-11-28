package pdftohtml.processors.html;

import pdftohtml.domain.pdf.object.deprecated.MediateObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MediateObject mediateObject);

}
