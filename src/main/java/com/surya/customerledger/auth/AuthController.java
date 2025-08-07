package com.surya.customerledger.auth;

import com.surya.customerledger.auth.dto.LoginRequestDto;
import com.surya.customerledger.auth.dto.RefreshRequestDto;
import com.surya.customerledger.auth.dto.SignupRequestDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
public class
AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public void signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
    authService.register(signupRequestDto.name(), signupRequestDto.email(), signupRequestDto.password(), "ADMIN");
  }

  @PostMapping("/login")
  public TokenPair login(@RequestBody @Valid LoginRequestDto loginRequestDto) throws NoSuchAlgorithmException {
    return authService.login(loginRequestDto.email(), loginRequestDto.password());
  }

  @PostMapping("/refresh")
  public TokenPair refresh(@RequestBody @Valid RefreshRequestDto refreshRequest) throws NoSuchAlgorithmException {
    return authService.refresh(refreshRequest.token());
  }

}
