package com.surya.customerledger.payment;

import com.surya.customerledger.basePack.BasePackPartial;

import java.time.Instant;

public class PaymentDto {
  private Integer id;
  private Boolean isMigration;
  private BasePackPartial currentPack;
  private BasePackPartial to;
  private Integer lcoPrice;
  private Integer customerPrice;
  private Instant date;

  public PaymentDto(Payment payment) {
    id = payment.getId();
    isMigration = payment.getMigration();
    lcoPrice = payment.getLcoPrice();
    customerPrice = payment.getCustomerPrice();
    date = payment.getDate();
    currentPack = new BasePackPartial() {
      @Override
      public Integer getId() {
        return payment.getCurrentPack().getId();
      }

      @Override
      public String getName() {
        return payment.getCurrentPack().getName();
      }

      @Override
      public Integer getLcoPrice() {
        return payment.getCurrentPack().getLcoPrice();
      }

      @Override
      public Integer getCustomerPrice() {
        return payment.getCurrentPack().getCustomerPrice();
      }
    };
    if (isMigration) {
      to = new BasePackPartial() {
        @Override
        public Integer getId() {
          return payment.getTo().getId();
        }

        @Override
        public String getName() {
          return payment.getTo().getName();
        }

        @Override
        public Integer getLcoPrice() {
          return payment.getTo().getLcoPrice();
        }

        @Override
        public Integer getCustomerPrice() {
          return payment.getTo().getCustomerPrice();
        }
      };
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Boolean getIsMigration() {
    return isMigration;
  }

  public void setMigration(Boolean migration) {
    isMigration = migration;
  }

  public BasePackPartial getCurrentPack() {
    return currentPack;
  }

  public void setCurrentPack(BasePackPartial currentPack) {
    this.currentPack = currentPack;
  }

  public BasePackPartial getTo() {
    return to;
  }

  public void setTo(BasePackPartial to) {
    this.to = to;
  }

  public Integer getLcoPrice() {
    return lcoPrice;
  }

  public void setLcoPrice(Integer lcoPrice) {
    this.lcoPrice = lcoPrice;
  }

  public Integer getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(Integer customerPrice) {
    this.customerPrice = customerPrice;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }
}
