package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CrawlerThread implements Runnable {
    private final DBConn dbConn;
    private final ConcurrentHashMap<String, Integer> visitedLinks;

    public CrawlerThread(DBConn dbConn, ConcurrentHashMap<String, Integer> visitedLinks) {
        this.dbConn = dbConn;
        this.visitedLinks = visitedLinks;
    }

    @Override
    public void run() {
        while (true) {
            String url = dbConn.getLink();

            if (url == null) {
                break;
            }

            Integer amount = visitedLinks.getOrDefault(url, 0);
            if (amount == 0) {
                visitedLinks.put(url, 1);
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");

                    for (Element link : links) {
                        String linkUrl = link.absUrl("href");
                        if (!dbConn.isVisited(linkUrl)) {
                            dbConn.insertRow(linkUrl);
                        } else {
                            dbConn.incrementSeen(linkUrl);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            visitedLinks.remove(url);
        }
    }
}
