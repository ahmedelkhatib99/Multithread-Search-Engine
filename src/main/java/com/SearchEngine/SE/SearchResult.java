package com.SearchEngine.SE;

public class SearchResult {
    private final String title;
    private final String URL;
    private final String snippet;


    public SearchResult(String title, String url, String snippet) {
        this.title = title;
        URL = url;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getURL() {
        return URL;
    }

    public String getSnippet() {
        return snippet;
    }

}
