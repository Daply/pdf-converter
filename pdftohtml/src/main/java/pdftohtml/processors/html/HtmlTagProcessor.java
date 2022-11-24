package pdftohtml.processors.html;

import pdftohtml.domain.pdf.object.mediate.MediateObject;

public abstract class HtmlTagProcessor {

    public abstract String process(MediateObject mediateObject);

}
