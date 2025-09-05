package com.surya.customerledger.auth;

import com.surya.customerledger.db.model.RefreshToken;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.db.repo.RefreshTokenRepo;
import com.surya.customerledger.db.repo.UserRepo;
import com.surya.customerledger.exceptions.ConflictException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  public AuthService(JwtService jwtService, HashEncoder hashEncoder, UserRepo userRepo, RefreshTokenRepo refreshTokenRepo) {
    this.jwtService = jwtService;
    this.hashEncoder = hashEncoder;
    this.userRepo = userRepo;
    this.refreshTokenRepo = refreshTokenRepo;
  }

  public TokenPair register(String name, String email, String password, String role) throws NoSuchAlgorithmException {
    var userExists = userRepo.existsByEmail(email);
    if (userExists) {
      throw new ConflictException("email", "A user with the same email already exists.");
    }
    var newUser = userRepo.save(new User(name, email, hashEncoder.encode(password), role));
    final var newAccessToken = jwtService.generateAccessToken(newUser.getId());
    final var newRefreshToken = jwtService.generateRefreshToken(newUser.getId());

    storeRefreshToken(newUser.getId(), newRefreshToken);

    return new TokenPair(newAccessToken, newRefreshToken);
  }

  public TokenPair login(String email, String password) throws NoSuchAlgorithmException {
    final var currUser = userRepo.findByEmail(email).orElseThrow(() -> new BadCredentialsException("No account is associated with this email."));
    if (!hashEncoder.matches(password, currUser.getPassword())) throw new BadCredentialsException("Wrong password.");

    final var newAccessToken = jwtService.generateAccessToken(currUser.getId());
    final var newRefreshToken = jwtService.generateRefreshToken(currUser.getId());

    storeRefreshToken(currUser.getId(), newRefreshToken);

    return new TokenPair(newAccessToken, newRefreshToken);
  }

  @Transactional
  public TokenPair refresh(String refreshToken) throws NoSuchAlgorithmException {
    if (!jwtService.validateRefreshToken(refreshToken)) throw new ResponseStatusException(
        HttpStatusCode.valueOf(401), "Invalid refresh token."
    );

    final var userId = jwtService.extractUserId(refreshToken);
    userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(
        HttpStatusCode.valueOf(401), "Invalid refresh token."
    ));

    final var hashed = hashToken(refreshToken);

    final var oldToken = refreshTokenRepo.findByUserIdAndToken(userId, hashed)
        .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh token not recognized."));

    refreshTokenRepo.delete(oldToken);

    final var newRefreshToken = jwtService.generateRefreshToken(userId);
    final var newAccessToken = jwtService.generateAccessToken(userId);

    storeRefreshToken(userId, newRefreshToken);
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
