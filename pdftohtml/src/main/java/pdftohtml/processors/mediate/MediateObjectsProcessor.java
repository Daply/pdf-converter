package pdftohtml.processors.mediate;

import pdftohtml.common.Properties;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.mediate.list.ItemsListBullet;
import pdftohtml.domain.pdf.object.basic.PdfDocumentObject;
import pdftohtml.domain.pdf.object.basic.TextObject;
import pdftohtml.domain.pdf.object.basic.TextObjectType;
import pdftohtml.domain.pdf.object.basic.container.Block;
import pdftohtml.domain.pdf.object.basic.container.PageLine;
import pdftohtml.domain.pdf.object.basic.template.Divider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MediateObjectsProcessor {

    private List<Divider> horizontalDividers;

    public void processMediateObjects(
            List<Block> blocks,
            List<Divider> dividers,
            List<FrameworkRectangle> strokePaths
    ) {
        defineAllVisibleDividers(dividers, strokePaths);
        List<Divider> dividersSortedByY = dividers.stream().sorted(
                Comparator.comparing(d -> d.getRectangle().getMinY())
        ).collect(Collectors.toList());
        dividersSortedByY.forEach(divider -> {
            List<Divider> sameYMinDividers = new ArrayList<>();
            sameYMinDividers.add(divider);
            for (Divider sameYMinDivider: dividersSortedByY) {
                if (divider.getRectangle()
                        .hasSameYCoordinatesWithYInaccuracy(
                                sameYMinDivider.getRectangle(),
                                Properties.yInaccuracy
                        )) {
                    sameYMinDividers.add(sameYMinDivider);
                }
            }
            // TODO
            defineMediateObject(blocks, sameYMinDividers);
        });
    }

    private void defineAllVisibleDividers(
            List<Divider> dividers,
            List<FrameworkRectangle> strokePaths
    ) {
        this.horizontalDividers = strokePaths.stream().map(path -> {
            if (path.getWidth() > path.getHeight() * 2) {
                return new Divider(path, false, true, true);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        strokePaths.forEach(path ->
                dividers.forEach(divider -> {
                    double dividerWidth = divider.getRectangle().getWidth();
                    if (divider.getRectangle().intersectsHorizontally(path) > dividerWidth * 0.7) {
                        divider.setVisible(true);
                    }
        }));
    }

    private void defineMediateObject(
            List<Block> blocks,
            List<Divider> sameYMinDividers
    ) {
        // 1. define list (by first blocks containing list bullets)
        sameYMinDividers.forEach(divider -> {
            blocks.forEach(block -> {
                if (block.getContentRectangle()
                        .isBeforeHorizontallyWithXInaccuracy(
                                divider.getRectangle(),
                                Properties.xInaccuracy
                        )) {
                    // TODO
                    List<ItemsListBullet> bulletList = new ArrayList<>();
                    if (block.getLines().size() == 1) {
                        PageLine line = block.getLines().get(0);
                        if (line.getObjects().size() == 1) {
                            PdfDocumentObject object = line.getObjects().get(0);
                            if (object instanceof TextObject &&
                                    ((TextObject) object).getTextObjectType() == TextObjectType.LIST_BULLET) {
                                ItemsListBullet bullet = createListBulletFromTextObject((TextObject) object);
                                bulletList.add(bullet);
                            }
                        }
                    }
                }
                if (block.getContentRectangle()
                        .isAfterHorizontallyWithXInaccuracy(
                                divider.getRectangle(),
                                Properties.xInaccuracy
                        )) {

                }
            });
        });

        // 2. define table (with visible or invisible borders)
    }

    private ItemsListBullet createListBulletFromTextObject(TextObject object) {
        ItemsListBullet bullet = new ItemsListBullet();
        bullet.setBulletCharacter(object.getText());
        bullet.setFontFamily(object.getFontFamily());
        return bullet;
    }
}
