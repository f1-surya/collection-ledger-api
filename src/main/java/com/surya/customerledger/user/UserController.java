package com.surya.customerledger.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public UserDto getUser() {
    return userService.getUser();
  }

  @PutMapping
  public void updateUser(@RequestBody @Valid UserDto dto) {
    userService.updateUser(dto);
  }
}
