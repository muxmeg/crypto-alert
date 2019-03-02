package com.cryptoalert.client.alerts;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Alerts CLI commands.
 */
@ShellComponent
public class AlertsCliCommands {

  final static String ALERT_DESCRIPTION = "Create alerts for crypto currency values";
  final static String ALERT_UNSUPPORTED_COMMAND = "Create alerts for crypto currency values";
  final static String ALERT_SAVED = "Alert saved!";
  final static String ALERT_DELETED = "Alert deleted!";
  final static String ALERT_ADD_COMMAND = "add";
  final static String ALERT_DELETE_COMMAND = "delete";
  static final String BTC_PAIR = "BTC-USD";

  private AlertsBackendProxy alertsBackendProxy;

  public AlertsCliCommands(AlertsBackendProxy alertsBackendProxy) {
    this.alertsBackendProxy = alertsBackendProxy;
  }

  @ShellMethod(ALERT_DESCRIPTION)
  public String alert(
      @ShellOption String command,
      @ShellOption String limit
  ) {
    switch (command) {
      case ALERT_ADD_COMMAND:
        alertsBackendProxy.saveAlert(BTC_PAIR, limit);
        return ALERT_SAVED;
      case ALERT_DELETE_COMMAND:
        alertsBackendProxy.deleteAlert(BTC_PAIR, limit);
        return ALERT_DELETED;
      default:
        return ALERT_UNSUPPORTED_COMMAND;
    }
  }

}
