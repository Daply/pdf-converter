package pdftohtml.domain.pdfdocument.object.process.template;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pdftohtml.domain.framework.Rectangle;

@AllArgsConstructor
@NoArgsConstructor
public class Divider {

    /**
     * Rectangle of empty space between pdf objects
     */
    private Rectangle rectangle;

    /**
     * First line number, that divider takes
     */
    private int numberOfFirstLine;

    /**
     * Last line number, that divider takes
     */
    private int numberOfLastLine;

    /**
     * Rectangle of area, that divider takes
     * (rectangle of possible large pdf object,
     * such as list or table)
     */
    private Rectangle borderRectangle;

    /**
     * If a divider is a border of content on a page
     */
    private boolean pageBorder;

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public int getNumberOfFirstLine() {
        return numberOfFirstLine;
    }

    public void setNumberOfFirstLine(int numberOfFirstLine) {
        this.numberOfFirstLine = numberOfFirstLine;
    }

    public int getNumberOfLastLine() {
        return numberOfLastLine;
    }

    public void setNumberOfLastLine(int numberOfLastLine) {
        this.numberOfLastLine = numberOfLastLine;
    }

    public Rectangle getBorderRectangle() {
        return borderRectangle;
    }

    public void setBorderRectangle(Rectangle borderRectangle) {
        this.borderRectangle = borderRectangle;
    }

    public boolean isPageBorder() {
        return pageBorder;
    }

    public void setPageBorder(boolean pageBorder) {
        this.pageBorder = pageBorder;
    }

    public boolean isDividerLessOtherDivider(Divider secondDivider) {
        return (this.getNumberOfFirstLine() > secondDivider.getNumberOfFirstLine() &&
                this.getNumberOfLastLine() < secondDivider.getNumberOfLastLine()) ||
                (this.getNumberOfFirstLine() == secondDivider.getNumberOfFirstLine() &&
                        this.getNumberOfLastLine() < secondDivider.getNumberOfLastLine()) ||
                (this.getNumberOfFirstLine() > secondDivider.getNumberOfFirstLine() &&
                        this.getNumberOfLastLine() == secondDivider.getNumberOfLastLine());
    }

    public Divider copy() {
        return new Divider(this.rectangle, this.numberOfFirstLine,
                this.numberOfLastLine, this.borderRectangle, this.pageBorder);
    }
}
