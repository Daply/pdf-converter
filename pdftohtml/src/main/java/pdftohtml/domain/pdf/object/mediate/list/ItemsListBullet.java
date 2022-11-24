package pdftohtml.domain.pdf.object.mediate.list;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemsListBullet {

    private String bulletCharacter;

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

}
