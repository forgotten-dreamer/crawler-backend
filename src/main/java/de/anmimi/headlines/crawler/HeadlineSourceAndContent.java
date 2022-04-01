package de.anmimi.headlines.crawler;

import java.util.Map;

public record HeadlineSourceAndContent(String source, Map<String, String> content) {
}
