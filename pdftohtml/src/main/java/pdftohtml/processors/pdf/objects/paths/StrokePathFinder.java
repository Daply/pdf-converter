package pdftohtml.processors.pdf.objects.paths;

import lombok.Getter;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import pdftohtml.domain.common.FrameworkRectangle;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class StrokePathFinder extends PageDrawer {

    private List<FrameworkRectangle> paths = new ArrayList<>();
    private double pageWidth = 0;
    private double pageHeight = 0;

    public StrokePathFinder(
            PageDrawerParameters parameters,
            double pageWidth,
            double pageHeight
    ) throws IOException {
        super(parameters);
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    @Override
    public void drawImage(PDImage pdImage) {

    }

    @Override
    public void shadingFill(COSName shadingName) {

    }

    @Override
    public void processPage(PDPage page) throws IOException {
        super.processPage(page);
    }

    @Override
    public void fillPath(int windingRule) {
        printPath();
        //System.out.printf("Fill; windingrule: %s\n\n", windingRule);
        getLinePath().reset();
    }

    @Override
    public void strokePath() throws IOException
    {
        printPath();
        //System.out.printf("Stroke; unscaled width: %s\n\n", getGraphicsState().getLineWidth());
        getLinePath().reset();
    }

    void printPath()
    {
        GeneralPath path = getLinePath();
        PathIterator pathIterator = path.getPathIterator(null);
        double x = 0, y = 0;
        double coords[] = new double[6];

        double rectX = 0;
        double rectY = 0;

        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    //System.out.printf("Move to (%s %s)\n", coords[0], fixY(coords[1]));
                    x = coords[0];
                    y = coords[1];
                    rectX = x;
                    rectY = fixY(y);
                    break;
                case PathIterator.SEG_LINETO:
                    double width = getEffectiveWidth(coords[0] - x, coords[1] - y);
                    //System.out.printf("Line to (%s %s), scaled width %s\n", coords[0], fixY(coords[1]), width);
                    x = coords[0];
                    y = coords[1];
                    FrameworkRectangle rectangle = new FrameworkRectangle(
                            rectX,
                            rectY,
                            Math.abs(x - rectX),
                            Math.abs(fixY(y) - rectY)
                    );
                    paths.add(rectangle);
                    break;
                case PathIterator.SEG_QUADTO:
                    //System.out.printf("Quad along (%s %s) and (%s %s)\n", coords[0], fixY(coords[1]), coords[2], fixY(coords[3]));
                    x = coords[2];
                    y = coords[3];
                    break;
                case PathIterator.SEG_CUBICTO:
                    //System.out.printf("Cubic along (%s %s), (%s %s), and (%s %s)\n", coords[0], fixY(coords[1]), coords[2], fixY(coords[3]), coords[4], fixY(coords[5]));
                    x = coords[4];
                    y = coords[5];
                    break;
                case PathIterator.SEG_CLOSE:
                    //System.out.println("Close path");
            }

            pathIterator.next();
        }
    }

    double fixY(double y) {
        return pageHeight - y;
    }

    double getEffectiveWidth(double dirX, double dirY)
    {
        if (dirX == 0 && dirY == 0)
            return 0;
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        double widthX = dirY;
        double widthY = -dirX;
        double widthXTransformed = widthX * ctm.getValue(0, 0) + widthY * ctm.getValue(1, 0);
        double widthYTransformed = widthX * ctm.getValue(0, 1) + widthY * ctm.getValue(1, 1);

        double factor = Math.sqrt((widthXTransformed*widthXTransformed + widthYTransformed*widthYTransformed) / (widthX*widthX + widthY*widthY));
        return getGraphicsState().getLineWidth() * factor;
    }
}
