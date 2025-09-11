package com.surya.customerledger.basePack;

import com.surya.customerledger.company.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface BasePackRepo extends ListCrudRepository<BasePack, Integer> {
  List<BasePackPartial> findByCompanyOrderByName(Company company);

  List<BasePack> findByCompany(Company company);

  @Query(value = "SELECT new com.surya.customerledger.basePack.BasePackConCount(b.id, b.name, b.lcoPrice, b.customerPrice, COUNT(c.id) AS connections)"
      + " FROM BasePack b "
      + " LEFT JOIN Connection c ON b.id = c.basePack.id "
      + " WHERE b.company.id = ?1 "
      + " GROUP BY b.id, b.name, b.lcoPrice, b.customerPrice "
      + " ORDER BY b.name ")
  List<BasePackConCount> findAll(Integer company);

  Optional<BasePack> findByIdAndCompany(Integer id, Company company);
}
