package de.anmimi.headlines.services;

import de.anmimi.headlines.data.Headline;
import de.anmimi.headlines.data.HeadlineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class HeadlineService {

    private final HeadlineRepository headlineRepository;
    private final long newsRenewalTime;

    @Autowired
    public HeadlineService(HeadlineRepository headlineRepository,
                           @Value("${de.anmimi.headlines.crawlingDelayInHours}") Long newsRenewalTime) {
        this.headlineRepository = headlineRepository;
        this.newsRenewalTime = newsRenewalTime;
    }

    @Cacheable("headlines")
    public List<Headline> getNewestHeadlines() {
        return headlineRepository.findByCrawlingDateAfterOrderByCrawlingDateDesc(LocalDateTime.now()
                .minusHours(newsRenewalTime));
    }

    @CacheEvict(allEntries = true, cacheNames = "headlines")
    public void saveNewHeadlines(List<Headline> headlines) {
        List<Headline> filteredHeadlines = headlines.stream()
                .filter(h -> !headlineRepository.existsByLinkAndSource(h.getLink(), h.getSource()))
                .toList();

        log.info("{} entries will be saved", filteredHeadlines.size());
        if (filteredHeadlines.isEmpty()) {
            return;
        }

        headlineRepository.saveAll(filteredHeadlines);
    }

    public List<Headline> getAllHeadlines() {
        return headlineRepository.findAll(Sort.by("crawlingDate"));
    }

    public List<Headline> findHeadLinesOfLast(int pastHours) {
        return headlineRepository.findByCrawlingDateAfterOrderByCrawlingDateDesc(LocalDateTime.now()
                .minusHours(pastHours));
    }
}
