package com.jobtrackr.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public final class ApplicationDtos {
    private ApplicationDtos() {}

    public record ApplicationRequest(
            @NotBlank @Size(max = 150) String company,
            @NotBlank @Size(max = 150) String role,
            @Size(max = 120) String location,
            @Size(max = 500) String jobUrl,
            ApplicationStatus status,
            LocalDate appliedOn,
            String notes) {}

    public record StatusUpdateRequest(@NotNull ApplicationStatus status) {}

    public record ApplicationResponse(
            Long id, String company, String role, String location, String jobUrl,
            ApplicationStatus status, LocalDate appliedOn, String notes,
            Instant createdAt, Instant updatedAt) {

        public static ApplicationResponse from(JobApplication a) {
            return new ApplicationResponse(a.getId(), a.getCompany(), a.getRole(), a.getLocation(),
                    a.getJobUrl(), a.getStatus(), a.getAppliedOn(), a.getNotes(),
                    a.getCreatedAt(), a.getUpdatedAt());
        }
    }

    public record HistoryResponse(ApplicationStatus from, ApplicationStatus to, Instant changedAt) {
        public static HistoryResponse from(StatusHistory h) {
            return new HistoryResponse(h.getFromStatus(), h.getToStatus(), h.getChangedAt());
        }
    }
}
