package com.our;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.net.UnknownHostException;

public class DBController {

    private final MongoCollection<Document> newLinks;
    private final MongoCollection<Document> visitedLinks;
    private final MongoCollection<Document> pages;

    private static final ArrayList<String> seedLinkList = new ArrayList<String>(Arrays.asList("https://en.wikipedia.org/", "https://www.nytimes.com/","https://www.theguardian.com/","https://www.bbc.com/","https://www.espn.com/","https://www.amazon.com/","https://egypt.souq.com/","https://www.gutenberg.org/","https://www.tutorialspoint.com/","https://stackoverflow.com/"));

    public DBController() {
        Logger mongoLogger = Logger.getLogger( "org.mongodb" );
        mongoLogger.setLevel(Level.SEVERE);

        MongoClient client = MongoClients.create("mongodb+srv://Admin:vl2Ae3opUkQUf0dX@cluster0.lolxd.mongodb.net");
        MongoDatabase database = client.getDatabase("SearchEngineDB");

        newLinks = database.getCollection("newLinks");
        visitedLinks = database.getCollection("visitedLinks");
        pages = database.getCollection("pages");
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
            newLinks.insertOne(seedEntry);
        }
    }

    // Get new link
    public String getNewLink() {
        return newLinks.find().first().get("URL").toString();
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
        pages.insertOne(pageEntry);
    }

    // Check if a page exists in pages
    public boolean pageSaved(String link) {
        return (pages.countDocuments(Filters.eq("URL", link)) != 0);
    }
}
