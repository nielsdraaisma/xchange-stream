package info.bitrich.xchangestream.acx.service;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.acx.dto.AcxWebSocketAuthMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcxStreamingService extends JsonNettyStreamingService {
  public static final String CHANNEL_CHALLENGE = "challenge";
  public static final String CHANNEL_TRADE = "challenge";
  public static final String CHANNEL_ORDERBOOK = "orderbook";
  public static final String CHANNEL_SUCCESS = "success";
  private static final Logger LOG = LoggerFactory.getLogger(AcxStreamingService.class);
  private static final String apiUrl = "wss://acx.io:8080";

  public AcxStreamingService(String apiKey, String apiSecret) {
    super(apiUrl);
    AcxStreamingDigest digest = new AcxStreamingDigest(apiKey, apiSecret);
    this.subscribeConnectionSuccess()
        .forEach(
            x -> {
              this.subscribeChannel(CHANNEL_CHALLENGE)
                  .forEach(
                      msg -> {
                        String challenge = msg.get("challenge").asText();
                        String response = digest.answerChallenge(challenge);
                        LOG.info("Sending challenge response");
                        sendObjectMessage(new AcxWebSocketAuthMessage(apiKey, response));
                      });
            });
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    if (message.has("orderbook")) {
      return CHANNEL_ORDERBOOK;
    } else if (message.has("trade")) {
      return CHANNEL_TRADE;
    } else if (message.has("challenge")) {
      return CHANNEL_CHALLENGE;
    } else if (message.has("success")) {
      return CHANNEL_SUCCESS;
    }
    throw new IllegalArgumentException("Cannot get channel from message" + message);
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) {
    return null;
  }

  @Override
  public String getUnsubscribeMessage(String channelName) {
    return null;
  }
}
