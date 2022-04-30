package com.puc.sistemasdevendas.controllers.configurations;

import com.puc.sistemasdevendas.model.helpers.ApplicationProperties;
import io.jsonwebtoken.Jwts;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthorizeFilter extends OncePerRequestFilter {
    @Autowired
    private ApplicationProperties applicationProperties;
    private final Logger logger = Logger.getLogger(AuthorizeFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        try {
            if (this.shouldValidateAuthorization(request)) {
                String token = authorizationHeader.substring("Bearer".length()).trim();

                Jwts.parserBuilder()
                        .setSigningKey(this.applicationProperties.getJwtKey().getBytes("UTF-8"))
                        .build()
                        .parseClaimsJws(token);
            }
        } catch (Exception e) {
            this.logger.warn("Authorization failed for operation: " + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldValidateAuthorization(HttpServletRequest request) {
        final String endpoint = request.getRequestURI().substring(1) + ":" + request.getMethod();
        return !this.applicationProperties
                .getAuthResourcesIgnores().contains(endpoint);
    }
}
