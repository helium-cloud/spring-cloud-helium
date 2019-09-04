package org.helium.boot.spring.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * Helium health indicator
 *
 * @author xionghui
 * @version 1.0.0
 * @since 1.0.0
 */
public class HeliumHealthIndicator extends AbstractHealthIndicator {

  @Override
  public void doHealthCheck(Health.Builder builder) throws Exception {
    boolean up = true;

    if (up) {
      builder.up();
    } else {
      builder.down();
    }
  }
}
