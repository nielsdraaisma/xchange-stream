package info.bitrich.xchangestream.btcmarkets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

public class BTCMarketsWebSocketUnsubscribeMessage {

  @JsonProperty("messageType")
  public final String messageType = "removeSubscription";

  @JsonProperty("marketIds  ")
  public final List<String> marketIds;

  @JsonProperty("channels")
  public final List<String> channels;

  public BTCMarketsWebSocketUnsubscribeMessage(String channel) {
    this.marketIds = Lists.newArrayList();
    this.channels = Lists.newArrayList(channel);
  }
}
