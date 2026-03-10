package org.kariioke.socialmediaapi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ApiResponse {

    /*AUTHENTICATION*/
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AuthResponse {
        private String token;
        private String tokenType;
        private UserResponse user;
    }

    /*USER*/
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String bio;
        private String profilePictureUrl;
        private Integer followerCount;
        private Integer followingCount;
        private LocalDateTime createdAt;
    }

    /*POST*/

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PostResponse {
        private Long id;
        private String content;
        private String imageUrl;
        private UserResponse author;
        private int likeCount;
        private int commentCount;
        private boolean likedByCurrentUser;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /*COMMENT*/

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CommentResponse {
        private Long id;
        private String content;
        private UserResponse author;
        private Long postId;
        private LocalDateTime createdAt;
    }

    /*GENERIC WRAPPER*/

    @Data
    @Builder
    public static class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    /*ERROR*/
    /*STANDARD ERROR RESPONSE*/

    @Data
    @Builder
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;
        private List<ValidationError> fieldErrors;

        @Data
        @Builder
        public static class ValidationError {
            private String field;
            private String message;
        }
    }

    /*SUCCESS - simple success message for actions that don't return an entity like liking a post and following a user*/

    @Data
    @Builder
    public static class MessageResponse {
        private String message;
    }
}
