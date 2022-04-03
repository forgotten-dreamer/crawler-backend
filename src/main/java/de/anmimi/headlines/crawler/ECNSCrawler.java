package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ECNSCrawler extends AbstractCrawler {

    private final JSoupCrawlerClient jSoupCrawlerClient;
    private static final String ECNS = "http://www.ecns.cn/";

    @Override
    protected Elements executeCrawling() {
        return jSoupCrawlerClient.crawleWithQuery(ECNS, "a");
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        String title = element.text();
        String href = element.attr("href");
        return new TitleAndLink(title, href);
    }
}
