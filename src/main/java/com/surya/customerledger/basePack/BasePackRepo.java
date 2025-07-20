package com.surya.customerledger.basePack;

import com.surya.customerledger.company.Company;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface BasePackRepo extends ListCrudRepository<BasePack, Integer> {
  BasePack findByName(String name);

  List<BasePackPartial> findByCompanyOrderByName(Company company);

  Optional<BasePack> findByIdAndCompany(Integer id, Company company);
}
