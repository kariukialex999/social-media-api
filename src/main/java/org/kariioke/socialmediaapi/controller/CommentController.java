package org.kariioke.socialmediaapi.controller;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.CommentRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse.CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentService.addComment(postId, request, currentUser));
    }

    @GetMapping
    public ResponseEntity<ApiResponse.PagedResponse<ApiResponse.CommentResponse>> getComments (
            @PathVariable Long postId,
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(commentService.getPostComments(postId, page, size));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
