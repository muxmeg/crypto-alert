package com.cryptoalert.server.crypto.bitstamp;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ExchangeFactory.class)
public class BitstampPriceServiceTest {

  private BitstampPriceService bitstampPriceService;
  private MarketDataService marketDataService;

  @Before
  public void init() {
    marketDataService = mock(MarketDataService.class);
    Exchange bitstampService = mock(Exchange.class);
    when(bitstampService.getMarketDataService()).thenReturn(marketDataService);

    ExchangeFactory exchangeFactory = mock(ExchangeFactory.class);
    when(exchangeFactory.createExchange(anyString())).thenReturn(bitstampService);
    Whitebox.setInternalState(ExchangeFactory.class, "INSTANCE", exchangeFactory);

    bitstampPriceService = new BitstampPriceService();
  }


  @Test
  public void getPrice() throws IOException {
    when(marketDataService.getTicker(CurrencyPair.BTC_USD)).thenReturn(new Ticker.Builder()
        .last(new BigDecimal("100"))
        .build());

    Ticker price = bitstampPriceService.getPrice(CurrencyPair.BTC_USD);

    assertThat(price.getLast(), is(new BigDecimal("100")));
  }
}