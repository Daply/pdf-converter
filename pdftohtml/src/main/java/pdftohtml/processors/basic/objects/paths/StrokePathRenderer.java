package pdftohtml.processors.basic;

import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import pdftohtml.domain.framework.FrameworkRectangle;
import pdftohtml.processors.PrintPaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class StrokePathRenderer extends PDFRenderer {

    private PrintPaths pageDrawer;
    private List<FrameworkRectangle> paths = new ArrayList<>();
    private double pageWidth = 0;
    private double pageHeight = 0;

    public StrokePathRenderer(PDDocument document)
    {
        super(document);
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
    {
        this.pageDrawer = new PrintPaths(parameters, pageWidth, pageHeight);
        return this.pageDrawer;
    }

    public void extractPaths(int pageIndex) throws IOException {
        PDRectangle cropBox = this.document.getPage(pageIndex).getCropBox();
        this.pageWidth = cropBox.getWidth();
        this.pageHeight = cropBox.getHeight();
        super.renderImage(pageIndex);
        this.paths = this.pageDrawer.getPaths();
    }
}