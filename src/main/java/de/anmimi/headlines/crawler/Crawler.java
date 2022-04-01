package de.anmimi.headlines.crawler;

import java.util.concurrent.CompletableFuture;

public interface Crawler {
    CompletableFuture<HeadlineSourceAndContent> crawleForHeadlines();
}
