package com.example.spring_boot_project_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import jakarta.annotation.PreDestroy; // បើ Spring Boot 2 ប្រើ javax.annotation.PreDestroy

@Component
@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
public class NgrokTunnelRunner {

  private static final Logger log = LoggerFactory.getLogger(NgrokTunnelRunner.class);

  @Value("${ngrok.authtoken}")
  private String authToken;

  @Value("${ngrok.frontend-port:5173}")
  private int frontendPort;

  @Value("${ngrok.domain:}")
  private String domain;

  private NgrokClient client;

  @EventListener(ApplicationReadyEvent.class)
  public void openTunnels() {
    JavaNgrokConfig config = new JavaNgrokConfig.Builder()
        .withAuthToken(authToken)
        .build();
    client = new NgrokClient.Builder().withJavaNgrokConfig(config).build();

    CreateTunnel.Builder tunnel = new CreateTunnel.Builder().withAddr(frontendPort);
    if (domain != null && !domain.isBlank()) {
      tunnel.withDomain(domain);
    }
    Tunnel frontend = client.connect(tunnel.build());

    log.info("=== NGROK URL: {}", frontend.getPublicUrl());
  }

  @PreDestroy
  public void closeTunnels() {
    if (client != null) client.kill();
  }
}