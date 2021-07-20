package com.alltheducks.bbrest.paging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Model representing a paged result from the server.
 *
 * This class is modelled off the response from the Blackboard REST API, but could be used with any REST API where
 * the pages match this fields on this model. It may be necessary to configure the JSR310 Object Mapper (e.g. Jackson)
 * to ignore unknown properties either in the response or on this model.
 *
 * @param <T> The type of object contained in the result. I.e. if this is a page of users T would be a user model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResult<T> {

    private List<T> results;
    private PagingInfo paging;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public PagingInfo getPaging() {
        return paging;
    }

    public void setPaging(PagingInfo paging) {
        this.paging = paging;
    }

    public class PagingInfo {

        private String nextPage;

        public String getNextPage() {
            return nextPage;
        }

        public void setNextPage(String nextPage) {
            this.nextPage = nextPage;
        }

    }

}

