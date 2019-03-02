package com.cryptoalert.server.alerts.btc;

import static com.cryptoalert.server.alerts.controllers.AlertsWSController.ALERTS_TOPIC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cryptoalert.server.ServerApplication;
import com.cryptoalert.server.alerts.AlertsRepository;
import com.cryptoalert.server.alerts.controllers.AlertsWSController;
import com.cryptoalert.server.alerts.dto.AlertNotification;
import com.cryptoalert.server.alerts.model.Alert;
import com.cryptoalert.server.crypto.bitstamp.BitstampPriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore({"javax.*.*", "com.sun.*", "org.xml.*"})
@PrepareForTest(ExchangeFactory.class)
@SpringBootTest(classes = ServerApplication.class,
    webEnvironment = WebEnvironment.DEFINED_PORT)
public class PriceCheckIntegrationTest {

  @Autowired
  private BtcAlertsChecker alertsChecker;
  @Autowired
  private AlertsRepository alertsRepository;
  @Autowired
  private BitstampPriceService bitstampPriceService;

  private MarketDataService marketDataServiceMock;
  private CompletableFuture<AlertNotification> wsMessageFuture;

  @Before
  public void init() throws InterruptedException, ExecutionException, TimeoutException {
    wsMessageFuture = new CompletableFuture<>();
    marketDataServiceMock = mock(MarketDataService.class);
    Whitebox.setInternalState(bitstampPriceService, "marketDataService", marketDataServiceMock);

    WebSocketClient webSocketClient = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSession session = stompClient.connect("ws://localhost:8081/alerts",
        new StompSessionHandlerAdapter() {}).get(1, SECONDS);
    session.subscribe(ALERTS_TOPIC, new AlertsNotificationHandler());
  }


  @Test
  public void getPrice()
      throws IOException, InterruptedException, ExecutionException, TimeoutException {
    when(marketDataServiceMock.getTicker(BtcAlertsChecker.CURRENCY_PAIR_BITSTAMP)).thenReturn(
        new Ticker.Builder().last(new BigDecimal("100")).build());
    alertsRepository.save(new Alert(BtcAlertsChecker.CURRENCY_PAIR, "100", false));
    alertsRepository.save(new Alert(BtcAlertsChecker.CURRENCY_PAIR, "99", false));
    alertsRepository.save(new Alert(BtcAlertsChecker.CURRENCY_PAIR, "101", false));

    alertsChecker.checkAlerts();

    List<Alert> alerts = alertsRepository.findAll();
    List<Alert> completed = alerts.stream().filter(Alert::isCompleted).collect(Collectors.toList());
    assertThat(completed.size(), is(1));
    assertThat(completed.get(0).getLimit(), is("99"));
    assertThat(wsMessageFuture.get(10, SECONDS).getLimit(), is("99"));
  }

  class AlertsNotificationHandler implements StompFrameHandler {

    @Override
    public Type getPayloadType(StompHeaders headers) {
      return AlertNotification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      wsMessageFuture.complete((AlertNotification) payload);
    }
  }
}