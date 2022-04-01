package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpiegelCrawler extends AbstractCrawler {

    private static final String SPIEGEL_DE = "https://www.spiegel.de/";
    private final JSoupCrawlerClient jSoupCrawlerClient;

    @Override
    protected Elements executeCrawling() {
        return jSoupCrawlerClient.crawleWithQuery(SPIEGEL_DE, "article[aria-label]");
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        String title = element.attr("aria-label").toLowerCase();
        String link = element.getElementsByTag("a").attr("href");
        return new TitleAndLink(title, link);
    }

}
