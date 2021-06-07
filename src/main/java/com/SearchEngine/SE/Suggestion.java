package com.SearchEngine.SE;

public class Suggestion {
    private final String SearchQueryText;
    private final String Frequency;

    public Suggestion(String searchQueryText, String Frequency) {
        this.SearchQueryText = searchQueryText;
        this.Frequency = Frequency;
    }

    public String getSearchQueryText() {
        return SearchQueryText;
    }

    public String getFrequency() {
        return Frequency;
    }
}