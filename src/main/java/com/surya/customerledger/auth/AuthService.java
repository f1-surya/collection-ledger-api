package com.surya.customerledger.auth;

import com.surya.customerledger.auth.dto.SignupFormData;
import com.surya.customerledger.db.model.RefreshToken;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.db.repo.RefreshTokenRepo;
import com.surya.customerledger.db.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {
  private final JwtService jwtService;
  private final HashEncoder hashEncoder;
  private final UserRepo userRepo;
  private final RefreshTokenRepo refreshTokenRepo;
  private final CustomUserDetailsService customUserDetailsService;
  private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
  private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

  public AuthService(JwtService jwtService, HashEncoder hashEncoder, UserRepo userRepo, RefreshTokenRepo refreshTokenRepo, CustomUserDetailsService customUserDetailsService) {
    this.jwtService = jwtService;
    this.hashEncoder = hashEncoder;
    this.userRepo = userRepo;
    this.refreshTokenRepo = refreshTokenRepo;
    this.customUserDetailsService = customUserDetailsService;
  }

  public void register(String name, String email, String password, String role) {
    var userExists = userRepo.existsByEmail(email);
    if (userExists) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with the same email already exists.");
    }
    userRepo.save(new User(name, email, hashEncoder.encode(password), role));
  }

  public String register(SignupFormData formData, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
    var userExists = userRepo.existsByEmail(formData.getEmail());
    if (userExists) {
      result.rejectValue("email", "errors.email", "A user with the same Email already exists.");
      return "/signup";
    }

    userRepo.save(new User(formData.getName(), formData.getEmail(), hashEncoder.encode(formData.getPassword()), "ADMIN"));
    var token = UsernamePasswordAuthenticationToken.unauthenticated(formData.getEmail(), formData.getPassword());
    var authManager = new DaoAuthenticationProvider(customUserDetailsService);
    authManager.setPasswordEncoder(new BCryptPasswordEncoder());
    var auth = authManager.authenticate(token);
    var context = securityContextHolderStrategy.createEmptyContext();
    context.setAuthentication(auth);
    securityContextHolderStrategy.setContext(context);
    securityContextRepository.saveContext(context, request, response);
    return "redirect:/createCompany";
  }

  public TokenPair login(String email, String password) throws NoSuchAlgorithmException {
    final var currUser = userRepo.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

    if (!hashEncoder.matches(password, currUser.getPassword())) throw new BadCredentialsException("Wrong password.");

    final var newAccessToken = jwtService.generateAccessToken(currUser.getId());
    final var newRefreshToken = jwtService.generateRefreshToken(currUser.getId());

    storeRefreshToken(currUser.getId(), newRefreshToken);

    return new TokenPair(newAccessToken, newRefreshToken);
  }

  @Transactional
  public TokenPair refresh(String accessToken) throws NoSuchAlgorithmException {
    if (!jwtService.validateRefreshToken(accessToken)) throw new ResponseStatusException(
        HttpStatusCode.valueOf(401), "Invalid refresh token."
    );

    final var userId = jwtService.extractUserId(accessToken);
    userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(
        HttpStatusCode.valueOf(401), "Invalid refresh token."
    ));

    final var hashed = hashToken(accessToken);

    final var oldToken = refreshTokenRepo.findByUserIdAndToken(userId, hashed);
    if (oldToken == null) throw new ResponseStatusException(
        HttpStatusCode.valueOf(401),
        "Refresh token not recognized."
    );

    refreshTokenRepo.deleteByUserIdAndToken(userId, hashed);

    final var newRefreshToken = jwtService.generateRefreshToken(userId);
    final var newAccessToken = jwtService.generateAccessToken(userId);

    storeRefreshToken(userId, newAccessToken);
    return new TokenPair(newAccessToken, newRefreshToken);
  }

  private void storeRefreshToken(Integer userId, String token) throws NoSuchAlgorithmException {
    final var hashed = hashToken(token);
    final var expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenValidityMs());

    refreshTokenRepo.save(new RefreshToken(userId, hashed, expiresAt));
  }

  private String hashToken(String token) throws NoSuchAlgorithmException {
    final var digest = MessageDigest.getInstance("SHA-256");
    final var hashedBytes = digest.digest(token.getBytes());
    return Base64.getEncoder().encodeToString(hashedBytes);
  }

}
