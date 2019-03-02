package com.cryptoalert.server.alerts.btc;

import com.cryptoalert.server.alerts.AlertsRepository;
import com.cryptoalert.server.alerts.controllers.AlertsWSController;
import com.cryptoalert.server.alerts.model.Alert;
import com.cryptoalert.server.crypto.bitstamp.BitstampPriceService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BtcAlertsChecker {

  private AlertsRepository alertsRepository;
  private BitstampPriceService priceService;
  private AlertsWSController alertsWSController;

  private static final String PRICE_SERVICE_EXCEPTION_MESSAGE = "Can't get price for currency pair: %s";
  static final String CURRENCY_PAIR = "BTC-USD";
  static final CurrencyPair CURRENCY_PAIR_BITSTAMP = CurrencyPair.BTC_USD;
  private static final int CHECK_INTERVAL = 20000;

  public BtcAlertsChecker(AlertsRepository alertsRepository,
      BitstampPriceService priceService,
      AlertsWSController alertsWSController) {
    this.alertsRepository = alertsRepository;
    this.priceService = priceService;
    this.alertsWSController = alertsWSController;
  }

  @Scheduled(fixedRate = CHECK_INTERVAL)
  void checkAlerts() {
    List<Alert> alerts = alertsRepository.findByCurrencyPairAndCompletedFalse(
        CURRENCY_PAIR);
    try {
      Ticker price = priceService.getPrice(CURRENCY_PAIR_BITSTAMP);
      alerts.forEach(alert -> {
        if (price.getLast().compareTo(new BigDecimal(alert.getLimit())) == 1) {
          Alert completedAlert = alert.withCompleted(true);
          alertsRepository.save(completedAlert);
          alertsWSController.sendAlert(completedAlert, price.getLast().toString());
        }
      });
    } catch (IOException e) {
      log.error(String.format(PRICE_SERVICE_EXCEPTION_MESSAGE, CURRENCY_PAIR), e);
    }
  }
}
