package pdftohtml.domain.pdfdocument.object.middleware;

import pdftohtml.domain.pdfdocument.object.process.TextObject;

import java.util.ArrayList;
import java.util.List;

public class TextParagraph extends MiddlewareObject {

    private String text;

    private float marginLeft;
    private float marginTop;
    private float marginRight;
    private float marginBottom;

    private List<TextObject> textObjects;

    public TextParagraph() {
        this.text = "";
        this.textObjects = new ArrayList<>();
        this.type = MiddlewareObjectType.PARAGRAPH;
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
