package com.cryptoalert.server.alerts.controllers;

import com.cryptoalert.server.alerts.AlertsRepository;
import com.cryptoalert.server.alerts.model.Alert;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alert")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AlertsRestController {

  private final AlertsRepository alertsRepository;

  @PutMapping
  public void putAlert(@RequestParam("pair") String currencyPair,
      @RequestParam String limit) {
    alertsRepository.save(new Alert(currencyPair, limit, false));
  }

  @DeleteMapping
  public void deleteAlert(@RequestParam("pair") String currencyPair,
      @RequestParam String limit) {
    List<Alert> alerts = alertsRepository.findByCurrencyPairAndLimit(currencyPair, limit);
    alerts.forEach(alertsRepository::delete);
  }

}
