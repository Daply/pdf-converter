package pdftohtml.domain.pdf.object.mediate.text;

import pdftohtml.domain.pdf.object.mediate.MediateObject;
import pdftohtml.domain.pdf.object.mediate.MediateObjectType;
import pdftohtml.domain.pdf.object.basic.TextObject;

import java.util.ArrayList;
import java.util.List;

public class TextParagraph extends MediateObject {

    private String text;

    private String fontFamily;

    private String fontName;

    private float fontSize;

    private int fontSizePt;

    private float fontWeight;

    private int[] color;

    private boolean underlinedText;

    private boolean strikeThroughText;

    private boolean italicText;

    private boolean boldText;

    private float marginLeft;
    private float marginTop;
    private float marginRight;
    private float marginBottom;

    private List<TextObject> textObjects;

    public TextParagraph() {
        this.text = "";
        this.textObjects = new ArrayList<>();
        this.type = MediateObjectType.PARAGRAPH;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public List<TextObject> getTextObjects() {
        return textObjects;
    }

    public void addTextObject(TextObject textObject) {
        this.textObjects.add(textObject);
    }

    public void setTextObjects(List<TextObject> textObjects) {
        this.textObjects = textObjects;
    }
}
