package de.anmimi.headlines.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "de.anmimi.headlines")
public record HeadlinesConfig(List<String> matchingWords) {
    public HeadlinesConfig {
        matchingWords = matchingWords.stream()
                .map(String::toLowerCase)
                .toList();
    }
}
