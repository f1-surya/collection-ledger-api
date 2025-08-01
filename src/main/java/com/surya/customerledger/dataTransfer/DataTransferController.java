package com.surya.customerledger.dataTransfer;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/data-transfer")
public class DataTransferController {
  private final DataTransferService dataTransferService;

  public DataTransferController(DataTransferService dataService) {
    this.dataTransferService = dataService;
  }

  @GetMapping("/payments")
  public ResponseEntity<byte[]> getPaymentsList(@RequestParam("start") Instant start, @RequestParam("end") Instant end) {
    return ResponseEntity
        .ok()
        .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(dataTransferService.exportPaymentRange(start, end));
  }

  @PostMapping("/import")
  public void imoprtFromSheet(@RequestBody byte[] sheet) {
    dataTransferService.importFromSheet(sheet);
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> getData() {
    return ResponseEntity
        .ok()
        .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(dataTransferService.exportToSheet());
  }
}
