package com.surya.customerledger.area;

import com.surya.customerledger.company.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepo extends ListCrudRepository<Area, Integer> {

  Area findByName(String name);

  List<Area> findByCompany(Company company);

  @Query(value = " SELECT new com.surya.customerledger.area.AreaDto(a.id, a.name, COUNT(c.id) AS connections) "
      + " FROM Area a"
      + " LEFT JOIN Connection c ON a.id = c.area.id"
      + " WHERE a.company.id = ?1 "
      + " GROUP BY a.id, a.name"
      + " ORDER BY a.name ")
  List<AreaDto> findAll(Integer company);

  List<AreaDto> findByCompanyOrderByName(Company company);

  Optional<Area> findByIdAndCompany(Integer id, Company company);

}
