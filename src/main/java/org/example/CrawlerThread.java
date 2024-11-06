package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CrawlerThread implements Runnable {
    private String url;
    private DBConn dbConn;

    public CrawlerThread(String url, DBConn dbConn) {
        this.url = url;
        this.dbConn = dbConn;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .get();
            Elements links = doc.select("a[href]"); // Extract all links

            for (Element link : links) {
                String linkUrl = link.absUrl("href");
                if (!dbConn.isVisited(linkUrl)) { // Check if link is already visited
                    dbConn.insertRow(linkUrl);
                    // Submit new thread for the newly found link
                    new Thread(new CrawlerThread(linkUrl, dbConn)).start();
                } else {
                    dbConn.incrementSeen(linkUrl); // Increment seen count
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}