package com.SearchEngine.SE;
import com.mongodb.client.FindIterable;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class QueryProcessor {

    public static ArrayList<Document> getSearchResults(String queryString, int page, int limit) throws FileNotFoundException {
        // Initialize DBController
        DBController DB = new DBController();

        // Read stopwords
        ArrayList<String> stopwords = new ArrayList<>();
        Stopwords.loadStopwords(stopwords);

        // Process queryString
        ArrayList<String> queryWords = processQueryText(queryString, stopwords);

        // Get queryURLList
        ArrayList<String> queryURLList = getQueryURLList(queryWords, DB);

        ArrayList<Document> resultsList = paginateAndPopulate(queryURLList, page, limit, DB);

        // Replace text with snippets
        resultsList = textToSnippets(resultsList, queryString, stopwords, 10);

        for (String URL : queryURLList) {
            System.out.println(URL);
        }

        if (resultsList != null) {
            for (Document doc : resultsList) {
                System.out.println(doc.get("pageText"));
            }
        }

        // Test add to history
        DB.addToHistory(queryString);



        return resultsList;
    }

    public static ArrayList<Document> getSuggestions(String searchQuery)
    {
        DBController DB = new DBController();
        FindIterable<Document> suggestions = DB.getSuggestions(searchQuery);
        ArrayList<Document> SuggestionList = new ArrayList<>();
        for (Document suggestion : suggestions) {
            Document SuggestionItem = new Document("query", suggestion.get("query").toString());
            SuggestionItem.append("frequency", suggestion.get("frequency").toString());
            SuggestionList.add(SuggestionItem);
        }
        return SuggestionList;
    }

    private static ArrayList<String> processQueryText(String queryString, ArrayList<String> stopwords) {
        // Split on space character
        String[] querySplitWords = queryString.toLowerCase().split(" ");

        // Create PortStemmer
        PorterStemmer porterStemmer = new PorterStemmer();

        // Initialize queryWords array
        ArrayList<String> queryWords = new ArrayList<>();

        // Loop on querySplitWords
        for (String word : querySplitWords) {
            // Remove punctuation, trim and convert to lowercase
            word = word.replaceAll("[^a-zA-Z0-9-]", "").trim().toLowerCase();
            // If word is not an empty string and is not a stopword
            if ((!word.equals("") && !stopwords.contains(word))) {
                // Stem word
                word = porterStemmer.stem(word);
                // Add word to queryWords
                queryWords.add(word);
            }
        }

        // Return queryWords
        return queryWords;
    }

    // Function that takes a list words and returns a list of URLs relevant to these words
    private static ArrayList<String> getQueryURLList(ArrayList<String> queryWords, DBController DB) {
        // Initialize queryURLList
        ArrayList<String> queryURLList = new ArrayList<String>();

        // Loop on all words in query
        for (String queryWord : queryWords) {
            // Get objects that match the queryWord
            ArrayList<Document> wordObjects = DB.searchWord(queryWord);
            // For each object
            for (Document wordObject : wordObjects) {
                // Get the URLList
                ArrayList<Document> URLList = (ArrayList<Document>) wordObject.get("URLList");
                // For each URLObject in URLList
                for (Document URLObject : URLList) {
                    // Get URL
                    String URL = (String) URLObject.get("URL");
                    // If queryURLList does not contain URL
                    if (!queryURLList.contains(URL)) {
                        // Add URL to queryURLList
                        queryURLList.add((String) URLObject.get("URL"));
                    }
                }
            }
        }

        // Return queryURLList
        return queryURLList;
    }

    // Function that takes an array of URLs, page, and limit and returns a paginated and populated result list
    private static ArrayList<Document> paginateAndPopulate(ArrayList<String> queryURLList, int page, int limit, DBController DB) {
        // Calculate offset, end and size
        int offset = (page - 1) * limit;
        int end = offset + limit;
        int URLListSize = queryURLList.size();

        // Pagination fault
        if (!(offset >= 0 && offset < URLListSize) || !(end > 0)) {
            return null;
        }

        // Corner case: end > URLListSize
        if (end > URLListSize) {
            end = URLListSize;
        }

        // Results List
        ArrayList<Document> resultsList = new ArrayList<>();
        // Loop on requested page
        for (int i = offset; i < end; i++) {
            String URL = queryURLList.get(i);
            Document searchDocument = DB.getPage(URL);
            resultsList.add(searchDocument);
        }

        // Return results list
        return resultsList;
    }

    // Function that takes a list of results and queryString and returns snippets instead of page text
    private static ArrayList<Document> textToSnippets(ArrayList<Document> resultsList, String queryString, ArrayList<String> stopwords, int maxLength) {
        // Split on space character
        String[] querySplitWords = queryString.toLowerCase().split(" ");

        // Initialize queryWords array
        ArrayList<String> queryWords = new ArrayList<>();

        // Loop on querySplitWords
        for (String word : querySplitWords) {
            // Remove punctuation, trim and convert to lowercase
            word = word.replaceAll("[^a-zA-Z0-9-]", "").trim().toLowerCase();
            // If word is not an empty string and is not a stopword
            if ((!word.equals("") && !stopwords.contains(word))) {
                // Add word to queryWords
                queryWords.add(word);
            }
        }

        // Loop on resultsList
        int outerLoopCounter = 0;
        for (Document result : resultsList) {
            // Get page text and set pageUpdated boolean
            String pageText = (String) result.get("pageText");
            String pageTextLower = pageText.toLowerCase();
            boolean pageUpdated = false;
            // Loop on query Words
            for (String word : queryWords) {
                // Search for word in pageText
                int wordIndex = pageTextLower.indexOf(word);
                // If word does not exist
                if (wordIndex == -1) {
                    continue;
                }
                // Calculate start and end indices
                int spaceCounter = 0;
                int start = wordIndex;
                while (start > 0 && spaceCounter < 7) {
                    start--;
                    if (pageText.charAt(start) == ' ') {
                        spaceCounter++;
                    }
                }
                spaceCounter = 0;
                int end = wordIndex;
                while (end < (pageText.length() - 1) && spaceCounter < 10) {
                    end++;
                    if (pageText.charAt(end) == ' ') {
                        spaceCounter++;
                    }
                }
                // Replace pageText with snippet
                result.replace("pageText", pageText.substring(start, end).trim());
                resultsList.set(outerLoopCounter, result);
                pageUpdated = true;
                break;
            }
            if (!pageUpdated) {
                // Replace pageText with snippet
                result.replace("pageText", pageText.substring(0, maxLength).trim());
                resultsList.set(outerLoopCounter, result);
            }
            outerLoopCounter++;
        }
        return resultsList;
    }
}

