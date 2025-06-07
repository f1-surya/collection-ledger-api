package com.surya.customerledger.db.repo;

import com.surya.customerledger.db.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepo extends CrudRepository<RefreshToken, Integer> {
  RefreshToken findByUserIdAndToken(Integer userId, String token);
  RefreshToken deleteByUserIdAndToken(Integer userId, String token);
}
