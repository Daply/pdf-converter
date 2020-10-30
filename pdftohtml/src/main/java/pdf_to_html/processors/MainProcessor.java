package pdf_to_html.processors;

import org.apache.pdfbox.pdmodel.PDDocument;
import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.processors.html.HtmlProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainProcessor {

    private List<MiddlewareObject> middlewareObjects = new ArrayList<>();

    private SkeletonsProcessor processor;

    public MainProcessor() {
        this.processor = new SkeletonsProcessor();
    }

    public void process(String path) throws IOException {
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        PageLinesProcessor pageLinesProcessor = new PageLinesProcessor(document);
        for (int pageIndex = 1; pageIndex <= document.getPages().getCount(); pageIndex++) {
            pageLinesProcessor.processPage(pageIndex);
            this.processor.processMiddlewareObjects(pageLinesProcessor.getSkeletons());

            // TODO
            // here must be union of objects of current page with objects of previous page

            middlewareObjects.addAll(this.processor.getPageMiddlewareObjects());
        }
        HtmlProcessor htmlProcessor = new HtmlProcessor();
        String result = htmlProcessor.process(middlewareObjects);
        System.out.println(result);
    }

}
