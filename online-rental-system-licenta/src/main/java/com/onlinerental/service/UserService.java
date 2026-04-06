package com.onlinerental.service;

import com.onlinerental.domain.User;
import com.onlinerental.repository.UserRepository;
import com.onlinerental.web.dto.ApiResponse;
import com.onlinerental.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> listAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getUsername))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ApiResponse promoteSuperOwner(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent"));
        user.getRoles().add("ROLE_SUPEROWNER");
        user.getRoles().add("ROLE_ADMIN");
        return ApiResponse.ok("Utilizator promovat super owner.");
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.isVerified(),
                u.getRoles().stream().sorted().toList()
        );
    }
}
