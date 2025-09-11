package com.surya.customerledger.basePack;

import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.connection.ConnectionRepo;
import com.surya.customerledger.db.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BasePackService {
  private final BasePackRepo basePackRepo;
  private final CompanyRepo companyRepo;
  private final ConnectionRepo connectionRepo;

  public BasePackService(BasePackRepo basePackRepo, CompanyRepo companyRepo, ConnectionRepo connectionRepo) {
    this.basePackRepo = basePackRepo;
    this.companyRepo = companyRepo;
    this.connectionRepo = connectionRepo;
  }

  public void createBasePack(BasePackDto basePackDto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to create base packs."));
    var newBasePack = new BasePack();
    newBasePack.setCompany(company);
    newBasePack.setName(basePackDto.name());
    newBasePack.setLcoPrice(basePackDto.lcoPrice());
    newBasePack.setCustomerPrice(basePackDto.customerPrice());
    basePackRepo.save(newBasePack);
  }

  public List<BasePackConCount> getAllPacks() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have base packs."));
    return basePackRepo.findAll(company.getId());
  }

  public void updatePack(UpdateBasePackDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You don't have any company."));
    var pack = basePackRepo.findByIdAndCompany(dto.id(), company).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The pack you're trying update doesn't exist"));

    pack.setCustomerPrice(dto.customerPrice());
    pack.setLcoPrice(dto.lcoPrice());
    pack.setName(dto.name());
    basePackRepo.save(pack);
  }

  public void deletePack(Integer id) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You don't have any company."));
    var pack = basePackRepo.findByIdAndCompany(id, company).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The pack you're trying delete doesn't exist"));

    var connectionsExists = connectionRepo.existsByBasePack(pack);
    if (connectionsExists) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "This pack is being used by some connections so it cannot be deleted.");
    }

    basePackRepo.delete(pack);
  }
}
