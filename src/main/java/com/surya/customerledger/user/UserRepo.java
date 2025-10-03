package com.surya.customerledger.user;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepo extends ListCrudRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
