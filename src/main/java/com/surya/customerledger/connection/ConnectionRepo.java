package com.surya.customerledger.connection;

import com.surya.customerledger.area.Area;
import com.surya.customerledger.basePack.BasePack;
import com.surya.customerledger.company.Company;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepo extends ListCrudRepository<Connection, Integer> {
  Optional<Connection> findByIdAndCompany(Integer id, Company company);

  Optional<ConnectionPartial> findConnectionPartialByIdAndCompany(Integer integer, Company company);

  List<ConnectionPartial> findConnectionPartialByCompanyOrderByName(Company company);

  List<Connection> findByCompanyOrderByName(Company company);

  Optional<Connection> findByBoxNumber(String boxNumber);

  Optional<Connection> findByBoxNumberAndCompany(String boxNumber, Company company);

  Optional<ConnectionPartial> findConnectionPartialByBoxNumberAndCompany(String boxNumber, Company company);

  Boolean existsByBasePack(BasePack basePack);

  Boolean existsByArea(Area area);
}
