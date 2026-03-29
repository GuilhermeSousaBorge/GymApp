package backend.dashboard.controller;

import backend.dashboard.dto.AdminDashboardResponse;
import backend.dashboard.dto.StudentDashboardResponse;
import backend.dashboard.usecase.GetAdminDashboardUseCase;
import backend.dashboard.usecase.GetStudentDashboardUseCase;
import backend.infrastructure.security.IsAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final GetAdminDashboardUseCase getAdminDashboardUseCase;
    private final GetStudentDashboardUseCase getStudentDashboardUseCase;

    @GetMapping("/admin")
    @IsAdmin
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(getAdminDashboardUseCase.execute());
    }

    @GetMapping("/student")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(getStudentDashboardUseCase.execute(userId));
    }
}
