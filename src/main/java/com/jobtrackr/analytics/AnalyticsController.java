package com.jobtrackr.analytics;

import com.jobtrackr.analytics.AnalyticsService.Summary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    public record WeekPoint(String week, long total, long cumulative) {}

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) { this.service = service; }

    @GetMapping("/summary")
    public Summary summary(Principal p) { return service.summary(p.getName()); }

    @GetMapping("/weekly")
    public List<WeekPoint> weekly(Principal p) {
        return service.weekly(p.getName()).stream()
                .map(w -> new WeekPoint(w.getWeek(), w.getTotal(), w.getCumulative()))
                .toList();
    }
}
