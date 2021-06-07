package com.SearchEngine.SE;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

@Controller
public class SearchController {

    @CrossOrigin
    @GetMapping("/api/search")
    @ResponseBody
    public SearchResult searchDB(@RequestParam("query") String query, @RequestParam(value = "page", required = true, defaultValue = "1") int page) throws FileNotFoundException {
        ArrayList<SearchResult.pageItem> resList = new ArrayList<SearchResult.pageItem>();

        Document doc = QueryProcessor.getSearchResults(query,page,20);
        ArrayList<Document> docList = (ArrayList<Document>) doc.get("list");
        String docCount = doc.get("count").toString();

        for (Document docItem: docList){
            resList.add(new SearchResult.pageItem(docItem.get("pageTitle").toString(), docItem.get("URL").toString(), docItem.get("pageText").toString()));
        }

        SearchResult res = new SearchResult(resList, docCount);

        return res;
    }

    @CrossOrigin
    @GetMapping("/api/suggest")
    @ResponseBody
    public ArrayList<Suggestion> getSuggestionsDB(@RequestParam("query") String query) {
        ArrayList<Suggestion> suggestionList = new ArrayList<Suggestion>();

        ArrayList<Document> docList = QueryProcessor.getSuggestions(query);

        for (Document doc: docList){
            suggestionList.add(new Suggestion(doc.get("query").toString(), doc.get("frequency").toString()));
        }

        return suggestionList;
    }
}
