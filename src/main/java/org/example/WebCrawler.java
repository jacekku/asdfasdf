package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler {
    private static final int MAX_THREADS = 1;
    public static void main(String... args) {
        String url = "https://brahmamuhurta.jacekku.net/";
        if (args.length == 1) {
            url = args[0];
        }

        startCrawler(url);
    }

    public static void startCrawler(String url) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        ConcurrentHashMap<String, Integer> links = new ConcurrentHashMap<>();
        DBConn dbConn = new DBConn();
        dbConn.connect();

        dbConn.createTables();
        dbConn.insertRow(url);

        for (int i = 0; i < MAX_THREADS; i++) {
            executor.submit(new CrawlerThread(dbConn, links));
        }
        executor.shutdown();
    }
}