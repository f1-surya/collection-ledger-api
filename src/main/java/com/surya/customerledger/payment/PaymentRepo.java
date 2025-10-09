package com.surya.customerledger.payment;

import com.surya.customerledger.company.Company;
import com.surya.customerledger.connection.Connection;
import org.springframework.data.repository.ListCrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends ListCrudRepository<Payment, Integer> {
  Optional<Payment> findByIdAndCompany(Integer id, Company company);

  Optional<Payment> findFirstByCompanyAndConnectionAndDateBetween(Company company, Connection connection, Instant start, Instant end);

  List<PaymentPartial> findPaymentPartialByCompanyAndDateBetween(Company company, Instant start, Instant end);

  List<PaymentWC> findPaymentWCByCompanyAndDateBetween(Company company, Instant start, Instant end);

  List<Payment> findByCompanyAndIsMigrationAndDateBetween(Company company, Boolean isMigration, Instant start, Instant end);

  List<PaymentPartial> findPaymentPartialByCompanyAndConnectionOrderByDateDesc(Company company, Connection connection);

  Optional<Payment> findFirstByCompanyAndConnectionOrderByDateDesc(Company company, Connection connection);

  List<Payment> findByCompanyOrderByDate(Company company);
}
