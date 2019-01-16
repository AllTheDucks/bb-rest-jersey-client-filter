package com.alltheducks.bbrest.paging;

import java.util.Collections;
import java.util.Iterator;

/**
 * An iterator capable of iterating over multiple
 * @param <T>
 */
class PagingIterator<T> implements Iterator<T> {

    private final PageSource<T> pageSource;

    private Iterator<T> currentPageIterator = Collections.emptyIterator();
    private long pageNumber = 0;

    public PagingIterator(final PageSource<T> pageSource) {
        this.pageSource = pageSource;
    }

    private Iterator<T> getPage() {
        if(!currentPageIterator.hasNext()) {
            final Iterable<T> page = pageSource.fetch(pageNumber++);
            currentPageIterator = page != null ? page.iterator() : Collections.emptyIterator();
        }
        return currentPageIterator;
    }

    @Override
    public boolean hasNext() {
        return getPage().hasNext();
    }

    @Override
    public T next() {
        return getPage().next();
    }

}
