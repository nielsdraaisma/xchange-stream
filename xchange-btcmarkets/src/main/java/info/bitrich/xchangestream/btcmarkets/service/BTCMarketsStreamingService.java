package info.bitrich.xchangestream.btcmarkets.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketSubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BTCMarketsStreamingService extends JsonNettyStreamingService {
  public static final Set<String> AUTHENTICATED_CHANNELS = Sets.newHashSet("orderChange");
  public static final String CHANNEL_ORDERBOOK = "orderbook";
  public static final String CHANNEL_HEARTBEAT = "heartbeat";
  private static final Logger LOG = LoggerFactory.getLogger(BTCMarketsStreamingService.class);
  private String apiKey;
  private String apiSecret;
  private Set<String> subscribedOrderbooks = Sets.newConcurrentHashSet();

  public BTCMarketsStreamingService(String apiUrl, String apiKey, String apiSecret) {
    super(apiUrl);
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }

  private BTCMarketsWebSocketSubscribeMessage buildSubscribeMessage() {
    BTCMarketsWebSocketSubscribeMessage message =
        new BTCMarketsWebSocketSubscribeMessage(
            new ArrayList<>(subscribedOrderbooks),
            Lists.newArrayList(CHANNEL_ORDERBOOK, CHANNEL_HEARTBEAT),
            null,
            null,
            null);
    // if (AUTHENTICATED_CHANNELS.contains(channelName)) {
    //    message = message.sign(apiKey, apiSecret);
    return message;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    final String messageType = message.get("messageType").asText();
    if (messageType.startsWith(CHANNEL_ORDERBOOK)) {
      return messageType + ":" + message.get("marketId").asText();
    }
    return messageType;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    if (CHANNEL_ORDERBOOK.equals(channelName)) {
      subscribedOrderbooks.add(args[0].toString());
      LOG.debug("Now subscribed to orderbooks {}", subscribedOrderbooks);
      return objectMapper.writeValueAsString(buildSubscribeMessage());
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
    if (channelName.startsWith(CHANNEL_ORDERBOOK)) {
      final String[] parts = channelName.split(":");
      final String market = parts[1];
      //      subscribedOrderbooks.remove(market);

      return objectMapper.writeValueAsString(buildSubscribeMessage());
    } else {
      return null;
    }
  }
}
