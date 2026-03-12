package org.kariioke.socialmediaapi.service;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.CommentRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.Comment;
import org.kariioke.socialmediaapi.entity.Post;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.exception.ForbiddenException;
import org.kariioke.socialmediaapi.exception.ResourceNotFoundException;
import org.kariioke.socialmediaapi.repository.CommentRepository;
import org.kariioke.socialmediaapi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /*ADD A COMMENT TO A POST*/

    @Transactional
    public ApiResponse.CommentResponse addComment(Long postId, CommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(currentUser)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToCommentResponse(saved);
    }

    /*GET COMMENTS FROM A POST*/
    public ApiResponse.PagedResponse<ApiResponse.CommentResponse> getPostComments(
            Long postId, int page, int size
    ) {
        if(postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        Page<Comment> commentPage = commentRepository
                .findByPostIdOrderByCreatedAtAsc(postId, PageRequest.of(page, size));
        return ApiResponse.PagedResponse.<ApiResponse.CommentResponse>builder()
                .content(commentPage.getContent().stream().map(this::mapToCommentResponse).toList())
                .size(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .last(commentPage.isLast())
                .build();
    }


    /*DELETE*/

    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id : " + commentId));
        if (comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }


    /*HELPER MAPPER METHODS*/
    private ApiResponse.CommentResponse mapToCommentResponse(Comment comment) {
        return ApiResponse.CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(AuthService.mapToUserResponse(comment.getPost().getAuthor()))
                .postId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
