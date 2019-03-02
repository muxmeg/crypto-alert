package com.cryptoalert.client.alerts;

import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_ADD_COMMAND;
import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_DELETED;
import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_DELETE_COMMAND;
import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_DESCRIPTION;
import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_SAVED;
import static com.cryptoalert.client.alerts.AlertsCliCommands.ALERT_UNSUPPORTED_COMMAND;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.util.ReflectionUtils;

public class AlertsCliCommandsTest {

  private AlertsBackendProxy alertsBackendProxy = mock(AlertsBackendProxy.class);

  private StandardMethodTargetRegistrar registrar =
      new StandardMethodTargetRegistrar();
  private ConfigurableCommandRegistry registry =
      new ConfigurableCommandRegistry();

  private MethodTarget alertMethod;

  @Before
  public void setUp() {
    ApplicationContext context = new AnnotationConfigApplicationContext();
    ((AnnotationConfigApplicationContext) context).refresh();
    ((AnnotationConfigApplicationContext) context).registerBean(AlertsCliCommands.class, alertsBackendProxy);
    registrar.setApplicationContext(context);
    registrar.register(registry);

    Map<String, MethodTarget> commands = registry.listCommands();
    alertMethod = commands.get("alert");
  }

  @Test
  public void alertCommand() {
    assertThat(alertMethod, notNullValue());
    assertThat(alertMethod.getHelp(), is(ALERT_DESCRIPTION));
    assertThat(alertMethod.getMethod(), is(
        ReflectionUtils.findMethod(AlertsCliCommands.class, "alert", String.class,
            String.class)));
    assertThat(alertMethod.getAvailability().isAvailable(), is(true));
  }

  @Test
  public void alertAddCommand() {
    Object result = ReflectionUtils.invokeMethod(alertMethod.getMethod(),
        alertMethod.getBean(), ALERT_ADD_COMMAND, "100");

    assertThat(result, is(ALERT_SAVED));
    verify(alertsBackendProxy, times(1))
        .saveAlert(AlertsCliCommands.BTC_PAIR, "100");
  }

  @Test
  public void alertDeleteCommand() {
    Object result = ReflectionUtils.invokeMethod(alertMethod.getMethod(),
        alertMethod.getBean(), ALERT_DELETE_COMMAND, "100");

    assertThat(result, is(ALERT_DELETED));
    verify(alertsBackendProxy, times(1))
        .deleteAlert(AlertsCliCommands.BTC_PAIR, "100");
  }

  @Test
  public void alertUnsupportedCommand() {
    Object result = ReflectionUtils.invokeMethod(alertMethod.getMethod(),
        alertMethod.getBean(), "unsupported", "100");
    assertThat(result, is(ALERT_UNSUPPORTED_COMMAND));
  }
}