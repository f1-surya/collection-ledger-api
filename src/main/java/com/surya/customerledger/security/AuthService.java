package com.surya.customerledger.security;

import com.surya.customerledger.db.model.RefreshToken;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.db.repo.RefreshTokenRepo;
import com.surya.customerledger.db.repo.UserRepo;
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

  public User register(String name, String email, String password, String role) {
    return userRepo.save(new User(name, email, hashEncoder.encode(password), role));
  }

  public TokenPair login(String email, String password) throws NoSuchAlgorithmException {
    final var currUser = userRepo.findByEmail(email);

    if (currUser == null) throw new BadCredentialsException("Invalid credentials.");

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
    final var user = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(
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

  public class TokenPair {
    private final String accessToken;
    private final String refreshToken;

    TokenPair(String accessToken, String refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }
  }
}
