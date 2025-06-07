package com.surya.customerledger.db.repo;

import com.surya.customerledger.db.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
  User findByEmail(String email);
}
