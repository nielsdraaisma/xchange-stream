package info.bitrich.xchangestream.coinjar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import java.util.Map;

public class CoinjarHeartbeat {
  public static final CoinjarHeartbeat INSTANCE = new CoinjarHeartbeat();

  @JsonProperty("topic")
  public final String topic = "phoenix";

  @JsonProperty("event")
  public final String event = "heartbeat";

  @JsonProperty("payload")
  public final Map<String, String> payload = Maps.newHashMap();

  @JsonProperty("ref")
  public final Integer ref = 0;

  private CoinjarHeartbeat() {}
}
