package pdf_to_html.entities.pdfdocument.object.process.container;

import java.util.ArrayList;
import java.util.List;

public class Block {

    private List<PageLine> lines;

    public Block() {
        this.lines = new ArrayList<>();
    }

    public List<PageLine> getLines() {
        return lines;
    }

    public void addLine(PageLine line) {
        this.lines.add(line);
    }

    public void setLines(List<PageLine> lines) {
        this.lines = lines;
    }

}
