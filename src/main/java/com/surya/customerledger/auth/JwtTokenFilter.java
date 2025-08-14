package com.surya.customerledger.auth;

import com.surya.customerledger.db.repo.UserRepo;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepo userRepo;

  public JwtTokenFilter(JwtService jwtService, UserRepo userRepo) {
    this.jwtService = jwtService;
    this.userRepo = userRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
    if (req.getRequestURI().contains("/auth/refresh")) {
      filterChain.doFilter(req, res);
      return;
    }
    final var authHeader = req.getHeader("Authorization");
    try {
      if (authHeader != null) {
        var token = authHeader.split(" ")[1];
        if (jwtService.validateAccessToken(token)) {
          var userId = jwtService.extractUserId(token);
          var user = userRepo.findById(userId).orElseThrow(() ->
              new ResponseStatusException(HttpStatus.UNAUTHORIZED, "We can't verify you're session."));
          var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (ExpiredJwtException e) {
      res.setStatus(419);
      res.setContentType("application/json");
      res.getWriter().write("{\"error\": \"Access token expired!!!\"}");
      return;
    }
    filterChain.doFilter(req, res);
  }
}
