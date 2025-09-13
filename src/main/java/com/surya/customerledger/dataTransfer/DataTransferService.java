package com.surya.customerledger.dataTransfer;

import com.surya.customerledger.area.Area;
import com.surya.customerledger.area.AreaRepo;
import com.surya.customerledger.basePack.BasePack;
import com.surya.customerledger.basePack.BasePackRepo;
import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.connection.Connection;
import com.surya.customerledger.connection.ConnectionRepo;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.payment.PaymentRepo;
import jakarta.transaction.Transactional;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DataTransferService {
  private final ConnectionRepo connectionRepo;
  private final CompanyRepo companyRepo;
  private final PaymentRepo paymentRepo;
  private final AreaRepo areaRepo;
  private final BasePackRepo basePackRepo;
  private final Logger logger = LoggerFactory.getLogger(DataTransferService.class);

  public DataTransferService(ConnectionRepo connectionRepo,
                             CompanyRepo companyRepo,
                             PaymentRepo paymentRepo,
                             AreaRepo areaRepo,
                             BasePackRepo basePackRepo) {
    this.connectionRepo = connectionRepo;
    this.companyRepo = companyRepo;
    this.paymentRepo = paymentRepo;
    this.areaRepo = areaRepo;
    this.basePackRepo = basePackRepo;
  }

  public byte[] exportPaymentRange(Instant start, Instant end) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You need to have a company."));
    var payments = paymentRepo.findByCompanyAndIsMigrationAndDateBetween(company, false, start, end);

    if (payments.isEmpty())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no payments for the provided date range");

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

  @Transactional
  public void importFromSheet(byte[] rawSheetData) {
    if (rawSheetData.length == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sheet is empty");

    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You need to have a company."));

    var areas = new HashMap<String, Area>();
    areaRepo.findByCompany(company).forEach((area -> areas.put(area.getName(), area)));

    var basePacks = new HashMap<String, BasePack>();
    basePackRepo.findByCompany(company)
        .forEach((basePack -> basePacks.put(basePack.getName(), basePack)));

    try (var is = new ByteArrayInputStream(rawSheetData); var wb = new ReadableWorkbook(is)) {
      var newConnections = new ArrayList<Connection>();
      var sheet = wb.getFirstSheet();
      var rows = sheet.read();
      if (rows.isEmpty())
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The sheet you've provided is empty.");

      var columns = rows.getFirst().stream()
          .filter(h -> Set.of("NAME", "SMARTCARD", "PACKAGE", "ADDRESS").contains(h.asString().toUpperCase()))
          .collect(Collectors.toMap(Cell::asString, Cell::getColumnIndex));

      if (columns.size() < 4) {
        throw new ResponseStatusException(
            HttpStatus.NOT_ACCEPTABLE,
            "The sheet you've provided is not in the required format. Please make sure it contains the following headers: NAME, SMARTCARD, PACKAGE and ADDRESS."
        );
      }

      int nameCol = columns.get("NAME");
      int boxNumberCol = columns.get("SMARTCARD");
      int packCol = columns.get("PACKAGE");
      int areaCol = columns.get("ADDRESS");

      for (int i = 1; i < rows.size(); i++) {
        var currentRow = rows.get(i);
        if (!currentRow.hasCell(packCol)) break;

        var packName = currentRow.getCell(packCol).asString();
        var currentPack = basePacks.get(packName);
        if (currentPack == null) {
          currentPack = basePackRepo.save(new BasePack(packName, 200, 90, company));
          basePacks.put(currentPack.getName(), currentPack);
        }
        var areaName = currentRow.getCell(areaCol).asString();
        var currentArea = areas.get(areaName);
        if (currentArea == null) {
          currentArea = areaRepo.save(new Area(areaName, company));
          areas.put(currentArea.getName(), currentArea);
        }

        var boxNumberVal = currentRow.getCell(boxNumberCol).getValue();
        String boxNumber = (boxNumberVal instanceof BigDecimal bd)
            ? bd.toPlainString()
            : boxNumberVal.toString();

        newConnections.add(new Connection(
            currentRow.getCell(nameCol).asString(),
            boxNumber,
            company,
            currentArea,
            currentPack
        ));
      }

      if (!newConnections.isEmpty()) {
        connectionRepo.saveAll(newConnections);
      }
    } catch (IOException e) {
      logger.error("Error while importing data", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while reading the sheet");
    }
  }

  public byte[] exportToSheet() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You need to have a company."));
    var connections = connectionRepo.findByCompanyOrderByName(company);

    if (connections.isEmpty())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You don't have any connections");

    try (var os = new ByteArrayOutputStream(); var wb = new Workbook(os, "CollectionLedger", "1.0")) {
      var ws = wb.newWorksheet("Connections");

      var headers = List.of("NAME", "ADDRESS", "PHONE", "SMARTCARD", "PACKAGE");
      for (int i = 0; i < headers.size(); i++) {
        ws.value(0, i, headers.get(i));
        ws.style(0, i).bold();
      }

      for (int i = 0; i < connections.size(); i++) {
        var currentConnection = connections.get(i);
        ws.value(i + 1, 0, currentConnection.getName());
        ws.value(i + 1, 1, currentConnection.getArea().getName());
        ws.value(i + 1, 2, currentConnection.getPhoneNumber());
        ws.value(i + 1, 3, currentConnection.getBoxNumber());
        ws.value(i + 1, 4, currentConnection.getBasePack().getName());
      }
      ws.finish();

      var payments = paymentRepo.findByCompanyOrderByDate(company);
      if (!payments.isEmpty()) {
        var paymentsSheet = wb.newWorksheet("Payments");
        var paymentHeaders = List.of("DATE", "NAME", "SMARTCARD", "CUSTOMER_PRICE", "LCO_PRICE", "MIGRATION", "CURRENT_PACK", "TO_PACK");
        for (int i = 0; i < paymentHeaders.size(); i++) {
          paymentsSheet.value(0, i, paymentHeaders.get(i));
          paymentsSheet.style(0, i).bold();
        }

        for (int i = 0; i < payments.size(); i++) {
          var currentPayment = payments.get(i);
          paymentsSheet.value(i + 1, 0, currentPayment.getDate().atZone(ZoneId.of("Asia/Kolkata")));
          paymentsSheet.value(i + 1, 1, currentPayment.getConnection().getName());
          paymentsSheet.value(i + 1, 2, currentPayment.getConnection().getBoxNumber());
          paymentsSheet.value(i + 1, 3, currentPayment.getCustomerPrice());
          paymentsSheet.value(i + 1, 4, currentPayment.getLcoPrice());
          paymentsSheet.value(i + 1, 5, currentPayment.getMigration() ? "YES" : "NO");
          paymentsSheet.value(i + 1, 6, currentPayment.getCurrentPack().getName());
          var toPack = currentPayment.getTo();
          if (toPack != null) paymentsSheet.value(i + 1, 7, toPack.getName());
        }
        paymentsSheet.finish();
      }

      wb.finish();
      return os.toByteArray();
    } catch (IOException e) {
      logger.error("Error while exporting payments", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while creating the sheet");
    }
  }
}
