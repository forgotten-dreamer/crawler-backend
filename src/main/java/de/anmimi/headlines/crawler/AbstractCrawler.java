package de.anmimi.headlines.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;

import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractCrawler implements Crawler {

    @Override
    @Async
    public CompletableFuture<HeadlineSourceAndContent> crawleForHeadlines() {
        String simpleName = getSourceNameFromCrawler();
        log.debug(simpleName);
        long currentTime = System.currentTimeMillis();

        Elements elements = executeCrawling();
        LinkedHashMap<String, String> result = removeDupplicateEntriesByValueAndCollectToMap(elements);

        log.debug("{} took time: {}", simpleName, System.currentTimeMillis() - currentTime);
        return CompletableFuture.completedFuture(new HeadlineSourceAndContent(simpleName, result));
    }

    private LinkedHashMap<String, String> removeDupplicateEntriesByValueAndCollectToMap(Elements elements) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (Element element : elements) {
            TitleAndLink titleAndLink = extractHeadLinesAndLinksFromDom(element);
            if (!result.containsValue(titleAndLink.link())) {
                result.put(titleAndLink.title(), titleAndLink.link());
            }
        }
        return result;
    }

    private String getSourceNameFromCrawler() {
        String simpleName = this.getClass().getSimpleName();
        simpleName = simpleName.substring(0, simpleName.indexOf("Crawler"));
        return simpleName;
    }

    protected abstract Elements executeCrawling();

    protected abstract TitleAndLink extractHeadLinesAndLinksFromDom(Element element);

    protected record TitleAndLink(String title, String link) {
    }
}
