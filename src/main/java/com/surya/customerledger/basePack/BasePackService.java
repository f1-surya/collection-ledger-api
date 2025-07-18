package com.surya.customerledger.basePack;

import com.surya.customerledger.company.CompanyRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BasePackService {
  private final BasePackRepo basePackRepo;
  private final CompanyRepo companyRepo;
  private final Logger logger = LoggerFactory.getLogger(BasePackService.class);

  public BasePackService(BasePackRepo basePackRepo, CompanyRepo companyRepo) {
    this.basePackRepo = basePackRepo;
    this.companyRepo = companyRepo;
  }

  public void createBasePack(BasePackDto basePackDto) {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(userId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to create areas."));
    var newBasePack = new BasePack();
    newBasePack.setCompany(company);
    newBasePack.setName(basePackDto.name());
    newBasePack.setLcoPrice(basePackDto.lcoPrice());
    newBasePack.setCustomerPrice(basePackDto.customerPrice());
    basePackRepo.save(newBasePack);
  }

  public List<BasePackPartial> getAllPacks() {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(userId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to create areas."));
    return basePackRepo.findByCompanyOrderByName(company);
  }

  public void updatePack(UpdateBasePackDto dto) {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var companyFuture = CompletableFuture.supplyAsync(() -> companyRepo.findByOwner(userId))
        .exceptionally(throwable -> {
          logger.error("Error while getting company at BasePackService", throwable);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error.");
        });
    var packFuture = CompletableFuture.supplyAsync(() -> basePackRepo.findById(dto.id()))
        .exceptionally((throwable -> {
          logger.error("Error while getting basePack at BasePackService", throwable);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error.");
        }));
    CompletableFuture.allOf(companyFuture, packFuture).join();

    try {
      var company = companyFuture.get().orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You don't have any companies."));
      var pack = packFuture.get().orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The pack you're trying update doesn't exist"));
      if (!company.getId().equals(pack.getCompany().getId())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The pack you're trying update doesn't belong to you.");
      }
      pack.setCustomerPrice(dto.customerPrice());
      pack.setLcoPrice(dto.lcoPrice());
      pack.setName(dto.name());
      basePackRepo.save(pack);
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Error while updating pack.", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }
  }
}
