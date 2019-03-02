package com.cryptoalert.server.alerts.btc;

import static com.cryptoalert.server.alerts.btc.BtcAlertsChecker.CURRENCY_PAIR;
import static com.cryptoalert.server.alerts.btc.BtcAlertsChecker.CURRENCY_PAIR_BITSTAMP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cryptoalert.server.alerts.AlertsRepository;
import com.cryptoalert.server.alerts.controllers.AlertsWSController;
import com.cryptoalert.server.alerts.model.Alert;
import com.cryptoalert.server.crypto.bitstamp.BitstampPriceService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class BtcAlertsCheckerTest {

  @Mock
  private AlertsRepository alertsRepository;
  @Mock
  private BitstampPriceService priceService;
  @Mock
  private AlertsWSController alertsWSController;

  @InjectMocks
  private BtcAlertsChecker alertsChecker;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void checkAlerts() throws IOException {
    when(alertsRepository.findByCurrencyPairAndCompletedFalse(CURRENCY_PAIR))
    .thenReturn(Arrays.asList(
        new Alert(CURRENCY_PAIR, "100", false),
        new Alert(CURRENCY_PAIR, "99", false),
        new Alert(CURRENCY_PAIR, "101", false)));
    when(priceService.getPrice(CURRENCY_PAIR_BITSTAMP)).thenReturn(new Ticker.Builder()
        .last(new BigDecimal("100")).build());

    alertsChecker.checkAlerts();

    verify(alertsRepository, times(1))
        .save(new Alert(CURRENCY_PAIR, "99", true));
    verify(alertsRepository, times(1)).save(any(Alert.class));
    verify(alertsWSController, times(1))
        .sendAlert(new Alert(CURRENCY_PAIR, "99", true), "100");
    verify(alertsWSController, times(1)).sendAlert(any(Alert.class),
        anyString());
  }

  @Test
  public void checkAlertsWithPriceException() throws IOException {
    when(alertsRepository.findByCurrencyPairAndCompletedFalse(CURRENCY_PAIR))
        .thenReturn(Arrays.asList(
            new Alert(CURRENCY_PAIR, "100", false),
            new Alert(CURRENCY_PAIR, "99", false),
            new Alert(CURRENCY_PAIR, "101", false)));
    when(priceService.getPrice(CURRENCY_PAIR_BITSTAMP)).thenThrow(new IOException("Unexpected"));

    alertsChecker.checkAlerts();

    verify(alertsRepository, times(0)).save(any(Alert.class));
    verify(alertsWSController, times(0)).sendAlert(any(Alert.class),
        anyString());
  }
}