package com.cryptoalert.server.crypto.bitstamp;

import java.io.IOException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.stereotype.Service;

@Service
public class BitstampPriceService {

  private MarketDataService marketDataService;

  public BitstampPriceService() {
    Exchange bitstampService = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
    marketDataService = bitstampService.getMarketDataService();
  }

  /**
   * Get current market ticker for given currencyPair.
   * @param currencyPair target currencyPair.
   * @return market ticket.
   * @throws IOException networking error occurred while fetching JSON data.
   */
  public Ticker getPrice(CurrencyPair currencyPair) throws IOException {
    return marketDataService.getTicker(currencyPair);
  }
}
