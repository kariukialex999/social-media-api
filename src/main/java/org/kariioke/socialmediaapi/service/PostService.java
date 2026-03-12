package org.kariioke.socialmediaapi.service;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.PostRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.Post;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.exception.ForbiddenException;
import org.kariioke.socialmediaapi.exception.ResourceNotFoundException;
import org.kariioke.socialmediaapi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /*CREATE*/

    @Transactional
    public ApiResponse.PostResponse createPost(PostRequest.Create request, User currentUser) {
        Post post = Post.builder()
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(currentUser)
                .build();

        Post saved = postRepository.save(post);
        return mapToPostResponse(saved, currentUser);
    }

    /*READ*/
    //getting one post by ID
    public ApiResponse.PostResponse getPostById(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return mapToPostResponse(post, currentUser);
    }


    //get paginated posts by a specific user
    public ApiResponse.PagedResponse<ApiResponse.PostResponse> getUserPosts(
            Long userId, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
        return mapToPagedResponse(postPage, currentUser);
    }

    //Get the current user's feeds - posts from people they follow
    public ApiResponse.PagedResponse<ApiResponse.PostResponse> getFeed(
            User currentUser, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findFeedForUser(currentUser.getId(), pageable);
        return mapToPagedResponse(postPage, currentUser);
    }

    //Explore/ discover page - all posts, newest first
    public ApiResponse.PagedResponse<ApiResponse.PostResponse> explorePosts(
            int page, int size, User currentUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return mapToPagedResponse(postPage, currentUser);
    }

    /*UPDATE*/
    @Transactional
    public ApiResponse.PostResponse updatePost(Long postId, PostRequest.Update request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        //authorization: only the post's author can edit it
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only edit your own posts");
        }
        post.setContent(request.getContent());
        Post saved = postRepository.save(post);
        return mapToPostResponse(saved, currentUser);
    }

    /*DELETE*/

    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only edit your own posts");
        }

        postRepository.delete(post);

    }

    /*LIKES*/
    //Toggling likes. if liked, unlike and vice versa

    @Transactional
    public ApiResponse.PostResponse toggleLikes(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (post.getLikes().contains(currentUser)) {
            post.getLikes().remove(currentUser);
        }else {
            post.getLikes().add(currentUser);
        }

        Post saved = postRepository.save(post);
        return mapToPostResponse(saved, currentUser);
    }


    /*HELPER MAPPER METHODS*/
    public ApiResponse.PostResponse mapToPostResponse(Post post, User currentUser) {
        boolean likedByCurrentUser = currentUser != null && post.getLikes().stream().anyMatch(u -> u.getId().equals(currentUser.getId()));
        return ApiResponse.PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .author(AuthService.mapToUserResponse(post.getAuthor()))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public ApiResponse.PagedResponse<ApiResponse.PostResponse> mapToPagedResponse(Page<Post> page, User currentUser) {
        return ApiResponse.PagedResponse.<ApiResponse.PostResponse>builder()
                .content(page.getContent().stream().map(p -> mapToPostResponse(p, currentUser))
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
