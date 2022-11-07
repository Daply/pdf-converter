package pdftohtml.helpers;

import pdftohtml.domain.pdf.object.process.TextObjectType;

public class TextObjectTypeResolver {

    /**
     * Resolves pdf text object type:
     *  - list bullet
     *  - footnote
     *  - simple text
     *  - formula
     *
     * @param text - content of pdf text object
     * @return TextObjectType enum
     */
    public TextObjectType resolveType(String text) {
        if (text.trim().matches("[0-9]+\\)") ||
                text.trim().matches("[0-9]+\\.") ||
                text.trim().matches("\\uF0B7")) {
            return TextObjectType.LIST_BULLET;
        }
        return TextObjectType.TEXT;
    }

}
