package com.our;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

    private static final ArrayList<String> seedLinkList = new ArrayList<String>(Arrays.asList("https://www.wikipedia.org/", "https://www.nytimes.com/","https://www.theguardian.com/","https://www.bbc.com/","https://www.espn.com/","https://www.amazon.com/","https://egypt.souq.com/","https://www.gutenberg.org/","https://www.tutorialspoint.com/","https://stackoverflow.com/"));

    public DBController() {
        Logger mongoLogger = Logger.getLogger( "org.mongodb" );
        mongoLogger.setLevel(Level.SEVERE);

        MongoClient client = MongoClients.create("mongodb+srv://Admin:vl2Ae3opUkQUf0dX@cluster0.lolxd.mongodb.net");
        MongoDatabase database = client.getDatabase("SearchEngineDB");

        newLinks = database.getCollection("newLinks");
        visitedLinks = database.getCollection("visitedLinks");
        pages = database.getCollection("pages");
    }

    public void loadInitSeed() {
        newLinks.drop();
        for (int i = 0; i <10; i++) {
            Document seedEntry = new Document("seedURL", seedLinkList.get(i));
            newLinks.insertOne(seedEntry);
        }
    }
}
