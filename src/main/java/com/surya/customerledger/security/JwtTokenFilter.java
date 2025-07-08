package com.surya.customerledger.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtTokenFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
    final var authHeader = req.getHeader("Authorization");
    try {
      if (authHeader != null) {
        var token = authHeader.split(" ")[1];
        if (jwtService.validateAccessToken(token)) {
          var userId = jwtService.extractUserId(token);
          var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (ExpiredJwtException e) {
      res.setStatus(419);
      res.setContentType("application/json");
      res.getWriter().write("{\"error\": \"Access token expired\"}");
      return;
    }
    filterChain.doFilter(req, res);
  }
}
