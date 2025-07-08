package com.surya.customerledger.db.repo;

import com.surya.customerledger.db.model.User;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepo extends ListCrudRepository<User, Integer> {
  User findByEmail(String email);
}
