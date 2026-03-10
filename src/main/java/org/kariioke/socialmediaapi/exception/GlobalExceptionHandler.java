package org.kariioke.socialmediaapi.exception;

import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //centralised error handling
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        //collect all field errors - there may be a couple
        List<ApiResponse.ErrorResponse.ValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ApiResponse.ErrorResponse.ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.badRequest().body(
                ApiResponse.ErrorResponse.builder()
                        .status(400)
                        .error("Validation Failed")
                        .message("One or more fields are invalid")
                        .fieldErrors(fieldErrors)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /// 404 -- resource not found(post, user comment doesn't exist)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleNotFoundException(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError(404, "Not Found", ex.getMessage())
        );
    }
    /// 409 -- conflict(duplicate username, already following, e.t.c
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleConflictException (
            ConflictException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(409, "Conflict", ex.getMessage())
        );
    }
    /// 403 -- forbidden(user doesn't own the resource they are trying to modify)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleForbiddenException (
            ForbiddenException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildError(403, "Forbidden", ex.getMessage())
        );
    }
    /// 401 -- bad credentials during login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(401, "Unauthorized", ex.getMessage())
        );
    }
    /// custom 400 bad request from service layer
    public ResponseEntity<ApiResponse.ErrorResponse> handleBadRequest(
            BadRequestException ex
    ) {
        return ResponseEntity.badRequest().body(
                buildError(400, "Bad Request", ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse.ErrorResponse> handleGenericException(Exception ex) {
        // In production, log ex here with a logger
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(500, "Internal Server Error",
                        "An unexpected error occurred. Please try again later.")
        );
    }

    /*helper builder method*/
    private ApiResponse.ErrorResponse buildError(int status, String error, String message) {
        return ApiResponse.ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
