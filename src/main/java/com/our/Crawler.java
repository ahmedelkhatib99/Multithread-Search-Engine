package com.our;

import java.io.IOException;
import java.util.ArrayList;

public class Crawler {
    public static void main(String[] args) {
        // Create DBController
        DBController DB = new DBController();

        // If visitedLinks is empty
        if (DB.getVisitedLinksCount() == 0) {
            // Reload the seed
            DB.loadInitSeed();
        }

        // Create linkCounter
        Counter linkCounter = new Counter(DB);

        Thread t1 = new Thread(new CrawlerThread(linkCounter, DB), "T1");
        Thread t2 = new Thread(new CrawlerThread(linkCounter, DB), "T2");

        t1.start();
        t2.start();
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

        synchronized (linkCounter) {
            // Get new link from newLinks
            String link = DB.getNewLink();

            // If new link not visited before
            if (!DB.visitedBefore(link)) {
                // Create page object
                Page linkPage = new Page(link, ((linkCounter.getCount() + DB.getNewLinksCount()) < linkCounter.getMaxCount()));

                // If link not in pages
                if (!DB.pageSaved(link)) {
                    // Save page
                    linkPage.savePage(DB);
                }
                // Extract links
                ArrayList<String> extractedLinks = linkPage.getHyperlinks();
                System.out.println("Links Extracted");
                System.out.println(extractedLinks);

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
            }

            // Remove link from newLinks
            DB.removeFromNewLinks(link);
            // Increment linkCounter
            linkCounter.increment();
        }
        crawl();
    }
}
