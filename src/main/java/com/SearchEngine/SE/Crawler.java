package com.SearchEngine.SE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Crawler {
    public static void main(String[] args) {
        // Create DBController
        DBController DB = new DBController();

        // If visitedLinks is empty
        if (DB.getVisitedLinksCount() == 0) {
            // Reload the seed
            DB.loadInitSeed();
        }
        else
        {
            DB.resetProcessingLink();
        }

        // Create linkCounter
        Counter linkCounter = new Counter(DB);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of threads to run:");
        String numThreads = scanner.nextLine();
        scanner.close();

        for (int i = 0; i < Integer.parseInt(numThreads); i++) {
            Thread thread = new Thread(new CrawlerThread(linkCounter, DB), "T-" + i);
            thread.start();
        }
    }
}

class Counter {
    private int linksCount;
    private final int maxCount;

    public Counter(DBController DB) {
        this.maxCount = 5000;
        this.linksCount = DB.getVisitedLinksCount();
    }

    public void increment() {
        linksCount++;
    }

    public void decrement() {
        linksCount--;
    }

    public int getCount() {
        return linksCount;
    }

    public final int getMaxCount() {
        return maxCount;
    }
}

class CrawlerThread implements Runnable {
    private final Counter linkCounter;
    private final DBController DB;

    public CrawlerThread (Counter linkCounter, DBController DB) {
        this.linkCounter = linkCounter;
        this.DB = DB;
    }

    @Override
    public void run() {
        try {
            crawl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void crawl() throws IOException {
        // Finish condition
        if (linkCounter.getCount() >= linkCounter.getMaxCount()) {
            return;
        }

        String link;

        // Start CS: Fetch a link and set it to processing
        synchronized (linkCounter) {
            // Get new link from newLinks
            link = DB.getNewUnprocessedLink();
        }
        // End CS

        // If new unprocessed link not visited before
        if (!DB.visitedBefore(link)) {
            // Create page object
            Page linkPage = new Page(link, ((linkCounter.getCount() + DB.getNewLinksCount()) < linkCounter.getMaxCount()), DB, linkCounter);

            // If link not in pages
            if (!DB.pageSaved(link)) {
                // Save page
                linkPage.savePage(DB);
            }
            // Extract links
            ArrayList<String> extractedLinks = linkPage.getHyperlinks();

            System.out.println("Start Transmission at Thread " + Thread.currentThread().getName() + " and extracting Links from: " + link);

                // For each extracted link
            for (String extractedLink : extractedLinks) {
                // If extractedLink is a new link
                if (DB.isNewLink(extractedLink)) {
                    // Add extracted link to newLinks
                    DB.addToNewLinks(extractedLink);
                }
            }
            // Add new link to visitedLinks
            DB.addToVisitedLinks(link);
            System.out.println("End Transmission at Thread " + Thread.currentThread().getName());

            // Start CS: Increment counter
            synchronized (linkCounter) {
                linkCounter.increment();
                System.out.println("END THREAD "+ Thread.currentThread().getName() + " at count " + linkCounter.getCount());
            }
            // End CS

        }

        // Remove link from newLinks
        DB.removeFromNewLinks(link);

        // Increment linkCounter

        crawl();
    }
}
