package com.surya.customerledger.connection;

import com.surya.customerledger.area.AreaNameIdOnly;
import com.surya.customerledger.basePack.BasePackPartial;

import java.time.Instant;

public interface ConnectionPartial {
  Integer getId();

  String getName();

  String getBoxNumber();

  String getPhoneNumber();

  AreaNameIdOnly getArea();

  BasePackPartial getBasePack();

  Instant getLastPayment();
}
