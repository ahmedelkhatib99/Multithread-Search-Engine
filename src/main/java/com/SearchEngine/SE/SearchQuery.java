package com.SearchEngine.SE;

public class SearchQuery {
    private final String SearchQueryText;

    public SearchQuery(String searchQueryText) {
        SearchQueryText = searchQueryText;
    }

    public String getSearchQueryText() {
        return SearchQueryText;
    }
}