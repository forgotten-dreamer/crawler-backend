package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NineGagCrawler extends AbstractCrawler {

    private final JSoupCrawlerClient client;
    private static final String NINEGAG = "https://9gag.com";


    @Override
    protected Elements executeCrawling() {
        Elements header = client.crawleWithQuery(NINEGAG, "article");
        return header;
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        String link = NINEGAG + element.getElementsByTag("a").attr("href");
        String title = element.getElementsByTag("h1").text();
        return new TitleAndLink(title, link);
    }
}
