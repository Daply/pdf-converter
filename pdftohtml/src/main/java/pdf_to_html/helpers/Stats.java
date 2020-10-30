package pdf_to_html.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pdf_to_html.entities.framework.Rectangle2D;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Stats {

    public static float MINIMUM_DISTANCE_BETWEEN_LINES = 0f;

    public static float MAXIMUM_DISTANCE_BETWEEN_LINES = 0f;

    public static float THRESHOLD = 2f;

    public static boolean isDistanceBetweenLinesMoreThanNormal(Rectangle2D rectangle1, Rectangle2D rectangle2) {
        float distance = (float) (rectangle2.getMinY() - rectangle1.getMaxY());
        return Math.abs(distance - MAXIMUM_DISTANCE_BETWEEN_LINES) < THRESHOLD;
    }

}
