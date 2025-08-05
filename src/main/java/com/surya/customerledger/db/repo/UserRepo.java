package com.surya.customerledger.db.repo;

import com.surya.customerledger.db.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepo extends ListCrudRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
