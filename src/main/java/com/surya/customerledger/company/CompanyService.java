package com.surya.customerledger.company;

import com.surya.customerledger.db.model.User;
import com.surya.customerledger.db.repo.UserRepo;
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
  private final UserRepo userRepo;
  private final Logger logger = LoggerFactory.getLogger(CompanyService.class);

  public CompanyService(CompanyRepo companyRepo, UserRepo userRepo) {
    this.companyRepo = companyRepo;
    this.userRepo = userRepo;
  }

  @Transactional
  public void createCompany(CompanyDto companyDto) throws ExecutionException, InterruptedException {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var companyByUser = CompletableFuture
        .supplyAsync(() -> companyRepo.existsByOwner(userId))
        .exceptionally(ex -> {
          logger.error("Error checking for existing company", ex);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        });

    var companyByEmail = CompletableFuture
        .supplyAsync(() -> companyRepo.existsByEmail(companyDto.email()))
        .exceptionally(ex -> {
          logger.error("Error checking for email availability", ex);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
        });

    CompletableFuture.allOf(companyByUser, companyByEmail).join();

    if (companyByUser.get() == 1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A company already exists for the current user.");
    }

    if (companyByEmail.get()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A company with provided email already exists");
    }

    var currentUser = userRepo.findById(userId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
    );

    companyRepo.save(new Company(companyDto.name(), companyDto.email(), currentUser));
  }

  public CompanyRepo.NameEmailOnly getCompany() {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return companyRepo.findByOwnerPartial(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public void editCompany(CompanyDto companyDto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The company you're trying edit doesn't exist")
    );
    company.setName(companyDto.name());
    company.setEmail(companyDto.email());
    companyRepo.save(company);
  }
}
