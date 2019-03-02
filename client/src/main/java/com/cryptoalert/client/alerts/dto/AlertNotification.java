package com.cryptoalert.client.alerts.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification received when the alert triggers.
 */
@Data
@NoArgsConstructor
public class AlertNotification {
  private String lastPrice;
  private String limit;
  private String currencyPair;
}
