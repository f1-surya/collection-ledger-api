package com.surya.customerledger.area;

import com.surya.customerledger.company.Company;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AreaRepo extends ListCrudRepository<Area, Integer> {

  Area findByName(String name);

  Area findByCompany(Company company);

  List<AreaNameIdOnly> findByCompanyOrderByName(Company company);
}
