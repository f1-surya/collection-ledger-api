package com.surya.customerledger.db.repo;

import com.surya.customerledger.db.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends CrudRepository<RefreshToken, Integer> {
  Optional<RefreshToken> findByUserIdAndToken(Integer userId, String token);
}
