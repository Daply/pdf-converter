package pdftohtml.processors.pdf.objects.composite;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdftohtml.common.Properties;
import pdftohtml.domain.common.FrameworkRectangle;
import pdftohtml.domain.pdf.object.composite.table.Table;
import pdftohtml.domain.pdf.object.container.TableSkeleton;
import pdftohtml.domain.pdf.object.composite.list.ItemsListBullet;
import pdftohtml.domain.pdf.object.PdfDocumentObject;
import pdftohtml.domain.pdf.object.text.TextObject;
import pdftohtml.domain.pdf.object.text.TextObjectType;
import pdftohtml.domain.pdf.object.container.Block;
import pdftohtml.domain.pdf.object.container.PageLine;
import pdftohtml.domain.pdf.object.template.Divider;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pdftohtml.helpers.testing.PdfPageDrawer.drawRectangle;

public class CompositeObjectsProcessor {

    /**
     * Main document for processing
     */
    private PDDocument document;
    private PDPage page;

    private List<Divider> horizontalDividers;

    /**
     * Test mode flags
     */
    private boolean skeletonTestMode = false;
    private boolean tableTestMode = true;

    public CompositeObjectsProcessor(PDDocument document) {
        this.document = document;
    }

    public void processCompositeObjects(
            int pageIndex,
            List<Block> blocks,
            List<Divider> dividers,
            List<FrameworkRectangle> strokePaths
    ) {
        this.page = this.document.getPage(pageIndex - 1);
        defineAllVisibleDividers(dividers, strokePaths);

        // 1. Create skeleton object with blocks and dividers
        List<TableSkeleton> skeletons = createTableSkeletons(blocks, dividers);
        drawSkeletonsForTest(pageIndex, skeletons);

        // 2. define tables from created skeleton objects
        List<Table> tables = skeletons.stream()
                .map(TableSkeleton::convertToTable)
                .collect(Collectors.toList());
        drawTableForTest(pageIndex, tables);
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

    private List<TableSkeleton> createTableSkeletons(
            List<Block> blocks,
            List<Divider> dividers
    ) {
        List<TableSkeleton> skeletons = new ArrayList<>();
        List<Divider> dividersSortedByY = dividers.stream().sorted(
                Comparator.comparing(d -> d.getRectangle().getMinY())
        ).collect(Collectors.toList());
        dividersSortedByY.forEach(divider -> {
            List<Divider> sameYMinDividers = new ArrayList<>();
            double minimumY = divider.getRectangle().getMinY();
            double maximumY = divider.getRectangle().getMaxY();
            sameYMinDividers.add(divider);
            for (Divider sameYMinDivider: dividersSortedByY) {
                if (divider.getRectangle()
                        .hasSameYCoordinatesWithYInaccuracy(
                                sameYMinDivider.getRectangle(),
                                Properties.yInaccuracy
                        ) && !divider.equals(sameYMinDivider)) {
                    sameYMinDividers.add(sameYMinDivider);
                    minimumY = Math.min(minimumY, sameYMinDivider.getRectangle().getMinY());
                    maximumY = Math.max(maximumY, sameYMinDivider.getRectangle().getMaxY());
                }
            }
            double finalMinimumY = minimumY;
            double finalMaximumY = maximumY;
            List<Block> tableBlocks = blocks.stream().filter(b ->
                    b.getRectangle().getMinY() >= finalMinimumY &&
                            b.getRectangle().getMaxY() <= finalMaximumY
            ).collect(Collectors.toList());
            TableSkeleton tableSkeleton = new TableSkeleton();
            tableSkeleton.setDividers(sameYMinDividers);
            tableSkeleton.setBlocks(tableBlocks);
            tableSkeleton.resolveRectangle();
            skeletons.add(tableSkeleton);
        });
        return skeletons;
    }

    private void defineMediateObject(
            List<Block> blocks,
            List<Divider> sameYMinDividers
    ) {
        // 1. define table
        sameYMinDividers.forEach(divider -> {
            blocks.forEach(block -> {
                if (block.getRectangle()
                        .isBeforeHorizontallyWithXInaccuracy(
                                divider.getRectangle(),
                                Properties.xInaccuracy
                        )) {
                    // TODO
                    if (block.getLines().size() == 1) {
                        PageLine line = block.getLines().get(0);
                        if (line.getObjects().size() == 1) {
                            PdfDocumentObject object = line.getObjects().get(0);
                            if (object instanceof TextObject &&
                                    ((TextObject) object).getTextObjectType() == TextObjectType.LIST_BULLET) {
                                ItemsListBullet bullet = createListBulletFromTextObject((TextObject) object);

                            }
                        }
                    }
                }
                if (block.getRectangle()
                        .isAfterHorizontallyWithXInaccuracy(
                                divider.getRectangle(),
                                Properties.xInaccuracy
                        )) {

                }
            });
        });

        // 2. define lists from tables
    }

    private ItemsListBullet createListBulletFromTextObject(TextObject object) {
        ItemsListBullet bullet = new ItemsListBullet();
        bullet.setBulletCharacter(object.getText());
        bullet.setFontFamily(object.getFontFamily());
        return bullet;
    }

    private void drawSkeletonsForTest(
            int pageIndex,
            List<TableSkeleton> skeletons
    ) {
        if (skeletonTestMode) {
            skeletons.forEach(skeleton -> {
                drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        skeleton.getRectangle(),
                        Color.GREEN,
                        null
                );
            });
        }
    }

    private void drawTableForTest(
            int pageIndex,
            List<Table> tables
    ) {
        if (tableTestMode) {
            tables.forEach(table -> {
                drawRectangle(
                        this.document,
                        this.document.getPages().get(pageIndex - 1),
                        table.getRectangle().getEnlargedRectangle(5f),
                        Color.GREEN,
                        null
                );
                table.getRows().forEach(row -> {
                    drawRectangle(
                            this.document,
                            this.document.getPages().get(pageIndex - 1),
                            row.getRectangle().getEnlargedRectangle(2.5f),
                            Color.BLUE,
                            null
                    );
                    row.getCells().forEach(cell -> {
                        drawRectangle(
                                this.document,
                                this.document.getPages().get(pageIndex - 1),
                                cell.getRectangle(),
                                Color.MAGENTA,
                                null
                        );
                    });
                });
            });
        }
    }
}
