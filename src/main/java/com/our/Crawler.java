package com.our;

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
        crawl();
    }

    public void crawl() {
        while (true) {
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

                    // If link not in pages
                    if (!DB.pageSaved(link)) {
                        // Save page

                    }
                    // Extract links

                    // For each extracted link

                        // If extractedLink is a new link
                        // if (DB.isNewLink(extractedLink)) {
                            // Add extracted link to newLinks
                            // DB.addToNewLinks(extractedLink);
                        // }
                    // Add new link to visitedLinks
                    DB.addToVisitedLinks(link);
                }
                // Remove link from newLinks
                DB.removeFromNewLinks(link);
                // Increment linkCounter
                linkCounter.increment();
            }
        }
    }
}
