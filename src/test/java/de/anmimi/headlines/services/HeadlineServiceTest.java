package de.anmimi.headlines.services;


import de.anmimi.headlines.data.Headline;
import de.anmimi.headlines.data.HeadlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadlineServiceTest {

    private static final String TEST_LINK = "test-link";
    private static final String TEST_LINK_1 = "test-link-1";
    private static final String TEST_SOURCE = "test source";

    @Mock
    private HeadlineRepository headlineRepository;
    @Captor
    private ArgumentCaptor<List<Headline>> headlinesResult;
    private HeadlineService headlineService;

    @BeforeEach
    public void setup() {
        headlineService = new HeadlineService(headlineRepository, 1L);
    }

    @Test
    public void testNewestHeadlines() {
        when(headlineRepository.findByCrawlingDateAfterOrderByCrawlingDateDesc(any()))
                .thenReturn(List.of(new Headline("title", "test-link", "test source", LocalDateTime.now())));

        List<Headline> newestHeadlines = headlineService.getNewestHeadlines();
        assertEquals(1, newestHeadlines.size());
    }

    @Test
    public void testSaveNewHeadlines_successful() {
        when(headlineRepository.existsByLinkAndSource(TEST_LINK, TEST_SOURCE)).thenReturn(true);
        when(headlineRepository.existsByLinkAndSource(TEST_LINK_1, TEST_SOURCE)).thenReturn(false);
        when(headlineRepository.saveAll(headlinesResult.capture())).thenReturn(null);

        List<Headline> testHeadlines = List.of(
                new Headline("title", TEST_LINK, TEST_SOURCE, LocalDateTime.now()),
                new Headline("title-a", TEST_LINK_1, TEST_SOURCE, LocalDateTime.now()));

        headlineService.saveNewHeadlines(testHeadlines);

        List<Headline> result = headlinesResult.getValue();
        assertEquals(1, result.size());
        Headline headline = result.get(0);
        assertEquals(TEST_LINK_1, headline.getLink());
        assertEquals(TEST_SOURCE, headline.getSource());
    }

    @Test
    public void testSaveNewHeadlines_allEntriesFiltered() {
        when(headlineRepository.existsByLinkAndSource(TEST_LINK, TEST_SOURCE)).thenReturn(true);
        when(headlineRepository.existsByLinkAndSource(TEST_LINK_1, TEST_SOURCE)).thenReturn(true);

        List<Headline> testHeadlines = List.of(
                new Headline("title", TEST_LINK, TEST_SOURCE, LocalDateTime.now()),
                new Headline("title-a", TEST_LINK_1, TEST_SOURCE, LocalDateTime.now()));

        headlineService.saveNewHeadlines(testHeadlines);

        verify(headlineRepository, times(0)).saveAll(any());
    }

    @Test
    public void testSaveNewHeadlines_emptyList() {
        headlineService.saveNewHeadlines(new ArrayList<>());
        verify(headlineRepository, times(0)).saveAll(any());
    }

}