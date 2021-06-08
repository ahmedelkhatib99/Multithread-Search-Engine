package com.SearchEngine.SE;

import com.mongodb.client.FindIterable;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Indexer {
    public static void main(String[] args) throws FileNotFoundException {
        // Initialize DBController
        DBController DB = new DBController();

        // Read stopwords
        ArrayList<String> stopwords = new ArrayList<>();
        Stopwords.loadStopwords(stopwords);

        // Create pageCounter
        Counter pageCounter = new Counter(DB);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of threads to run:");
        String numThreads = scanner.nextLine();
        scanner.close();

        for (int i = 0; i < Integer.parseInt(numThreads); i++) {
            IndexerThread indexThread = new IndexerThread(pageCounter, DB, stopwords);
            Thread thread = new Thread(indexThread, "T-" + i);
            thread.start();
        }
    }
}

class IndexerThread implements Runnable {
    private final Counter pageCounter;
    private final DBController DB;
    private final ArrayList<String> stopwords;
    private final PorterStemmer porterStemmer;

    public IndexerThread (Counter pageCounter, DBController DB, ArrayList<String> stopwords) {
        this.pageCounter = pageCounter;
        this.DB = DB;
        this.stopwords = stopwords;
        this.porterStemmer = new PorterStemmer();
    }

    @Override
    public void run() {
        indexPage();
    }


    public void indexPage() {
        System.out.println("New Page Started " + pageCounter.getCount());
        Document page;
        // Start CS: Fetch a link and set it to processing
        synchronized (pageCounter) {
            // Get new link from newLinks
            page = DB.getNewUnindexedPage();
            System.out.println("New Page Started " + page.get("pageTitle"));
        }
        // End CS

        // Get text and URL
        String URL = (String) page.get("URL");
        String text = (String) page.get("pageText");

        // Split text on space character
        String[] arrOfWords = text.split(" ");
        // Loop on words
        for (String word : arrOfWords) {
            // If stopword
            if (stopwords.contains(word.toLowerCase())) {
                continue;
            }
            // Remove punctuation and convert to lower case
            word = word.replaceAll("[^a-zA-Z0-9-]", "").toLowerCase();
            // Stem word
            word = porterStemmer.stem(word);
            // Check if word exists in indexer
            if (!DB.wordExists(word)) {
                DB.addWordToIndexer(word, URL, 1, 5000);
            } else {
                // Get URLList of word
                ArrayList<Document> URLList = DB.getURLList(word);
                // Check if URL exists in word URLList
                Document wordURLDocument = findDocumentByUrl(URLList, URL);
                // If URL does not exist in word URLList
                if (wordURLDocument == null) {
                    // Create new URL document
                    Document URLDocument = new Document("URL", URL);
                    URLDocument.append("TF", 1);
                    // Add URL document to word URLList
                    URLList.add(URLDocument);
                    // Update URLList in DB
                    DB.updateURLListAndIDF(word, URLList, 5000 / URLList.size());
                } else {
                    // Update URLDocument
                    wordURLDocument.replace("TF", (int) wordURLDocument.get("TF") + 1);
                    // Update URLList with URLDocument
                    modifyElementInURLList(URLList, wordURLDocument);
                    // Update URLList in DB
                    DB.updateURLList(word, URLList);
                }
            }
        }

        DB.markDonePage(page);
        synchronized (pageCounter) {
            // Get new link from newLinks
            pageCounter.increment();
            System.out.println("New Page Done " + pageCounter.getCount());
        }
        indexPage();

    }

    static Document findDocumentByUrl(ArrayList<Document> array, String url) {
        for (Document element : array) {
            if (element.get("URL").equals(url)) {
                return element;
            }
        }
        return null;
    }

    private static ArrayList<Document> modifyElementInURLList(ArrayList<Document> array, Document newObject) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).get("URL").equals(newObject.get("URL"))) {
                array.set(i, newObject);
            }
        }
        return array;
    }
}
