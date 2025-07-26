package com.surya.customerledger.connection;

import com.surya.customerledger.area.AreaNameIdOnly;

import java.time.Instant;

public interface ConnectionWBP {
  Integer getId();

  String getName();

  String getBoxNumber();

  String getPhoneNumber();

  AreaNameIdOnly getArea();


  Instant getLastPayment();
}
