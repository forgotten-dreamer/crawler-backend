package de.anmimi.headlines.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HeadlineRepository extends MongoRepository<Headline, Long> {
    List<Headline> findByCrawlingDateAfterOrderByCrawlingDateDesc(LocalDateTime localDateTime);

    boolean existsByLinkAndSource(String link, String source);
}
