package com.surya.customerledger.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public void signup(@RequestBody @Valid SignupRequest signupRequest) {
    authService.register(signupRequest.name, signupRequest.email, signupRequest.password, "ADMIN");
  }

  @PostMapping("/login")
  public AuthService.TokenPair login(@RequestBody @Valid LoginRequest loginRequest) throws NoSuchAlgorithmException {
    return authService.login(loginRequest.email, loginRequest.password);
  }

  @PostMapping("/refresh")
  public AuthService.TokenPair refresh(@RequestBody @Valid RefreshRequest refreshRequest) throws NoSuchAlgorithmException {
    return authService.refresh(refreshRequest.token);
  }

  public static class SignupRequest {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name mustn't be empty")
    private final String name;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format.")
    private final String email;

    @NotNull(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}$",
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
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format.")
    private final String email;

    @NotNull(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
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
