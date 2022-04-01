package de.anmimi.headlines.controller;

import de.anmimi.headlines.data.Headline;
import de.anmimi.headlines.services.HeadlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final HeadlineService headlineService;

    @GetMapping
    public List<Headline> headlinesOfLastThreeHours() {
        return headlineService.getNewestHeadlines();
    }

    @GetMapping("/all")
    public List<Headline> allHeadLines(@RequestParam(required = false) Integer pastHours) {
        if (pastHours != null) {
            return headlineService.findHeadLinesOfLast(pastHours);
        }
        return headlineService.getAllHeadlines();
    }
}
