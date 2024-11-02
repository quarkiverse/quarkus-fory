package io.quarkiverse.fury;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.apache.fury.BaseFury;

/** Producers of beans that are injectable via CDI. */
@Singleton
public class FuryProducer {
  private volatile BaseFury fury;

  public void setFury(BaseFury fury) {
    this.fury = fury;
  }

  @Singleton
  @Produces
  BaseFury fury() {
    return this.fury;
  }
}
