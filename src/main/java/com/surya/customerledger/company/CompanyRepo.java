package com.surya.customerledger.company;

import com.surya.customerledger.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanyRepo extends CrudRepository<Company, Integer> {

  Optional<Company> findByOwner(User owner);

  boolean existsByEmail(String email);

  boolean existsByOwner(User owner);

  boolean existsByPhone(String phone);

  Company findByEmail(String email);

}
