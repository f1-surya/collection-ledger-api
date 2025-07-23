package com.surya.customerledger.payment;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public void create(@RequestParam("connectionId") Integer connectionId) {
    paymentService.create(connectionId);
  }

  @PostMapping("migrate")
  public void migrate(@RequestParam("connectionId") Integer connectionId, @RequestParam("to") Integer to) {
    paymentService.migrate(connectionId, to);
  }

  @GetMapping
  public List<PaymentPartial> getAll(@RequestParam("start") Instant start, @RequestParam("end") Instant end) {
    return paymentService.getAll(start, end);
  }

  @GetMapping("/{connectionId}")
  public List<PaymentPartial> getConnectionPayments(@PathVariable("connectionId") Integer connectionId) {
    return paymentService.getAllForConnection(connectionId);
  }

  @DeleteMapping
  public void delete(@RequestParam("paymentId") Integer paymentId) {
    paymentService.delete(paymentId);
  }
}
