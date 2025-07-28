package com.surya.customerledger.sheetData;

import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.payment.PaymentRepo;
import org.dhatim.fastexcel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.Instant;

@Service
public class ExportDataService {
  private final CompanyRepo companyRepo;
  private final PaymentRepo paymentRepo;
  private final Logger logger = LoggerFactory.getLogger(ExportDataService.class);

  public ExportDataService(CompanyRepo companyRepo, PaymentRepo paymentRepo) {
    this.companyRepo = companyRepo;
    this.paymentRepo = paymentRepo;
  }

  public byte[] exportPaymentRange(Instant start, Instant end) {
    var userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You need to have a company."));
    var payments = paymentRepo.findByCompanyAndIsMigrationAndDateBetween(company, false, start, end);
    System.out.println(payments.size());

    try (var os = new ByteArrayOutputStream(); var wb = new Workbook(os, "CollectionLedger", "1.0")) {
      var ws = wb.newWorksheet("Sheet 1");
      for (int i = 0; i < payments.size(); i++) {
        var payment = payments.get(i);
        ws.value(i, 0, payment.getConnection().getBoxNumber());
      }
      wb.finish();
      return os.toByteArray();
    } catch (Exception e) {
      logger.error("Error while exporting payments", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while creating the sheet");
    }
  }

}
