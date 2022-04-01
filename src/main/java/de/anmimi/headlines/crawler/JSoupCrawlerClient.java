package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JSoupCrawlerClient {

    public Elements crawleWithQuery(String uri, String query) {
        log.info("crawling: {} with query: {}", uri, query);
        Connection connect = Jsoup.connect(uri);
        try {
            return connect.get().select(query);
        } catch (IOException e) {
            log.error("Error loading: {} with select query {}", uri, query, e);
            return new Elements();
        }
    }


}
