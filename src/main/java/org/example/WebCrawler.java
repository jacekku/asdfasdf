package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler {
    private static final int MAX_THREADS = 5; // Define maximum number of threads

    public static void main(String... args) {
        String url = "https://brahmamuhurta.jacekku.net/";
        if (args.length == 1) {
            url = args[0];
        }

        startCrawler(url);
    }

    public static void startCrawler(String url) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        DBConn dbConn = new DBConn();
        dbConn.connect();

        // Create tables if not exist
        dbConn.createTables();

        // Start the crawling process
        executor.submit(new CrawlerThread(url, dbConn));

        executor.shutdown();
    }
}