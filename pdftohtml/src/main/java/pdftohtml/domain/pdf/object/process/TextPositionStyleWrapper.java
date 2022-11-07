package pdftohtml.domain.pdf.object.process;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

public class TextPositionStyleWrapper {

    private PDColor strokingColor;
    private PDColor nonStrokingColor;
    private RenderingMode renderingMode;

    private boolean underlinedText;
    private boolean strikeThroughText;
    private boolean italicText;
    private boolean boldText;
    private int rotated;

    public PDColor getStrokingColor() {
        return strokingColor;
    }

    public void setStrokingColor(PDColor strokingColor) {
        this.strokingColor = strokingColor;
    }

    public PDColor getNonStrokingColor() {
        return nonStrokingColor;
    }

    public void setNonStrokingColor(PDColor nonStrokingColor) {
        this.nonStrokingColor = nonStrokingColor;
    }

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    public boolean isUnderlinedText() {
        return underlinedText;
    }

    public void setUnderlinedText(boolean underlinedText) {
        this.underlinedText = underlinedText;
    }

    public boolean isStrikeThroughText() {
        return strikeThroughText;
    }

    public void setStrikeThroughText(boolean strikeThroughText) {
        this.strikeThroughText = strikeThroughText;
    }

    public boolean isItalicText() {
        return italicText;
    }

    public void setItalicText(boolean italicText) {
        this.italicText = italicText;
    }

    public boolean isBoldText() {
        return boldText;
    }

    public void setBoldText(boolean boldText) {
        this.boldText = boldText;
    }

    public int getRotated() {
        return rotated;
    }

    public void setRotated(int rotated) {
        this.rotated = rotated;
    }
}
