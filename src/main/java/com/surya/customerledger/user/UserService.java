package com.surya.customerledger.user;

import com.surya.customerledger.exceptions.ConflictException;
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

    if (!dto.getEmail().equals(user.getEmail())) {
      if (userRepo.existsByEmail(dto.getEmail())) {
        throw new ConflictException("email", "Email is already taken");
      } else {
        user.setEmail(dto.getEmail());
      }
    }

    userRepo.save(user);
  }
}
