package info.bitrich.xchangestream.btcmarkets.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketSubscribeMessage;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketUnsubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BTCMarketsStreamingService extends JsonNettyStreamingService {
  public static final Set<String> AUTHENTICATED_CHANNELS = Sets.newHashSet("orderChange");
  public static final String CHANNEL_ORDERBOOK = "orderbook";
  private static final Logger logger = LoggerFactory.getLogger(BTCMarketsStreamingService.class);
  private String apiKey;
  private String apiSecret;
  private Set<String> subscribedOrderbooks = Sets.newConcurrentHashSet();

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
    if (CHANNEL_ORDERBOOK.equals(channelName)) {
      subscribedOrderbooks.add(args[0].toString());
      logger.debug("Now subscribed to orderbooks {}", subscribedOrderbooks);
      BTCMarketsWebSocketSubscribeMessage message =
          new BTCMarketsWebSocketSubscribeMessage(
              new ArrayList<>(subscribedOrderbooks),
              Lists.newArrayList(channelName),
              null,
              null,
              null);
      if (AUTHENTICATED_CHANNELS.contains(channelName)) {
        message = message.sign(apiKey, apiSecret);
      }
      return objectMapper.writeValueAsString(message);
    } else {
      throw new IllegalArgumentException(
          "Can't create subscribe messsage for channel " + channelName);
    }
  }

  public String getSubscriptionUniqueId(String channelName, Object... args) {
    if (CHANNEL_ORDERBOOK.equals(channelName)) {
      return channelName + ":" + args[0].toString();
    }
    return channelName;
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    BTCMarketsWebSocketUnsubscribeMessage message =
        new BTCMarketsWebSocketUnsubscribeMessage(channelName);
    return objectMapper.writeValueAsString(message);
  }
}
