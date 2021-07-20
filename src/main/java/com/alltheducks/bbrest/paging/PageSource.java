package com.alltheducks.bbrest.paging;

/**
 * Describes the procedure for fetching a page of results from the REST API.
 * @param <T> The type of object contained in the result. I.e. if this is a page of users T would be a user model.
 */
@FunctionalInterface
public interface PageSource<T> {

    /**
     * Request the results from the REST API.
     * @return The results associated with the page.
     */
    Iterable<T> nextPage();

}
