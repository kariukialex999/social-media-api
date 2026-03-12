package org.kariioke.socialmediaapi.controller;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse.UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ApiResponse.UserResponse>> searchUsers(
            @RequestParam("q") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<ApiResponse.UserResponse>> getFollowers(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<ApiResponse.UserResponse>> getFollowing(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFollowing(id));
    }

    @GetMapping("/{id}/follow")
    public ResponseEntity<ApiResponse.MessageResponse> followUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.followUser(id, currentUser));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<ApiResponse.MessageResponse> unfollowUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.unfollowUser(id, currentUser));
    }
}
