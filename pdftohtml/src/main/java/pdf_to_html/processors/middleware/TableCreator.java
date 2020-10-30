package pdf_to_html.processors.middleware;

import pdf_to_html.entities.pdfdocument.object.middleware.MiddlewareObject;
import pdf_to_html.entities.pdfdocument.object.middleware.table.Table;
import pdf_to_html.entities.pdfdocument.object.middleware.table.TableCell;
import pdf_to_html.entities.pdfdocument.object.middleware.table.TableRow;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObjectType;
import pdf_to_html.entities.pdfdocument.object.process.SkeletonType;
import pdf_to_html.entities.pdfdocument.object.process.complex.Skeleton;
import pdf_to_html.entities.pdfdocument.object.process.container.Block;
import pdf_to_html.entities.pdfdocument.object.process.container.PageLine;
import pdf_to_html.helpers.Stats;

import java.util.ArrayList;
import java.util.List;

public class TableCreator extends MiddlewareObjectCreator {

    @Override
    public MiddlewareObject create(PdfDocumentObject object) {
        validateObject(object);
        return processTable((Skeleton)object);
    }

    public Table processTable(Skeleton skeleton) {
        validate(skeleton);

        Table table = new Table();
        List<Integer> linesThatEndRow = resolveTableRows(skeleton);
        List<TableRow> tableRows = createListOfTableRows(linesThatEndRow.size());
        List<PageLine> cellLines = new ArrayList<>();
        for (Block block: skeleton.getSkeletonDataBlocks()) {
            TableCell tableCell = new TableCell();

            int lineNumber = 0;
            int currentRow = 0;
            while (lineNumber != linesThatEndRow.get(currentRow)) {
                cellLines.add(block.getLines().get(lineNumber));
                ++lineNumber;
            }

            tableCell.setObjects(linesMiddlewareObjectsProcessor.processLines(cellLines));
            tableRows.get(currentRow).addCell(tableCell);
            ++currentRow;
        }
        table.setRows(tableRows);

        return table;
    }

    public List<TableRow> createListOfTableRows(int size) {
        List<TableRow> tableRows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tableRows.add(new TableRow());
        }
        return tableRows;
    }

    public List<Integer> resolveTableRows(Skeleton skeleton) {
        List<Integer> linesThatEndRow = new ArrayList<>();
        int numberOfLinesInSkeleton = skeleton.getNumberOfLastLine() - skeleton.getNumberOfFirstLine();
        for (int lineNumber = 1; lineNumber < numberOfLinesInSkeleton; lineNumber++) {
            for (Block block: skeleton.getSkeletonDataBlocks()) {
                if (Stats.isDistanceBetweenLinesMoreThanNormal(block.getLines().get(lineNumber - 1).getRectangle(),
                        block.getLines().get(lineNumber).getRectangle()) && !linesThatEndRow.contains(lineNumber)) {
                    linesThatEndRow.add(lineNumber);
                }
            }
        }
        return linesThatEndRow;
    }

    private void validate(Skeleton skeleton) {
        if (skeleton == null) {
            throw new IllegalArgumentException("skeleton object in table processing is null");
        }

        if (!skeleton.getType().equals(SkeletonType.TABLE)) {
            throw new IllegalArgumentException("skeleton is not of type TABLE");
        }
    }

    @Override
    protected void validateObject(PdfDocumentObject object) {
        super.validateObject(object);
        if (!object.getObjectType().equals(PdfDocumentObjectType.SKELETON))
            throw new IllegalArgumentException("object in processing middleware object TABLE is NOT a SKELETON");
    }

}
