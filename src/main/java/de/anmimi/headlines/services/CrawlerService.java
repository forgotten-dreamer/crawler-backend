package de.anmimi.headlines.services;

import de.anmimi.headlines.config.HeadlinesConfig;
import de.anmimi.headlines.crawler.Crawler;
import de.anmimi.headlines.crawler.HeadlineSourceAndContent;
import de.anmimi.headlines.data.Headline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CrawlerService {

    private final List<String> matchingWords;
    private final List<Crawler> crawlers;
    private final HeadlineService headlineService;

    @Autowired
    public CrawlerService(HeadlinesConfig headlinesConfig, List<Crawler> crawlers, HeadlineService headlineService) {
        this.matchingWords = headlinesConfig.matchingWords();
        this.crawlers = crawlers;
        this.headlineService = headlineService;
    }

    @Scheduled(fixedDelayString = "${de.anmimi.headlines.crawlingDelayInHours}",
            timeUnit = TimeUnit.HOURS,
            initialDelay = 0)
    public void loadHeadlines() {
        Stream<Headline> headlinesStream = crawlers.stream()
                .map(Crawler::crawleForHeadlines)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        c -> c.stream().map(CompletableFuture::join)))
                .flatMap(this::filterAndParseToHeadlines);

        List<Headline> headlines = filterHeadlinesAgainstLatestDatabaseEntries(headlinesStream);

        log.info("Found: {} new Headlines compared to last hour", headlines.size());
        headlineService.saveNewHeadlines(headlines);
    }

    private List<Headline> filterHeadlinesAgainstLatestDatabaseEntries(Stream<Headline> headlines) {
        List<Headline> latestHeadlinesFromDatabase = headlineService.getNewestHeadlines();
        return headlines.filter(h -> !latestHeadlinesFromDatabase.contains(h))
                .toList();
    }

    private Stream<Headline> filterAndParseToHeadlines(HeadlineSourceAndContent content) {
        Set<Map.Entry<String, String>> entries = content.content().entrySet();
        log.debug("Crawled Headlines count for: {} is {}", content.source(), entries.size());
        return entries.stream()
                .filter(e -> checkIfHeadlineIsQualifiedByImportantWords(e.getKey()))
                .map(e -> new Headline(e.getKey(), e.getValue(), content.source(), LocalDateTime.now()));
    }

    private boolean checkIfHeadlineIsQualifiedByImportantWords(String headline) {
        log.trace("Headline to be matched against important wordlist: {}", headline);
        return Arrays.stream(headline.split("\\s"))
                .map(String::toLowerCase)
                .anyMatch(matchingWords::contains);
    }


}
