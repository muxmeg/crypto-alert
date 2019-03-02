package com.cryptoalert.client;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@EnableFeignClients("com.cryptoalert.client.alerts")
public class ClientApplication {

  private static final String COMMAND_LINE_PREFIX = ">";

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  @Bean
  public PromptProvider myPromptProvider() {
    return () -> new AttributedString(COMMAND_LINE_PREFIX,
        AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
  }
}
