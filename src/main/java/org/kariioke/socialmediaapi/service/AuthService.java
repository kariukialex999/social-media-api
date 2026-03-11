package org.kariioke.socialmediaapi.service;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.AuthRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.exception.ConflictException;
import org.kariioke.socialmediaapi.repository.UserRepository;
import org.kariioke.socialmediaapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /*REGISTERING A NEW USER*/

    @Transactional
    public ApiResponse.AuthResponse register(AuthRequest.Register request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with email '" + request.getEmail() + "' already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return ApiResponse.AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(savedUser))
                .build();
    }

    /*LOGIN A USER*/

    public ApiResponse.AuthResponse login(AuthRequest.Login request) {
        // authenticationManager.authenticate() handles everything
        //      1. Loads user via UserDetailsService
        //      2. Verifies password with BCrypt
        //      3. Throws BadCredentialsException if wrong
        // We just need to catch generic Exception if we want custom messages

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        //if we reach here, authentication succeeded - load user and generate token

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return ApiResponse.AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }


    /*MAPPER HELPER CLASSES*/

    public static ApiResponse.UserResponse mapToUserResponse(User user) {
        return ApiResponse.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .followerCount(user.getFollowers().size())
                .followingCount(user.getFollowing().size())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
