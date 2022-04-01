package de.anmimi.headlines.services;

import de.anmimi.headlines.config.HeadlinesConfig;
import de.anmimi.headlines.crawler.Crawler;
import de.anmimi.headlines.crawler.HeadlineSourceAndContent;
import de.anmimi.headlines.data.Headline;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrawlerServiceTest {

    private static final List<String> MATCHING_WORDS = List.of("test", "matching");
    private static final List<Headline> HEADLINES = List.of(
            new Headline("test", "test-link", "TestSourceOne", LocalDateTime.now()));

    private List<Crawler> MOCKED_CRAWLERS;
    private HeadlinesConfig headlinesConfig = new HeadlinesConfig(MATCHING_WORDS);
    @Mock
    private HeadlineService headlineService;
    @Captor
    private ArgumentCaptor<List<Headline>> headlineResult;

    private CrawlerService crawlerService;

    @BeforeEach
    public void setup() {
        MOCKED_CRAWLERS = new ArrayList<>();
        crawlerService = new CrawlerService(headlinesConfig, MOCKED_CRAWLERS, headlineService);

        when(headlineService.getNewestHeadlines()).thenReturn(HEADLINES);
        doNothing().when(headlineService).saveNewHeadlines(headlineResult.capture());
    }


    @Test
    public void testLoadHeadlines_successful() {
        MOCKED_CRAWLERS.addAll(List.of(
                //First not matching since exisiting Databse entry
                CrawlerServiceMock.create("TestSourceOne", Map.of("test", "test-link",
                        "matching", "test-link-matching-1")),
                CrawlerServiceMock.create("DuplicateEntry", Map.of("test", "test-link",
                        "matching", "test-link-matching-2")),
                CrawlerServiceMock.create("TestSourceFour", Map.of("notMatching", "test-link"))
        ));

        crawlerService.loadHeadlines();
        List<Headline> allValues = headlineResult.getValue();
        Assertions.assertEquals(3, allValues.size());

        mockDatabaseUpdateWithEntries(allValues);
        crawlerService.loadHeadlines();

        Mockito.verify(headlineService, times(2)).getNewestHeadlines();
        Assertions.assertEquals(0, headlineResult.getAllValues().get(1).size());
    }

    private void mockDatabaseUpdateWithEntries(List<Headline> allValues) {
        List<Headline> updatedHeadlines = new ArrayList<>(HEADLINES);
        updatedHeadlines.addAll(allValues);
        when(headlineService.getNewestHeadlines()).thenReturn(updatedHeadlines);
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class CrawlerServiceMock implements Crawler {
        private final Supplier<CompletableFuture<HeadlineSourceAndContent>> supplier;

        @Override
        public CompletableFuture<HeadlineSourceAndContent> crawleForHeadlines() {
            return supplier.get();
        }

        static CrawlerServiceMock create(String source, Map<String, String> content) {
            HeadlineSourceAndContent headlineSourceAndContent1 = new HeadlineSourceAndContent(source, content);
            return new CrawlerServiceMock(() -> CompletableFuture.completedFuture(headlineSourceAndContent1));
        }
    }
}