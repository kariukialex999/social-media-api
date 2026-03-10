package org.kariioke.socialmediaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        //1.Step 1:Check the "Authorization" header
        final String authHeader = request.getHeader("Authentication");

        if(authHeader == null && !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Step 2: Extract the token(everything after "Bearer ")
        final String jwt = authHeader.substring(7);

        //Step 3: Extract username from token
        final String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            }
        }
        filterChain.doFilter(request, response);

    }



    /*
     * JwtAuthFilter intercepts every HTTP request
     * This is done by using a security filter chain where each request passes once
     * OncePerRequestFilter guarantees the filter runs only once per http request
     *
     * flow of request:
     * 1.Extract "Authorization" header
     * 2.If it starts with "Bearer ", extract the token
     * 3.parse username from the token extracted
     * 4.Load user from DB and validate token
     * 5.Set authentication in SecurityContext
     * 6.Pass request to the next filter / controller
     * */

    //1.Step 1:Check and extract the "Bearer" token from the "Authorization" header


}
