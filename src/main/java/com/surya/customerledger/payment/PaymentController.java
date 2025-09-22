package com.surya.customerledger.payment;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public PaymentDto create(@RequestParam("connectionId") Integer connectionId) {
    return paymentService.create(connectionId);
  }

  @PostMapping("migrate")
  public PaymentDto migrate(@RequestParam("connectionId") Integer connectionId, @RequestParam("to") Integer to) {
    return paymentService.migrate(connectionId, to);
  }

  @GetMapping
  public List<PaymentWC> getAll(@RequestParam("start") Instant start, @RequestParam("end") Instant end) {
    return paymentService.getAll(start, end);
  }

  @GetMapping("/{boxNumber}")
  public List<PaymentPartial> getConnectionPayments(@PathVariable("boxNumber") String boxNumber) {
    return paymentService.getAllForConnection(boxNumber);
  }

  @DeleteMapping
  public void delete(@RequestParam("paymentId") Integer paymentId) {
    paymentService.delete(paymentId);
  }
}
