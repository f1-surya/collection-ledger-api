package com.surya.customerledger.payment;

import com.surya.customerledger.basePack.BasePackPartial;
import com.surya.customerledger.connection.ConnectionWBP;

import java.time.Instant;

public interface PaymentWC {
  Integer getId();

  ConnectionWBP getConnection();

  BasePackPartial getCurrentPack();

  Boolean getIsMigration();

  BasePackPartial getTo();

  Integer getLcoPrice();

  Integer getCustomerPrice();

  Instant getDate();
}
