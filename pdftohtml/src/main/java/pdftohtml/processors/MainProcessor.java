package pdftohtml.processors;

import org.apache.pdfbox.pdmodel.PDDocument;
import pdftohtml.domain.pdf.object.deprecated.MediateObject;
import pdftohtml.processors.pdf.objects.composite.PageDataBlocksProcessor;
import pdftohtml.processors.html.HtmlProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainProcessor {

    private List<MediateObject> mediateObjects = new ArrayList<>();

    public MainProcessor() {
    }

    public void process(String path) throws IOException {
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        PageDataBlocksProcessor pageDataBlocksProcessor = new PageDataBlocksProcessor(document);
        for (int pageIndex = 1; pageIndex <= document.getPages().getCount(); pageIndex++) {
            pageDataBlocksProcessor.processPage(pageIndex);
            //this.processor.processMiddlewareObjects(pageLinesProcessor.getSkeletons());

            // TODO
            // here must be union of objects of current page with objects of previous page

            //middlewareObjects.addAll(this.processor.getPageMiddlewareObjects());
        }
        HtmlProcessor htmlProcessor = new HtmlProcessor();
        String result = htmlProcessor.process(mediateObjects);
        //System.out.println(result);
    }

}
