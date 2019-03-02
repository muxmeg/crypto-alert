package com.cryptoalert.server.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * Alert entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alert {

  @Id
  @Column(insertable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String currencyPair;
  @Column(name = "price_limit")
  String limit;
  @Wither
  boolean completed;

  public Alert(String currencyPair, String limit, boolean completed) {
    this.currencyPair = currencyPair;
    this.limit = limit;
    this.completed = completed;
    this.id = null;
  }
}
