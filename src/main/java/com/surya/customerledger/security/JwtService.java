package com.surya.customerledger.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

  private final String secretKey;

  private final long accessTokenValidityMs = 15L * 60L * 1000L;
  private final long refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L;

  public JwtService(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  public long getAccessTokenValidityMs() {
    return accessTokenValidityMs;
  }

  public long getRefreshTokenValidityMs() {
    return refreshTokenValidityMs;
  }

  public Integer extractUserId(String token) {
    final var rawToken = token.startsWith("Bearer") ? token.split(" ")[1] : token;
    final var claims = parseAllClaims(rawToken);
    if (claims == null) throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid token.");

    return Integer.parseInt(claims.getSubject());
  }

  public String generateAccessToken(Integer id) {
    return generateToken("access", id, accessTokenValidityMs);
  }

  public String generateRefreshToken(Integer id) {
    return generateToken("refresh", id, refreshTokenValidityMs);
  }

  public boolean validateAccessToken(String token) {
    final var claims = parseAllClaims(token);
    if (claims == null) return false;

    var type = (String) claims.get("type");
    if (type == null) return false;

    return type.equals("access");
  }

  public boolean validateRefreshToken(String token) {
    final var claims = parseAllClaims(token);
    if (claims == null) return false;

    var type = (String) claims.get("type");
    if (type == null) return false;

    return type.equals("refresh");
  }

  private String generateToken(String type, Integer userId, long expiry) {
    final var now = new Date();
    final var expiryDate = new Date(now.getTime() + expiry);
    return Jwts.builder()
        .subject(userId.toString())
        .claim("type", type)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  private Key getSigningKey() {
    var keyBytes = Base64.getDecoder().decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims parseAllClaims(String token) throws ExpiredJwtException {
    try {
      return Jwts.parser()
          .verifyWith((SecretKey) getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (MalformedJwtException e) {
      return null;
    }
  }
}
