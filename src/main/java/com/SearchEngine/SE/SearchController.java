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
    public ArrayList<SearchResult> searchDB(@RequestParam("query") String query, @RequestParam(value = "page", required = true, defaultValue = "1") int page) throws FileNotFoundException {
        ArrayList<SearchResult> resList = new ArrayList<SearchResult>();

        ArrayList<Document> docList = QueryProcessor.getSearchResults(query,page,20);

        for (Document doc: docList){
            resList.add(new SearchResult(doc.get("pageTitle").toString(), doc.get("URL").toString(), doc.get("pageText").toString()));
        }

        return resList;
    }
}
