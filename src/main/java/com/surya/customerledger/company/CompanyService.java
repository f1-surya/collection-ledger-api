package com.surya.customerledger.company;

import com.surya.customerledger.user.User;
import com.surya.customerledger.exceptions.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CompanyService {
  private final CompanyRepo companyRepo;
  private final Logger logger = LoggerFactory.getLogger(CompanyService.class);

  public CompanyService(CompanyRepo companyRepo) {
    this.companyRepo = companyRepo;
  }

  @Transactional
  public void createCompany(CompanyDto companyDto) throws ExecutionException, InterruptedException {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var companyByUser = CompletableFuture
        .supplyAsync(() -> companyRepo.existsByOwner(user))
        .exceptionally(ex -> {
          logger.error("Error checking for existing company", ex);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        });

    var companyByEmail = CompletableFuture
        .supplyAsync(() -> companyRepo.existsByEmail(companyDto.getEmail()))
        .exceptionally(ex -> {
          logger.error("Error checking for email availability", ex);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        });

    var companyByPhone = CompletableFuture
        .supplyAsync(() -> companyRepo.existsByPhone(companyDto.getPhone()))
            .exceptionally(ex -> {
              logger.error("Error checking for email availability", ex);
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
            });

    CompletableFuture.allOf(companyByUser, companyByEmail, companyByPhone).join();

    if (companyByUser.get()) {
      throw new ConflictException("message", "Only 1 company is supported per user at the moment");
    }

    if (companyByEmail.get()) {
      throw new ConflictException("email", "A company with provided email already exists");
    }

    if (companyByPhone.get()) {
      throw new ConflictException("phone", "A company with provide phone number already exists");
    }

    companyRepo.save(new Company(companyDto.getName(), companyDto.getEmail(), companyDto.getPhone(), companyDto.getAddress(), user));
  }

  public Company getCompany() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    company.setOwner(null);
    return company;
  }

  public void editCompany(CompanyDto companyDto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The company you're trying edit doesn't exist")
    );
    company.setName(companyDto.getName());
    company.setEmail(companyDto.getEmail());
    companyRepo.save(company);
  }
}
