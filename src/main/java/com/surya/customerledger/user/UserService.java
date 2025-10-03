package com.surya.customerledger.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepo userRepo;

  public UserService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  public UserDto getUser() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return UserDto.fromUser(user);
  }

  public void updateUser(UserDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    userRepo.save(user);
  }
}
