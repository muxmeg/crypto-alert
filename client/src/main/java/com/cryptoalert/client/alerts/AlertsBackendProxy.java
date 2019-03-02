package com.cryptoalert.client.alerts;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Rest service for backend application.
 */
@FeignClient(name = "alerts-service", url="http://localhost:8081")
public interface AlertsBackendProxy {

  /**
   * Save new alert.
   * @param currencyPair alert currency pair.
   * @param limit alert will be rased if price is bigger than alert.
   */
  @PutMapping("/alert")
  void saveAlert (@RequestParam("pair") String currencyPair, @RequestParam("limit") String limit);

  /**
   * Delete alert.
   * @param currencyPair alert currency pair.
   * @param limit alert price limit.
   */
  @DeleteMapping("/alert")
  void deleteAlert (@RequestParam("pair") String currencyPair, @RequestParam("limit") String limit);

}

