package com.surya.customerledger.controller;

import com.surya.customerledger.db.model.User;
import com.surya.customerledger.db.repo.UserRepo;
import com.surya.customerledger.security.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;
  private final UserRepo userRepo;

  public AuthController(AuthService authService, UserRepo userRepo) {
    this.authService = authService;
    this.userRepo = userRepo;
  }

  @PostMapping("/signup")
  public void signup(@RequestBody SignupRequest signupRequest) {
    authService.register(signupRequest.name, signupRequest.email, signupRequest.password, "ADMIN");
  }

  @PostMapping("/login")
  public AuthService.TokenPair login(@RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException {
    return authService.login(loginRequest.email, loginRequest.password);
  }

  @PostMapping("/refresh")
  public AuthService.TokenPair refresh(@RequestBody RefreshRequest refreshRequest) throws NoSuchAlgorithmException {
    return authService.refresh(refreshRequest.token);
  }

  public static class SignupRequest {
    @NotBlank
    private final String name;
    @Email(message = "Invalid email format.")
    private final String email;
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\\$",
        message = "Password must be at least 8 characters long and contain at least one digit, uppercase and lowercase letters."
    )
    private final String password;

    SignupRequest(String name, String email, String password) {
      this.name = name;
      this.email = email;
      this.password = password;
    }
  }

  public static class LoginRequest {
    @Email(message = "Invalid email format.")
    private final String email;
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\\$",
        message = "Password must be at least 8 characters long and contain at least one digit, uppercase and lowercase letters."
    )
    private final String password;

    LoginRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }
  }

  public class RefreshRequest {
    private final String token;

    public RefreshRequest(String token) {
      this.token = token;
    }
  }
}
