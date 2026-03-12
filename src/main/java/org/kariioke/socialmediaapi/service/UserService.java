package org.kariioke.socialmediaapi.service;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.entity.User;
import org.kariioke.socialmediaapi.exception.ConflictException;
import org.kariioke.socialmediaapi.exception.ResourceNotFoundException;
import org.kariioke.socialmediaapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

       /*PROFILE*/

    public ApiResponse.UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return AuthService.mapToUserResponse(user);
    }

    public ApiResponse.UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return AuthService.mapToUserResponse(user);
    }

    /// search users by username or bio keyword
    public List<ApiResponse.UserResponse> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(AuthService::mapToUserResponse)
                .toList();
    }


    /*FOLLOWING SYSTEM*/

    @Transactional
    public ApiResponse.MessageResponse followUser(Long targetUserId, User currentUser) {
        if (targetUserId.equals(currentUser.getId())) {
            throw new ConflictException("You cannot follow yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        if (targetUser.getFollowers().contains(currentUser)) {
            throw new ConflictException("You are already following this user");
        }

        targetUser.getFollowers().add(currentUser);
        userRepository.save(targetUser);

        return ApiResponse.MessageResponse.builder()
                .message("Now following @" + targetUser.getUsername())
                .build();
    }

    @Transactional
    public ApiResponse.MessageResponse unfollowUser(Long targetUserId, User currentUser) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        if (targetUser.getFollowers().contains(currentUser)) {
            throw new ConflictException("You are not following this user");
        }

        targetUser.getFollowers().remove(currentUser);
        userRepository.save(targetUser);

        return ApiResponse.MessageResponse.builder()
                .message("Unfollowed @" + targetUser)
                .build();
    }

    /*GET ALL FOLLOWERS FOR A USER*/

    public List<ApiResponse.UserResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getFollowers().stream()
                .map(AuthService::mapToUserResponse)
                .toList();
    }

    /*GET ALL USERS THAT A FOLLOWER IS FOLLOWING*/
    public List<ApiResponse.UserResponse> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getFollowing().stream()
                .map(AuthService::mapToUserResponse)
                .toList();
    }
}


























