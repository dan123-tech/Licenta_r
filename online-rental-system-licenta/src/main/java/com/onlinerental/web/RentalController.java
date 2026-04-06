package com.onlinerental.web;

import com.onlinerental.domain.User;
import com.onlinerental.repository.UserRepository;
import com.onlinerental.service.RentalService;
import com.onlinerental.web.dto.ApiResponse;
import com.onlinerental.web.dto.ConditionCheckRequest;
import com.onlinerental.web.dto.RentalDto;
import com.onlinerental.web.dto.RentalRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public RentalDto create(@Valid @RequestBody RentalRequest req, @AuthenticationPrincipal UserDetails principal) {
        return rentalService.create(req, principal.getUsername());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<RentalDto> my(@AuthenticationPrincipal UserDetails principal) {
        return rentalService.myRentals(principal.getUsername());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPEROWNER')")
    public List<RentalDto> all() {
        return rentalService.allRentals();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public RentalDto get(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return rentalService.getById(id, u);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SUPEROWNER')")
    public ApiResponse status(
            @PathVariable Long id,
            @RequestParam String newStatus,
            @AuthenticationPrincipal UserDetails principal
    ) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return rentalService.updateStatus(id, newStatus, u);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPEROWNER')")
    public ApiResponse delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return rentalService.delete(id, u);
    }

    @PostMapping("/{id}/check-condition")
    @PreAuthorize("hasAnyRole('ADMIN','SUPEROWNER')")
    public ApiResponse checkCondition(
            @PathVariable Long id,
            @Valid @RequestBody ConditionCheckRequest req,
            @AuthenticationPrincipal UserDetails principal
    ) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return rentalService.checkCondition(id, req, u);
    }

    @PostMapping("/{id}/generate-awb")
    @PreAuthorize("hasAnyRole('ADMIN','SUPEROWNER')")
    public ApiResponse awb(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return rentalService.generateAwb(id, u);
    }
}
