package com.surya.customerledger.connection;

import com.surya.customerledger.area.AreaRepo;
import com.surya.customerledger.basePack.BasePackRepo;
import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.exceptions.ConflictException;
import com.surya.customerledger.exceptions.InvalidReferenceException;
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
public class ConnectionService {
  private final ConnectionRepo connectionRepo;
  private final CompanyRepo companyRepo;
  private final AreaRepo areaRepo;
  private final BasePackRepo basePackRepo;
  private final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

  public ConnectionService(ConnectionRepo connectionRepo,
                           CompanyRepo companyRepo,
                           AreaRepo areaRepo,
                           BasePackRepo basePackRepo) {
    this.connectionRepo = connectionRepo;
    this.companyRepo = companyRepo;
    this.areaRepo = areaRepo;
    this.basePackRepo = basePackRepo;
  }

  public void create(CreateConnectionDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need a company to create connections."));
    var areaFuture = CompletableFuture.supplyAsync(() -> areaRepo.findByIdAndCompany(dto.area(), company))
        .exceptionally(throwable -> {
          logger.error("Area fetch errored", throwable);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        });
    var basePackFuture = CompletableFuture.supplyAsync(() -> basePackRepo.findByIdAndCompany(dto.basePack(), company))
        .exceptionally(throwable -> {
          logger.error("Base pack fetch errored", throwable);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        });
    var connectionFuture = CompletableFuture.supplyAsync(() -> connectionRepo.findByBoxNumber(dto.boxNumber()));

    CompletableFuture.allOf(areaFuture, basePackFuture, connectionFuture).join();

    try {
      connectionFuture.get().ifPresent(connection -> {
        throw new ConflictException("boxNumber", "A connection with the provided box number already exists.");
      });
      var area = areaFuture.get().orElseThrow(() ->
          new InvalidReferenceException("area", "The area you've selected doesn't exist"));
      var basePack = basePackFuture.get().orElseThrow(() ->
          new InvalidReferenceException("basePack", "The base pack you've provided doesn't exist"));

      var newConnection = new Connection(dto.name(), dto.boxNumber(), dto.phoneNumber(), company, area, basePack);
      connectionRepo.save(newConnection);
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Error while updating pack.", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }
  }

  public void update(UpdateConnectionDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need a company to have connections."));
    var connection = connectionRepo.findByIdAndCompany(dto.id(), company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The connection you're trying to update doesn't exist"));

    connection.setName(dto.name());
    connection.setPhoneNumber(dto.phoneNumber());
    if (!dto.area().equals(connection.getArea().getId())) {
      var newArea = areaRepo.findById(dto.area()).orElseThrow(() ->
          new InvalidReferenceException("area", "The new area you've selected for this connection doesn't exist"));
      connection.setArea(newArea);
    }
    connectionRepo.save(connection);
  }

  public ConnectionPartial getConnectionById(Integer connectionId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need a company to have connections."));
    return connectionRepo.findConnectionPartialByIdAndCompany(connectionId, company)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The connection you're trying to find doesn't exist"));
  }

  public List<ConnectionPartial> getAllConnections() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need a company to have connections."));
    return connectionRepo.findConnectionPartialByCompanyOrderByName(company);
  }
}
