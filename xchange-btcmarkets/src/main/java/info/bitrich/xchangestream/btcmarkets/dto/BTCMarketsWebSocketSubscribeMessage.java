package info.bitrich.xchangestream.btcmarkets.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import info.bitrich.xchangestream.btcmarkets.service.BTCMarketsStreamingDigest;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BTCMarketsWebSocketSubscribeMessage {

  @JsonProperty("messageType")
  public final String messageType = "subscribe";

  @JsonProperty("marketIds")
  public final List<String> marketIds;

  @JsonProperty("channels")
  public final List<String> channels;

  @JsonProperty("timestamp")
  public final Long timestamp;

  @JsonProperty("key")
  public final String key;

  @JsonProperty("signature")
  public final String signature;

  public BTCMarketsWebSocketSubscribeMessage(String marketId, String channel) {
    this.marketIds = Lists.newArrayList(marketId);
    this.channels = Lists.newArrayList(channel);
    this.timestamp = null;
    this.key = null;
    this.signature = null;
  }

  private BTCMarketsWebSocketSubscribeMessage(
      List<String> marketIds, List<String> channels, Long timestamp, String key, String signature) {
    this.marketIds = marketIds;
    this.channels = channels;
    this.timestamp = timestamp;
    this.key = key;
    this.signature = signature;
  }

  public BTCMarketsWebSocketSubscribeMessage sign(String apiKey, String apiSecret) {
    long timestamp = System.currentTimeMillis();
    String stringToSign = "/users/self/subscribe" + "\n" + timestamp;

    BTCMarketsStreamingDigest digest = new BTCMarketsStreamingDigest("sha512", apiSecret);
    String signature = digest.base64Digest(stringToSign);
    return new BTCMarketsWebSocketSubscribeMessage(marketIds, channels, timestamp, key, signature);
  }
}
