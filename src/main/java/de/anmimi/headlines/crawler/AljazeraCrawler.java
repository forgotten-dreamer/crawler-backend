package de.anmimi.headlines.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AljazeraCrawler extends AbstractCrawler {

    private static final String ALJAZEERA_COM = "https://www.aljazeera.com";
    private final JSoupCrawlerClient jSoupCrawlerClient;

    @Override
    protected Elements executeCrawling() {
        return jSoupCrawlerClient.crawleWithQuery(ALJAZEERA_COM, "div.fte-article__content");
    }

    @Override
    protected TitleAndLink extractHeadLinesAndLinksFromDom(Element element) {
        String link = ALJAZEERA_COM + element.getElementsByTag("a").attr("href");
        String headlineText = element.getElementsByTag("span").text();
        return new TitleAndLink(headlineText, link);
    }

}
