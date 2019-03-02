package com.cryptoalert.client.alerts;

import com.cryptoalert.client.alerts.dto.AlertNotification;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Service that listens for triggered alerts notifications.
 */
@Service
@Slf4j
public class AlertNotificationsService {

  static final String WS_URL_ALERTS = "ws://localhost:8081/alerts";
  static final String WS_CONNECTION_SUCCESS_MESSAGE = "Websocket connected!";
  static final String WS_CONNECTION_ERROR_MESSAGE = "Websocket connected!";
  static final String WS_TOPIC_ALERT_NOTIFICATIONS = "/topic/alerts";
  static final String ALERT_NOTIFICATION_MESSAGE = "Received alert notification! Currency pair: %s, "
      + "notification limit: %s, last price: %s\n>";

  private AlertsBackendProxy alertsBackendProxy;

  public AlertNotificationsService(AlertsBackendProxy alertsBackendProxy) {
    this.alertsBackendProxy = alertsBackendProxy;

    WebSocketClient webSocketClient = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSessionHandler sessionHandler = new AlertsNotificationSessionHandler();
    ListenableFuture<StompSession> connect = stompClient.connect(WS_URL_ALERTS, sessionHandler);
    connect.addCallback(stompSession -> {
      log.info(WS_CONNECTION_SUCCESS_MESSAGE);
    }, throwable -> {
      log.error(WS_CONNECTION_ERROR_MESSAGE, throwable);
    });
  }

  class AlertsNotificationSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
      session.subscribe(WS_TOPIC_ALERT_NOTIFICATIONS, new StompFrameHandler() {

        @Override
        public Type getPayloadType(StompHeaders headers) {
          return AlertNotification.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
          AlertNotification alert = (AlertNotification) payload;
          System.out.print(String.format(ALERT_NOTIFICATION_MESSAGE, alert.getCurrencyPair(),
              alert.getLimit(), alert.getLastPrice()));
        }
      });
    }
  }

}
