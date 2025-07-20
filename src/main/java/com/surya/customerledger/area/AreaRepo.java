package com.surya.customerledger.area;

import com.surya.customerledger.company.Company;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepo extends ListCrudRepository<Area, Integer> {

  Area findByName(String name);

  Area findByCompany(Company company);

  List<AreaNameIdOnly> findByCompanyOrderByName(Company company);

  Optional<Area> findByIdAndCompany(Integer id, Company company);
}
