package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NYTCrawler extends AbstractCrawler {

    private static final String NEW_YORK_TIMES = "https://www.nytimes.com/";
    private final JSoupCrawlerClient jSoupCrawlerClient;

    @Override
    protected Elements executeCrawling() {
        return jSoupCrawlerClient.crawleWithQuery(NEW_YORK_TIMES, "section.smartphone.story-wrapper");
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        String title = element.getElementsByTag("h3").text();
        String link = element.getElementsByTag("a").attr("href");
        return new TitleAndLink(title, link);
    }
}
