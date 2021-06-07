package com.SearchEngine.SE;

import java.util.ArrayList;

public class SearchResult {
    public static class pageItem {
        private final String title;
        private final String URL;
        private final String snippet;

        pageItem(String title, String url, String snippet) {
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

    public ArrayList<pageItem> Results;
    public String Count;

    public SearchResult(ArrayList<pageItem> Results, String Count) {
        this.Count = Count;
        this.Results = Results;
    }



}
