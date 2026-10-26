package com.jobtrackr.application;

import com.jobtrackr.application.ApplicationDtos.*;
import com.jobtrackr.exception.NotFoundException;
import com.jobtrackr.user.User;
import com.jobtrackr.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final JobApplicationRepository applications;
    private final StatusHistoryRepository history;
    private final UserRepository users;

    public ApplicationService(JobApplicationRepository applications,
                              StatusHistoryRepository history, UserRepository users) {
        this.applications = applications;
        this.history = history;
        this.users = users;
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private JobApplication owned(String email, Long id) {
        return applications.findByIdAndUserId(id, user(email).getId())
                .orElseThrow(() -> new NotFoundException("Application " + id + " not found"));
    }

    public ApplicationResponse create(String email, ApplicationRequest req) {
        JobApplication app = new JobApplication(user(email));
        apply(app, req);
        app.setStatus(req.status() != null ? req.status() : ApplicationStatus.WISHLIST);
        JobApplication saved = applications.save(app);
        history.save(new StatusHistory(saved.getId(), null, saved.getStatus()));
        return ApplicationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> list(String email, ApplicationStatus status, Pageable pageable) {
        Long userId = user(email).getId();
        Page<JobApplication> page = status == null
                ? applications.findByUserId(userId, pageable)
                : applications.findByUserIdAndStatus(userId, status, pageable);
        return page.map(ApplicationResponse::from);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(String email, Long id) {
        return ApplicationResponse.from(owned(email, id));
    }

    public ApplicationResponse update(String email, Long id, ApplicationRequest req) {
        JobApplication app = owned(email, id);
        ApplicationStatus before = app.getStatus();
        apply(app, req);
        if (req.status() != null && req.status() != before) {
            app.setStatus(req.status());
            history.save(new StatusHistory(app.getId(), before, req.status()));
        }
        return ApplicationResponse.from(applications.save(app));
    }

    public ApplicationResponse updateStatus(String email, Long id, ApplicationStatus newStatus) {
        JobApplication app = owned(email, id);
        ApplicationStatus before = app.getStatus();
        if (before != newStatus) {
            app.setStatus(newStatus);
            history.save(new StatusHistory(app.getId(), before, newStatus));
        }
        return ApplicationResponse.from(applications.save(app));
    }

    public void delete(String email, Long id) {
        applications.delete(owned(email, id));
    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> history(String email, Long id) {
        JobApplication app = owned(email, id);
        return history.findByApplicationIdOrderByChangedAtAsc(app.getId())
                .stream().map(HistoryResponse::from).toList();
    }

    private void apply(JobApplication app, ApplicationRequest req) {
        app.setCompany(req.company().trim());
        app.setRole(req.role().trim());
        app.setLocation(req.location());
        app.setJobUrl(req.jobUrl());
        app.setAppliedOn(req.appliedOn());
        app.setNotes(req.notes());
    }
}
