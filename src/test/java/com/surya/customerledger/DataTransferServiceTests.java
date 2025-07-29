package com.surya.customerledger;

import com.surya.customerledger.area.AreaRepo;
import com.surya.customerledger.basePack.BasePackRepo;
import com.surya.customerledger.company.Company;
import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.connection.Connection;
import com.surya.customerledger.connection.ConnectionRepo;
import com.surya.customerledger.dataTransfer.DataTransferService;
import com.surya.customerledger.payment.Payment;
import com.surya.customerledger.payment.PaymentRepo;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataTransferServiceTests {

  @Mock
  private ConnectionRepo connectionRepo;

  @Mock
  private CompanyRepo companyRepo;

  @Mock
  private PaymentRepo paymentRepo;

  @Mock
  private AreaRepo areaRepo;

  @Mock
  private BasePackRepo basePackRepo;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private DataTransferService dataTransferService;

  @Mock
  private Payment payment1;

  @Mock
  private Payment payment2;

  @Mock
  private Connection connection1;

  @Mock
  private Connection connection2;

  @Mock
  private Company company;

  private final Integer userId = 123;
  private final Instant startDate = Instant.parse("2025-07-01T00:00:00Z");
  private final Instant endDate = Instant.now();

  @Test
  void DataTransferService_exportPaymentRange_Success_ReturnsExcelBytes() {
    var payments = Arrays.asList(payment1, payment2);
    try (var securityContextHolder = mockStatic(SecurityContextHolder.class)) {
      setUpSecurityContext(securityContextHolder);
      setUpCompanyMock();
      setUpPayments(payments);

      var result = dataTransferService.exportPaymentRange(startDate, endDate);

      assertNotNull(result);
      assertTrue(result.length > 0);

      verify(companyRepo).findByOwner(userId);
      verify(paymentRepo).findByCompanyAndIsMigrationAndDateBetween(company, false, startDate, endDate);
      verify(payment1).getConnection();
      verify(payment2).getConnection();
      verify(connection1).getBoxNumber();
      verify(connection2).getBoxNumber();

      try (var is = new ByteArrayInputStream(result); var wb = new ReadableWorkbook(is)) {
        var rows = wb.getFirstSheet().read();
        assertFalse(rows.isEmpty());
        assertEquals("832b00100001", rows.getFirst().getCell(0).asString());
        assertEquals("832b00100002", rows.getLast().getCell(0).asString());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Test
  void DataTransferService_exportPaymentRange_Fail_ThrowsResponseStatusException() {
    try (var securityContextHolder = mockStatic(SecurityContextHolder.class)) {
      setUpSecurityContext(securityContextHolder);
      setUpCompanyMock();

      assertThrows(ResponseStatusException.class, () -> dataTransferService.exportPaymentRange(startDate, endDate));
    }
  }

  private void setUpSecurityContext(MockedStatic<SecurityContextHolder> securityContextHolder) {
    securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userId);
  }

  private void setUpCompanyMock() {
    when(companyRepo.findByOwner(userId)).thenReturn(Optional.of(company));
  }

  private void setUpPayments(List<Payment> payments) {
    when(paymentRepo.findByCompanyAndIsMigrationAndDateBetween(company, false, startDate, endDate))
        .thenReturn(payments);
    when(payment1.getConnection()).thenReturn(connection1);
    when(payment2.getConnection()).thenReturn(connection2);
    when(connection1.getBoxNumber()).thenReturn("832b00100001");
    when(connection2.getBoxNumber()).thenReturn("832b00100002");
  }
}
