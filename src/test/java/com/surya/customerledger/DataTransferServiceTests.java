package com.surya.customerledger;

import com.surya.customerledger.area.Area;
import com.surya.customerledger.area.AreaRepo;
import com.surya.customerledger.basePack.BasePack;
import com.surya.customerledger.basePack.BasePackRepo;
import com.surya.customerledger.company.Company;
import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.connection.Connection;
import com.surya.customerledger.connection.ConnectionRepo;
import com.surya.customerledger.dataTransfer.DataTransferService;
import com.surya.customerledger.db.model.User;
import com.surya.customerledger.payment.Payment;
import com.surya.customerledger.payment.PaymentRepo;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
  private Area area;

  @Mock
  private BasePack basePack;

  @Mock
  private Connection connection1;

  @Mock
  private Connection connection2;

  @Mock
  private Company company;

  @Mock
  private User user;

  private final Instant startDate = Instant.parse("2025-07-01T00:00:00Z");
  private final Instant endDate = Instant.now();

  @Test
  void DataTransferService_exportPaymentRange_Success_ReturnsExcelBytes() {
    var payments = Arrays.asList(payment1, payment2);
    setUpCompanyMock();
    setUpPayments(payments);
    setupSecurityContext();

    var result = dataTransferService.exportPaymentRange(startDate, endDate);

    assertNotNull(result);
    assertTrue(result.length > 0);

    verify(companyRepo).findByOwner(user);
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

  @Test
  void DataTransferService_exportPaymentRange_Fail_ThrowsResponseStatusException() {
    assertThrows(ResponseStatusException.class, () -> dataTransferService.exportPaymentRange(startDate, endDate));
  }

  @Test
  void DataTransferService_importFromSheet_Success() throws IOException {
    setupSecurityContext();
    setUpCompanyMock();
    setupAreaAndBasePackMocks();

    var resource = new ClassPathResource("test_data.xlsx");
    assertTrue(resource.exists());
    assertTrue(resource.isReadable());

    dataTransferService.importFromSheet(resource.getContentAsByteArray());

    verify(areaRepo).findByCompany(company);
    var areaArgument = ArgumentCaptor.forClass(Area.class);
    verify(areaRepo, times(5)).save(areaArgument.capture());
    assertEquals(5, areaArgument.getAllValues().size());
    var firstArea = areaArgument.getAllValues().getFirst();
    assertEquals("GOVT SCHOOL ROAD, MADURAI - 625018", firstArea.getName());

    verify(basePackRepo).findByCompany(company);
    var basePackArgument = ArgumentCaptor.forClass(BasePack.class);
    verify(basePackRepo, times(2)).save(basePackArgument.capture());
    assertEquals(2, basePackArgument.getAllValues().size());
    var firstBasePack = basePackArgument.getAllValues().getFirst();
    assertEquals("SCV TAMIL PLATINUM", firstBasePack.getName());

    ArgumentCaptor<List<Connection>> connectionsArgument = ArgumentCaptor.forClass(List.class);
    verify(connectionRepo).saveAll(connectionsArgument.capture());
    var newConnections = connectionsArgument.getValue();
    assertEquals(7, newConnections.size());
    assertEquals("Faramir", newConnections.getFirst().getName());
  }

  @Test
  void DataTransferService_importFromSheet_Fail_EmptySheet() {
    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> dataTransferService.importFromSheet(new byte[0]));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
  }

  @Test
  void DataTransferService_importFromSheet_Fail_UnknownFormat() {
    setupSecurityContext();
    setUpCompanyMock();

    var resource = new ClassPathResource("test_data_wrong_format.xlsx");
    assertTrue(resource.exists());
    assertTrue(resource.isReadable());

    ResponseStatusException e = assertThrows(ResponseStatusException.class,
        () -> dataTransferService.importFromSheet(resource.getContentAsByteArray()));
    assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatusCode());
  }

  @Test
  void DataTransferService_exportToSheet_Success() {
    setupSecurityContext();
    setUpCompanyMock();
    setUpPayments(List.of(payment1, payment2));
    var connections = getMockedConnections();

    when(connectionRepo.findByCompanyOrderByName(company)).thenReturn(connections);

    var result = dataTransferService.exportToSheet();

    try (var is = new ByteArrayInputStream(result); var wb = new ReadableWorkbook(is)) {
      var connectionRows = wb.getFirstSheet().read();
      connectionRows.removeFirst();
      assertFalse(connectionRows.isEmpty());
      for (int i = 0; i < connectionRows.size(); i++) {
        var currentRow = connectionRows.get(i);
        assertEquals("Name " + i, currentRow.getCell(0).asString());
        assertEquals(area.getName(), currentRow.getCell(1).asString());
        assertNull(currentRow.getCell(2));
        assertEquals("832b0010000" + i, currentRow.getCell(3).asString());
        assertEquals(basePack.getName(), currentRow.getCell(4).asString());
      }

      var paymentsOpt = wb.getSheet(1);
      assertTrue(paymentsOpt.isPresent());
      var paymentRows = paymentsOpt.get().read();
      paymentRows.removeFirst();
      assertEquals("Batman", paymentRows.getFirst().getCellText(1));
      assertEquals("Tyler Durden", paymentRows.getLast().getCellText(1));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void DataTransferService_exportToSheet_Fail_NoConnections() {
    setupSecurityContext();
    setUpCompanyMock();
    when(connectionRepo.findByCompanyOrderByName(company)).thenReturn(List.of());

    assertThrows(ResponseStatusException.class, () -> dataTransferService.exportToSheet());
  }

  private void setupSecurityContext() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
  }

  private void setUpCompanyMock() {
    when(companyRepo.findByOwner(user)).thenReturn(Optional.of(company));
  }

  private void setupAreaAndBasePackMocks() {
    when(area.getName()).thenReturn("Gondor");
    when(basePack.getName()).thenReturn("Silver pack");

    when(areaRepo.findByCompany(company)).thenReturn(List.of(area));
    when(areaRepo.save(any(Area.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Area.class));
    when(basePackRepo.findByCompany(company)).thenReturn(List.of(basePack));
    when(basePackRepo.save(any(BasePack.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, BasePack.class));
  }

  private List<Connection> getMockedConnections() {
    when(area.getName()).thenReturn("Gondor");
    when(basePack.getName()).thenReturn("Silver pack");

    var connections = new ArrayList<Connection>();

    for (int i = 0; i < 5; i++) {
      var newConnection = mock(Connection.class);
      when(newConnection.getBoxNumber()).thenReturn("832b0010000" + i);
      when(newConnection.getName()).thenReturn("Name " + i);
      when(newConnection.getBasePack()).thenReturn(basePack);
      when(newConnection.getArea()).thenReturn(area);
      connections.add(newConnection);
    }

    return connections;
  }

  private void setUpPayments(List<Payment> payments) {
    lenient().when(paymentRepo.findByCompanyAndIsMigrationAndDateBetween(company, false, startDate, endDate))
        .thenReturn(payments);
    lenient().when(paymentRepo.findByCompanyOrderByDate(company)).thenReturn(payments);

    when(payment1.getConnection()).thenReturn(connection1);
    lenient().when(payment1.getDate()).thenReturn(startDate);
    lenient().when(payment1.getCurrentPack()).thenReturn(basePack);
    when(payment2.getConnection()).thenReturn(connection2);
    lenient().when(payment2.getDate()).thenReturn(startDate);
    lenient().when(payment2.getCurrentPack()).thenReturn(basePack);

    when(connection1.getBoxNumber()).thenReturn("832b00100001");
    lenient().when(connection1.getName()).thenReturn("Batman");

    when(connection2.getBoxNumber()).thenReturn("832b00100002");
    lenient().when(connection2.getName()).thenReturn("Tyler Durden");
  }
}
