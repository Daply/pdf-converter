package pdftohtml.processors.middleware;

import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdfdocument.object.middleware.list.ItemsList;
import pdftohtml.domain.pdfdocument.object.middleware.list.ItemsListRowContent;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObject;
import pdftohtml.domain.pdfdocument.object.process.PdfDocumentObjectType;
import pdftohtml.domain.pdfdocument.object.process.SkeletonType;
import pdftohtml.domain.pdfdocument.object.process.complex.Skeleton;
import pdftohtml.domain.pdfdocument.object.process.container.Block;
import pdftohtml.domain.pdfdocument.object.process.container.PageLine;

import java.util.ArrayList;
import java.util.List;

public class ItemsListCreator extends MiddlewareObjectCreator {

    @Override
    public MiddlewareObject create(PdfDocumentObject object) {
        validateObject(object);
        return processList((Skeleton) object);
    }

    public ItemsList processList(Skeleton skeleton) {
        validate(skeleton);

        ItemsList itemsList = new ItemsList();
        List<Integer> bulletsLinesNumbers = processBulletsBlock(skeleton.getSkeletonDataBlocks().get(0));
        validateBulletsLinesNumbers(bulletsLinesNumbers);


        Block contentBlock = skeleton.getSkeletonDataBlocks().get(1);
        List<PageLine> bulletLines = new ArrayList<>();
        ItemsListRowContent listRow = new ItemsListRowContent();
        int bulletLineNumberIndex = 0;
        for (PageLine line: contentBlock.getLines()) {
            if (bulletLineNumberIndex < bulletsLinesNumbers.size() &&
                    line.getLineNumber() == bulletsLinesNumbers.get(bulletLineNumberIndex)) {

                // process one previous list bullet content
                if (!bulletLines.isEmpty()) {
                    listRow.setObjects(linesMiddlewareObjectsProcessor.processLines(bulletLines));
                    itemsList.addItemListRowContent(listRow);
                }

                // creating new row
                listRow = new ItemsListRowContent();
                bulletLines = new ArrayList<>();

                ++bulletLineNumberIndex;
            }
            bulletLines.add(line);
        }
        listRow.setObjects(linesMiddlewareObjectsProcessor.processLines(bulletLines));
        itemsList.addItemListRowContent(listRow);
        return itemsList;
    }

    private List<Integer> processBulletsBlock(Block block) {
        List<Integer> bulletsLinesNumbers = new ArrayList<>();
        for (PageLine line: block.getLines()) {
            if (!line.getObjects().isEmpty() && isTextTypeObject(line.getObjects().get(0)))
                bulletsLinesNumbers.add(line.getLineNumber());
            else if (!line.getObjects().isEmpty())
                throw new IllegalArgumentException("there was found not bullet element in bullets block");
        }
        return bulletsLinesNumbers;
    }

    private boolean isTextTypeObject(PdfDocumentObject object) {
        return object.getObjectType().equals(PdfDocumentObjectType.SIMPLE_TEXT) ||
                object.getObjectType().equals(PdfDocumentObjectType.LINK);
    }

    private void validateBulletsLinesNumbers(List<Integer> bulletsLinesNumbers) {
        if (bulletsLinesNumbers.isEmpty())
            throw new IllegalArgumentException("skeleton object of type LIST has no any bullets!");
    }

    private void validate(Skeleton skeleton) {
        if (skeleton == null) {
            throw new IllegalArgumentException("skeleton object in list processing is null");
        }

        if (!skeleton.getType().equals(SkeletonType.LIST)) {
            throw new IllegalArgumentException("skeleton is not of type LIST");
        }

        if (skeleton.getSkeletonDataBlocks().size() != 2) {
            throw new IllegalArgumentException("skeleton of type LIST has more than two number of blocks");
        }
    }

    @Override
    protected void validateObject(PdfDocumentObject object) {
        super.validateObject(object);
        if (!object.getObjectType().equals(PdfDocumentObjectType.SKELETON))
            throw new IllegalArgumentException("object in processing middleware object LIST is NOT a SKELETON");
    }

}
