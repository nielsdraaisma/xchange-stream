package info.bitrich.xchangestream.independentreserve;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import info.bitrich.xchangestream.independentreserve.dto.IndependentReserveWebSocketSubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IndependentReserveStreamingService extends JsonNettyStreamingService {

  private static final Logger LOG =
      LoggerFactory.getLogger(IndependentReserveStreamingService.class);

  public IndependentReserveStreamingService(String apiUrl) {
    super(apiUrl);
  }

  @Override
  public void messageHandler(String message) {
    LOG.debug("Received message: {}", message);
    JsonNode jsonNode;

    // Parse incoming message to JSON
    try {
      jsonNode = objectMapper.readTree(message);
    } catch (IOException e) {
      LOG.error("Error parsing incoming message to JSON: {}", message);
      return;
    }

    handleMessage(jsonNode);
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    String event = message.get("Event").asText();
    switch (event) {
      case "NewOrder":
      case "OrderChanged":
      case "OrderCanceled":
        return message.get("Channel").asText();
      default:
        return event;
    }
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new IndependentReserveWebSocketSubscribeMessage(Lists.newArrayList(channelName)));
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    IndependentReserveWebSocketSubscribeMessage message =
        new IndependentReserveWebSocketSubscribeMessage(Lists.newArrayList(channelName));
    return objectMapper.writeValueAsString(message);
  }
}
