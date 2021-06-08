package com.SearchEngine.SE;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


public class DBController {

    private final MongoCollection<Document> newLinks;
    private final MongoCollection<Document> visitedLinks;
    private final MongoCollection<Document> pages;
    private final MongoCollection<Document> indexer;
    private final MongoCollection<Document> history;

    private static final ArrayList<String> seedLinkList = new ArrayList<String>(Arrays.asList("https://www.nytimes.com/", "https://www.theguardian.com/", "https://www.bbc.com/", "https://www.espn.com/", "https://www.amazon.com/", "https://egypt.souq.com/", "https://www.gutenberg.org/", "https://www.tutorialspoint.com/", "https://stackoverflow.com/", "https://en.wikipedia.org/"));

    public DBController() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);

        MongoClient client = MongoClients.create("mongodb+srv://Admin:vl2Ae3opUkQUf0dX@cluster0.lolxd.mongodb.net");
        MongoDatabase database = client.getDatabase("SearchEngineDB");

        newLinks = database.getCollection("newLinks");
        visitedLinks = database.getCollection("visitedLinks");
        pages = database.getCollection("pages");
        indexer = database.getCollection("indexer");
        history = database.getCollection("history");
    }

    // Get number of documents in visitedLinks collection
    public int getVisitedLinksCount() {
        return (int) visitedLinks.countDocuments();
    }

    // Get number of documents in newLinks collection
    public int getNewLinksCount() {
        return (int) newLinks.countDocuments();
    }

    // Load initial seed
    public void loadInitSeed() {
        newLinks.drop();
        for (int i = 0; i <10; i++) {
            Document seedEntry = new Document("URL", seedLinkList.get(i));
            seedEntry.append("processing", false);
            newLinks.insertOne(seedEntry);
        }
    }

    // Get new non processing link
    public String getNewUnprocessedLink() {
        Document newLink = newLinks.find(Filters.eq("processing", false)).first();
        newLinks.findOneAndUpdate(Filters.eq("_id",newLink.get("_id")), Updates.set("processing", true));
        return newLink.get("URL").toString();
    }

    // Reset All Processing Links
    public void resetProcessingLink() {
        newLinks.updateMany(Filters.eq("processing", true), Updates.set("processing", false));
    }

    // Remove a link from newLinks
    public void removeFromNewLinks(String newLink) {
        newLinks.deleteOne(Filters.eq("URL", newLink));
    }

    // Checks if a specific link exits in visitedLinks
    public boolean visitedBefore(String newLink) {
        return (visitedLinks.countDocuments(Filters.eq("URL", newLink)) != 0);
    }

    // Checks if a specific link exits in visitedLinks or in newLinks
    public boolean isNewLink(String newLink) {
        int countInVisitedLinks = (int) visitedLinks.countDocuments(Filters.eq("URL", newLink));
        int countInNewLinks = (int) newLinks.countDocuments(Filters.eq("URL", newLink));
        return ((countInVisitedLinks + countInNewLinks) == 0);
    }

    // Add a link to newLinks
    public void addToNewLinks(String link) {
        Document newLinkEntry = new Document("URL", link);
        newLinkEntry.append("processing", false);
        newLinks.insertOne(newLinkEntry);
    }

    // Add a link to visitedLinks
    public void addToVisitedLinks(String link) {
        Document visitedLinkEntry = new Document("URL", link);
        visitedLinks.insertOne(visitedLinkEntry);
    }

    // Add a page to pages
    public void addToPages(String URL, String pageTitle, String pageText) {
        Document pageEntry = new Document("URL", URL);
        pageEntry.append("pageTitle", pageTitle);
        pageEntry.append("pageText", pageText);
        pageEntry.append("indexState", "none");
        pages.insertOne(pageEntry);
    }

    // Get new non indexed page
    public Document getNewUnindexedPage() {
        Document newPage = pages.find(Filters.eq("indexState", "none")).first();
        pages.findOneAndUpdate(Filters.eq("_id",newPage.get("_id")), Updates.set("indexState", "indexing"));
        return newPage;
    }

    // Get new non indexed page
    public void markDonePage(Document Page) {
        pages.findOneAndUpdate(Filters.eq("_id",Page.get("_id")), Updates.set("indexState", "done"));
    }

    // Check if a page exists in pages
    public boolean pageSaved(String link) {
        return (pages.countDocuments(Filters.eq("URL", link)) != 0);
    }

    // Get pages function
    public FindIterable<Document> getPages() {
        return pages.find();
    }

    // Check if word exists in indexer
    public boolean wordExists(String word) {
        return !(indexer.countDocuments(Filters.eq("word", word)) == 0);
    }

    // Add new word to indexer
    public void addWordToIndexer(String word, String URL, int TF, float IDF) {
        Document indexerEntry = new Document("word", word);
        ArrayList<Document> URLList =  new ArrayList<>();
        Document URLEntry = new Document("URL", URL);
        URLEntry.append("TF", TF);
        URLList.add(URLEntry);
        indexerEntry.append("URLList", URLList);
        indexerEntry.append("IDF", IDF);
        indexer.insertOne(indexerEntry);
    }

    // Get URLList of a word
    public ArrayList<Document> getURLList(String word) {
        return (ArrayList<Document>) indexer.find(Filters.eq("word", word)).first().get("URLList");
    }

    // Update URLList of a word in indexer
    public void updateURLList(String word, ArrayList<Document> URLList) {
        // Create query
        BasicDBObject query = new BasicDBObject();
        query.put("word", word);

        // Create new document
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("URLList", URLList);

        // Create update object
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        // Update document
        indexer.updateOne(query, updateObject);
    }

    // Update URLList and IDF of a word in indexer
    public void updateURLListAndIDF(String word, ArrayList<Document> URLList, float IDF) {
        // Create query
        BasicDBObject query = new BasicDBObject();
        query.put("word", word);

        // Create new document
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("URLList", URLList);
        newDocument.put("IDF", IDF);

        // Create update object
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        // Update document
        indexer.updateOne(query, updateObject);
    }

    // Drop indexer collection
    public void dropIndexerCollection() {
        indexer.drop();
    }
	
	// Search for a word in indexer
    public ArrayList<Document> searchWord(String word) {
        return indexer.find(Filters.eq("word", word)).into(new ArrayList<Document>());
    }

    // Get page with url
    public Document getPage(String URL) {
        return (Document) pages.find(Filters.eq("URL", URL)).first();
    }

    // Add a query to history
    public void addToHistory(String query) {
        history.updateOne(Filters.eq("query", query), Updates.inc("frequency", 1), new UpdateOptions().upsert(true));
    }

    // Get suggestions
    public FindIterable<Document> getSuggestions(String query) {
        return history.find(Filters.regex("query", "^"+query, "i"));
    }
}
