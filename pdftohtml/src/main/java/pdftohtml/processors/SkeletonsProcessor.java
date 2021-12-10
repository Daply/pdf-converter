package pdftohtml.processors;

import pdftohtml.domain.pdfdocument.object.middleware.MiddlewareObject;
import pdftohtml.domain.pdfdocument.object.process.*;
import pdftohtml.domain.pdfdocument.object.process.complex.Skeleton;
import pdftohtml.processors.middleware.LinesMiddlewareObjectsProcessor;

import java.util.ArrayList;
import java.util.List;

public class SkeletonsProcessor {

    private List<MiddlewareObject> pageMiddlewareObjects;

    public SkeletonsProcessor() {
        this.pageMiddlewareObjects = new ArrayList<>();
    }

    public void processMiddlewareObjects(List<Skeleton> skeletons) {

        // sort skeletons from biggest to smallest
        skeletons.sort(Skeleton::compareTo);

        Skeleton pageSkeleton = skeletons.get(0);
        validate(pageSkeleton);

        processMiddlewareObjectsInPageSkeleton(pageSkeleton);

    }

    private void processMiddlewareObjectsInPageSkeleton(Skeleton skeleton) {
        validate(skeleton);

        LinesMiddlewareObjectsProcessor linesMiddlewareObjectsProcessor = new
                LinesMiddlewareObjectsProcessor();
        this.pageMiddlewareObjects.addAll(linesMiddlewareObjectsProcessor
                                               .processLines(skeleton.getSkeletonDataBlocks().get(0).getLines()));
    }

    public List<MiddlewareObject> getPageMiddlewareObjects() {
        return this.pageMiddlewareObjects;
    }

    private void validate(Skeleton pageSkeleton) {
        if (!pageSkeleton.getType().equals(SkeletonType.PAGE)) {
            throw new IllegalArgumentException("Skeleton with NOT PAGE type");
        }
        if (pageSkeleton.getSkeletonDataBlocks().size() != 1) {
            throw new IllegalArgumentException("Skeleton with type PAGE has number of blocks not equal to 1");
        }
    }

}
