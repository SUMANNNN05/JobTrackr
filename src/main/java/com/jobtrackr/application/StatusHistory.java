package com.jobtrackr.application;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "application_status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private ApplicationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private ApplicationStatus toStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    protected StatusHistory() {}

    public StatusHistory(Long applicationId, ApplicationStatus from, ApplicationStatus to) {
        this.applicationId = applicationId;
        this.fromStatus = from;
        this.toStatus = to;
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public ApplicationStatus getFromStatus() { return fromStatus; }
    public ApplicationStatus getToStatus() { return toStatus; }
    public Instant getChangedAt() { return changedAt; }
}
