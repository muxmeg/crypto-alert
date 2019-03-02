package com.cryptoalert.server.alerts.controllers;

import com.cryptoalert.server.alerts.dto.AlertNotification;
import com.cryptoalert.server.alerts.model.Alert;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class AlertsWSController {
  public static final String ALERTS_TOPIC = "/topic/alerts";

  private final SimpMessagingTemplate template;

  public AlertsWSController(SimpMessagingTemplate template) {
    this.template = template;
  }

  public void sendAlert(Alert alert, String lastPrice) {
    template.convertAndSend(ALERTS_TOPIC, new AlertNotification(lastPrice, alert.getLimit(),
        alert.getCurrencyPair()));
  }
}
