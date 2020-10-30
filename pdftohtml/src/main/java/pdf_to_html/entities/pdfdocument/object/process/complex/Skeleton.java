package pdf_to_html.entities.pdfdocument.object.process.complex;

import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObject;
import pdf_to_html.entities.pdfdocument.object.process.PdfDocumentObjectType;
import pdf_to_html.entities.pdfdocument.object.process.SkeletonType;
import pdf_to_html.entities.pdfdocument.object.process.container.Block;
import pdf_to_html.entities.pdfdocument.object.process.template.Divider;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends PdfDocumentObject implements Comparable<Skeleton> {

    private List<Block> skeletonDataBlocks;

    private List<Divider> dividers;

    private int numberOfFirstLine;

    private int numberOfLastLine;

    private int level;

    private SkeletonType type;

    public Skeleton() {
        this.dividers = new ArrayList<>();
        this.skeletonDataBlocks = new ArrayList<>();
        this.objectType = PdfDocumentObjectType.SKELETON;
        this.type = SkeletonType.TABLE;
    }

    public List<Block> getSkeletonDataBlocks() {
        return skeletonDataBlocks;
    }

    public void addSkeletonDataBlock(Block skeletonDataBlock) {
        this.skeletonDataBlocks.add(skeletonDataBlock);
    }

    public void setSkeletonDataBlocks(List<Block> skeletonDataBlocks) {
        this.skeletonDataBlocks = skeletonDataBlocks;
    }

    public int getNumberOfFirstLine() {
        return numberOfFirstLine;
    }

    public void setNumberOfFirstLine(int numberOfFirstLine) {
        this.numberOfFirstLine = numberOfFirstLine;
    }

    public int getNumberOfLastLine() {
        return numberOfLastLine;
    }

    public void setNumberOfLastLine(int numberOfLastLine) {
        this.numberOfLastLine = numberOfLastLine;
    }

    public List<Divider> getDividers() {
        return dividers;
    }

    public void addDivider(Divider divider) {
        this.dividers.add(divider);
    }

    public void setDividers(List<Divider> dividers) {
        this.dividers = dividers;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public SkeletonType getType() {
        return type;
    }

    public void setType(SkeletonType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Skeleton o) {
        return Integer.compare(this.level, o.level);
    }

    public int compareToDesc(Skeleton o) {
        return Integer.compare(o.level, this.level);
    }

}
