package com.jobtrackr.analytics;

import com.jobtrackr.application.ApplicationStatus;
import com.jobtrackr.application.JobApplicationRepository;
import com.jobtrackr.application.JobApplicationRepository.WeeklyCount;
import com.jobtrackr.exception.NotFoundException;
import com.jobtrackr.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    public record Summary(long total, long applied, Map<String, Long> byStatus,
                          double interviewRate, double offerRate) {}

    private final JobApplicationRepository repo;
    private final UserRepository users;

    public AnalyticsService(JobApplicationRepository repo, UserRepository users) {
        this.repo = repo;
        this.users = users;
    }

    private Long userId(String email) {
        return users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found")).getId();
    }

    public Summary summary(String email) {
        Map<ApplicationStatus, Long> counts = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus s : ApplicationStatus.values()) counts.put(s, 0L);
        repo.countByStatus(userId(email))
                .forEach(r -> counts.put(ApplicationStatus.valueOf(r.getStatus()), r.getTotal()));

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        long applied = total - counts.get(ApplicationStatus.WISHLIST);
        long interviews = counts.get(ApplicationStatus.INTERVIEW) + counts.get(ApplicationStatus.OFFER);
        long offers = counts.get(ApplicationStatus.OFFER);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        counts.forEach((k, v) -> byStatus.put(k.name(), v));
        return new Summary(total, applied, byStatus, rate(interviews, applied), rate(offers, applied));
    }

    public List<WeeklyCount> weekly(String email) {
        return repo.weeklyApplications(userId(email));
    }

    private static double rate(long part, long whole) {
        return whole == 0 ? 0.0 : Math.round(part * 10000.0 / whole) / 100.0;
    }
}
