package pdftohtml.domain.pdf.object.process.template;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pdftohtml.domain.framework.FrameworkRectangle;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Divider {

    /**
     * Rectangle of empty space between pdf objects
     */
    private FrameworkRectangle rectangle;

    /**
     * If a divider is a border of content on a page
     */
    private boolean pageBorder;

    public Divider copy() {
        return new Divider(this.rectangle, this.pageBorder);
    }
}
