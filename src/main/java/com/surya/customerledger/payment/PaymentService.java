package com.surya.customerledger.payment;

import com.surya.customerledger.basePack.BasePackRepo;
import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.connection.ConnectionRepo;
import com.surya.customerledger.db.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PaymentService {

  private final PaymentRepo paymentRepo;
  private final CompanyRepo companyRepo;
  private final ConnectionRepo connectionRepo;
  private final BasePackRepo basePackRepo;

  public PaymentService(PaymentRepo paymentRepo, CompanyRepo companyRepo, ConnectionRepo connectionRepo, BasePackRepo basePackRepo) {
    this.paymentRepo = paymentRepo;
    this.companyRepo = companyRepo;
    this.connectionRepo = connectionRepo;
    this.basePackRepo = basePackRepo;
  }

  @Transactional
  public void create(Integer connectionId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need a company first to create payments."));

    var connection = connectionRepo.findByIdAndCompany(connectionId, company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The connection you're trying to mark as paid doesn't exist"));

    var now = LocalDateTime.now();
    var paymentCurrentMonth = paymentRepo.findFirstByCompanyAndConnectionAndDateBetween(
        company,
        connection,
        now.withDayOfMonth(1).atZone(ZoneId.systemDefault()).toInstant(),
        Instant.now()
    );

    if (paymentCurrentMonth.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A payment for this connection already exists.");
    }

    var newPayment = new Payment(connection,
        connection.getBasePack(),
        null,
        connection.getBasePack().getCustomerPrice(),
        connection.getBasePack().getLcoPrice(),
        company);
    paymentRepo.save(newPayment);
    connection.setLastPayment(Instant.now());
    connectionRepo.save(connection);
  }

  @Transactional
  public void migrate(Integer connectionId, Integer toPackId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Since you don't have any company so you don't have any connections to make migrations."));

    var connection = connectionRepo.findByIdAndCompany(connectionId, company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The connection you're trying to migrate doesn't exist"));
    var toPack = basePackRepo.findByIdAndCompany(toPackId, company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The pack you've selected doesn't exist"));

    var now = LocalDateTime.now();
    Payment currPayment;
    var paymentCurrentMonth = paymentRepo.findFirstByCompanyAndConnectionAndDateBetween(
        company,
        connection,
        now.withDayOfMonth(1).atZone(ZoneId.systemDefault()).toInstant(),
        Instant.now()
    );
    if (paymentCurrentMonth.isPresent()) {
      currPayment = paymentCurrentMonth.get();
      currPayment.setTo(toPack);
      currPayment.setDate(Instant.now());
      currPayment.getConnection().setBasePack(toPack);
      currPayment.getConnection().setLastPayment(Instant.now());
    } else {
      connection.setLastPayment(Instant.now());
      connection.setBasePack(toPack);
      currPayment = new Payment(connection,
          connection.getBasePack(),
          toPack,
          toPack.getCustomerPrice(),
          toPack.getLcoPrice(),
          company);
    }

    currPayment.setMigration(true);
    paymentRepo.save(currPayment);
  }

  public List<PaymentWC> getAll(Instant start, Instant end) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have payments."));

    return paymentRepo.findPaymentWCByCompanyAndDateBetween(company, start, end);
  }

  public List<PaymentPartial> getAllForConnection(Integer connectionId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have connection payments."));

    var connection = connectionRepo.findByIdAndCompany(connectionId, company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The connection you're trying to update doesn't exist"));
    return paymentRepo.findPaymentPartialByCompanyAndConnection(company, connection);
  }

  @Transactional
  public void delete(Integer paymentId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You don't have any company yet"));
    var currentPayment = paymentRepo.findByIdAndCompany(paymentId, company)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The payment you're trying to delete doesn't exist"));
    var currentConnection = connectionRepo.findById(currentPayment.getConnection().getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't get connection for the current payment."));
    paymentRepo.delete(currentPayment);

    var lastPayment = paymentRepo.findFirstByCompanyAndConnectionOrderByDateDesc(company, currentConnection);
    if (lastPayment.isPresent()) {
      var payment = lastPayment.get();
      currentConnection.setLastPayment(payment.getDate());
      if (payment.getTo() != null) {
        currentConnection.setBasePack(payment.getTo());
      } else {
        currentConnection.setBasePack(payment.getCurrentPack());
      }
    } else {
      currentConnection.setLastPayment(null);
    }
    connectionRepo.save(currentConnection);
  }
}
