package info.bitrich.xchangestream.btcmarkets.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketSubscribeMessage;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketUnsubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;

import java.io.IOException;
import java.util.Set;

public class BTCMarketsStreamingService extends JsonNettyStreamingService {

  private static final Set<String> AUTHENTICATED_CHANNELS = Sets.newHashSet("orderChange");
  private String apiKey;
  private String apiSecret;

  public BTCMarketsStreamingService(String apiUrl, String apiKey, String apiSecret) {
    super(apiUrl);
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    return message.get("messageType").asText();
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    BTCMarketsWebSocketSubscribeMessage message =
        new BTCMarketsWebSocketSubscribeMessage(args[0].toString(), channelName);
    if (AUTHENTICATED_CHANNELS.contains(channelName)) {
      message = message.sign(apiKey, apiSecret);
    }
    return objectMapper.writeValueAsString(message);
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    // TODO : this unsubscribes from all marketIds in a channel and does not allow unsubscription of
    // a single market.
    BTCMarketsWebSocketUnsubscribeMessage message =
        new BTCMarketsWebSocketUnsubscribeMessage(channelName);
    return objectMapper.writeValueAsString(message);
  }
}
