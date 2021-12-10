package pdftohtml.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pdftohtml.domain.framework.Rectangle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Stats {

    public static float minimumDistanceBetweenLines = 0f;

    public static float maximumDistanceBetweenLines = 0f;

    public static float threshold = 2f;

    public static boolean isDistanceBetweenLinesMoreThanNormal(Rectangle rectangle1, Rectangle rectangle2) {
        float distance = (float) (rectangle2.getMinY() - rectangle1.getMaxY());
        return Math.abs(distance - maximumDistanceBetweenLines) < threshold;
    }

}
