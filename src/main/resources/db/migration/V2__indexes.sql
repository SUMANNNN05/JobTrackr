-- list/filter by status for one user
CREATE INDEX idx_applications_user_status ON job_applications (user_id, status);
-- weekly analytics + date range filtering
CREATE INDEX idx_applications_user_applied_on ON job_applications (user_id, applied_on);
-- timeline lookups
CREATE INDEX idx_status_history_application ON application_status_history (application_id, changed_at);
