package com.our;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.lang.reflect.Array;
import java.util.Scanner; // Import the Scanner class to read text files

import opennlp.tools.stemmer.PorterStemmer;

import java.util.ArrayList;

public class Indexer {
    public static void main(String[] args) throws FileNotFoundException {
        // Initialize DBController
        DBController DB = new DBController();

        // Drop indexer collection
        DB.dropIndexerCollection();

        // Read stopwords
        ArrayList<String> stopwords = new ArrayList<>();
        loadStopwords(stopwords);

        // Create PortStemmer
        PorterStemmer porterStemmer = new PorterStemmer();

        // Get pages
        FindIterable<Document> pages = DB.getPages();

        // For each page in pages
        for (Document page : pages) {
            System.out.println("New Page");
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
        }
    }

    private static Document findDocumentByUrl(ArrayList<Document> array, String url) {
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

    private static void loadStopwords(ArrayList<String> stopwords) throws FileNotFoundException {
        File fileObj = new File("stopwords.txt");
        Scanner myReader = new Scanner(fileObj);
        while (myReader.hasNextLine()) {
            stopwords.add(myReader.nextLine().toLowerCase());
        }
    }
}
