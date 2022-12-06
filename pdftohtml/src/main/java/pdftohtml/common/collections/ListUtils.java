package pdftohtml.common.collections;

import pdftohtml.domain.pdf.object.composite.table.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

public class ListUtils<T, S> {

    public List<Pair<T, S>> zip(
            List<T> a,
            List<S> b,
            BiPredicate<T, S> predicate
    ) {
        List<Pair<T, S>> pairs = new ArrayList<>();
        Iterator<T> it1 = a.iterator();
        Iterator<S> it2 = b.iterator();

        while (it1.hasNext()) {
            if (it2.hasNext()) {
                T el1 = it1.next();
                S el2 = it2.next();

                if (predicate.test(el1, el2)) {
                    Pair<T, S> pair = new Pair<T, S>(el1, el2);
                    pairs.add(pair);
                }
            }
        }
        return pairs;
    }
}
