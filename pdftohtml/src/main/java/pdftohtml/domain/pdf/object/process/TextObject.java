package pdftohtml.domain.pdfdocument.object.process;

import pdftohtml.helpers.TextObjectTypeResolver;

public class TextObject extends PdfDocumentObject {

    private StringBuffer textContent;

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

    /**
     * -90, 0, 90, 180
     * left, not_rotated, right, reverted
     */
    private int rotated = 0;

    private TextObjectType textObjectType;

    public TextObject() {
        this.textContent = new StringBuffer();
        this.color = new int[3];
        this.objectType = PdfDocumentObjectType.SIMPLE_TEXT;
        this.textObjectType = TextObjectType.TEXT;
    }

    public void resolveTextObjectType() {
        TextObjectTypeResolver resolver = new TextObjectTypeResolver();
        this.textObjectType = resolver.resolveType(this.textContent.toString());
    }

    public void addToTextContent(String strToAdd) {
        this.textContent.append(strToAdd);
        resolveTextObjectType();
    }

    public String getText() {
        return textContent.toString();
    }

    public void setTextContent(StringBuffer textContent) {
        this.textContent = textContent;
        resolveTextObjectType();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSizePt() {
        return fontSizePt;
    }

    public void setFontSizePt(int fontSizePt) {
        this.fontSizePt = fontSizePt;
    }

    public float getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(float fontWeight) {
        this.fontWeight = fontWeight;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public void setColor(float[] color) {
        if (color.length == 3) {
            this.color[0] = Math.round(color[0] * 255.0F);
            this.color[1] = Math.round(color[1] * 255.0F);
            this.color[2] = Math.round(color[2] * 255.0F);
        }
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

    public int getRotated() {
        return rotated;
    }

    public void setRotated(int rotated) {
        this.rotated = rotated;
    }

    public StringBuffer getTextContent() {
        return textContent;
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

    public TextObjectType getTextObjectType() {
        return textObjectType;
    }

    public void setTextObjectType(TextObjectType textObjectType) {
        this.textObjectType = textObjectType;
    }

    @Override
    public String toString() {
        return "TextObject{" +
                "textContent=" + textContent +
                '}';
    }

}
