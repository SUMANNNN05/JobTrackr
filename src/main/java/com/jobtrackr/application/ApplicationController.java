package com.jobtrackr.application;

import com.jobtrackr.application.ApplicationDtos.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(Principal p, @Valid @RequestBody ApplicationRequest req) {
        return service.create(p.getName(), req);
    }

    @GetMapping
    public PagedModel<ApplicationResponse> list(
            Principal p,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // cap page size to protect the database
        Pageable safe = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), pageable.getSort());
        return new PagedModel<>(service.list(p.getName(), status, safe));
    }

    @GetMapping("/{id}")
    public ApplicationResponse get(Principal p, @PathVariable Long id) {
        return service.get(p.getName(), id);
    }

    @PutMapping("/{id}")
    public ApplicationResponse update(Principal p, @PathVariable Long id,
                                      @Valid @RequestBody ApplicationRequest req) {
        return service.update(p.getName(), id, req);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(Principal p, @PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest req) {
        return service.updateStatus(p.getName(), id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Principal p, @PathVariable Long id) {
        service.delete(p.getName(), id);
    }

    @GetMapping("/{id}/history")
    public List<HistoryResponse> history(Principal p, @PathVariable Long id) {
        return service.history(p.getName(), id);
    }
}
