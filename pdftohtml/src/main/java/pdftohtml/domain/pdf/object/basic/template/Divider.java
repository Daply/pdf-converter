package pdftohtml.domain.pdf.object.basic.template;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pdftohtml.domain.common.FrameworkRectangle;

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

    /**
     * If a divider is visible
     */
    private boolean visible;

    /**
     * If a divider is horizontal
     */
    private boolean horizontal;

    public Divider copy() {
        return new Divider(
                this.rectangle,
                this.pageBorder,
                this.visible,
                this.horizontal
        );
    }
}
