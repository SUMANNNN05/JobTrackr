package com.jobtrackr.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    Page<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    // ---- analytics (native SQL) ----

    interface StatusCount {
        String getStatus();
        Long getTotal();
    }

    interface WeeklyCount {
        String getWeek();
        Long getTotal();
        Long getCumulative();
    }

    @Query(value = """
            SELECT status AS status, COUNT(*) AS total
            FROM job_applications
            WHERE user_id = :userId
            GROUP BY status
            """, nativeQuery = true)
    List<StatusCount> countByStatus(@Param("userId") Long userId);

    /** Applications per week with a running total (window function). */
    @Query(value = """
            SELECT to_char(w.week_start, 'YYYY-MM-DD') AS week,
                   w.total                             AS total,
                   CAST(SUM(w.total) OVER (ORDER BY w.week_start) AS BIGINT) AS cumulative
            FROM (
                SELECT date_trunc('week', applied_on) AS week_start, COUNT(*) AS total
                FROM job_applications
                WHERE user_id = :userId AND applied_on IS NOT NULL
                GROUP BY date_trunc('week', applied_on)
            ) w
            ORDER BY w.week_start
            """, nativeQuery = true)
    List<WeeklyCount> weeklyApplications(@Param("userId") Long userId);
}
