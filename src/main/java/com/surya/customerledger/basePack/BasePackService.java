package com.surya.customerledger.basePack;

import com.surya.customerledger.company.CompanyRepo;
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

  public BasePackService(BasePackRepo basePackRepo, CompanyRepo companyRepo) {
    this.basePackRepo = basePackRepo;
    this.companyRepo = companyRepo;
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

  public List<BasePackPartial> getAllPacks() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have base packs."));
    return basePackRepo.findByCompanyOrderByName(company);
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
}
