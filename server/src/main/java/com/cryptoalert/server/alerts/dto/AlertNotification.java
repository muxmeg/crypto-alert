package com.cryptoalert.server.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alert trigger notification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertNotification {
  private String lastPrice;
  private String limit;
  private String currencyPair;
}
