package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TheGuardianCrawler extends AbstractCrawler {

    private static final String THE_GUARDIAN = "https://www.theguardian.com/international";
    private final JSoupCrawlerClient jSoupCrawlerClient;

    @Override
    protected Elements executeCrawling() {
        return jSoupCrawlerClient.crawleWithQuery(THE_GUARDIAN, "a[data-link-name='article']");
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        return new TitleAndLink(element.text(), element.attr("href"));
    }
}
