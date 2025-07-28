package com.surya.customerledger.sheetData;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/sheet")
public class SheetController {
  private final ExportDataService exportDataService;

  public SheetController(ExportDataService exportDataService) {
    this.exportDataService = exportDataService;
  }

  @GetMapping("/payments")
  public ResponseEntity<byte[]> getPaymentsList(@RequestParam("start") Instant start, @RequestParam("end") Instant end) {
    return ResponseEntity
        .ok()
        .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(exportDataService.exportPaymentRange(start, end));
  }
}
