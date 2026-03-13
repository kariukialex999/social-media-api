package org.kariioke.socialmediaapi.controller;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.PostRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse.PostResponse> createPost(
            @RequestBody PostRequest.Create request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse.PostResponse> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.getPostById(id, currentUser));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse.PagedResponse<ApiResponse.PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.getUserPosts(userId, page, size, currentUser));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse.PagedResponse<ApiResponse.PostResponse>> getFeed(
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.getFeed(currentUser, page, size));
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse.PagedResponse<ApiResponse.PostResponse>> explorePosts (
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.explorePosts(page, size, currentUser));
    }

    /*UPDATE*/
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse.PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest.Update request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.updatePost(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost (
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        postService.deletePost(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    /*LIKES*/
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse.PostResponse> toggleLikes(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.toggleLikes(id, currentUser));
    }
}
