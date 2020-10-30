package pdf_to_html.entities.pdfdocument.object.process.template;


import pdf_to_html.entities.framework.Rectangle2D;

public class Divider {

    /**
     * Rectangle of empty space between pdf objects
     */
    private Rectangle2D rectangle;

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
    private Rectangle2D borderRectangle;

    /**
     * If a divider is a border of content on a page
     */
    private boolean pageBorder;

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle2D rectangle) {
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

    public Rectangle2D getBorderRectangle() {
        return borderRectangle;
    }

    public void setBorderRectangle(Rectangle2D borderRectangle) {
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

}
