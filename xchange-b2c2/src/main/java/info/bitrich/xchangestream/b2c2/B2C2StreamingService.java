package info.bitrich.xchangestream.b2c2;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import info.bitrich.xchangestream.b2c2.dto.B2C2WebSocketSubscribeMessage;
import info.bitrich.xchangestream.b2c2.dto.B2C2WebSocketUnsubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class B2C2StreamingService extends JsonNettyStreamingService {

  private static final Logger LOG = LoggerFactory.getLogger(B2C2StreamingService.class);
  private static final String EVENT = "event";
  private static final String INSTRUMENT = "instrument";
  private static final String PRICE = "price";

  private final Map<String, List<Integer>> levels = new HashMap<>();

  private final String apiKey;

  public B2C2StreamingService(
          String apiUrl, String apiKey, Map<CurrencyPair, List<Integer>> levels) {
    super(apiUrl);
    this.apiKey = apiKey;
    // Store levels for each currencypair keyed by the channel name.
    for (Map.Entry<CurrencyPair, List<Integer>> e : levels.entrySet()) {
      this.levels.put(channelName(e.getKey()), e.getValue());
    }
  }

  @Override
  protected DefaultHttpHeaders getCustomHeaders() {
    DefaultHttpHeaders headers = super.getCustomHeaders();
    headers.add("Authorization", "Token " + this.apiKey);
    return headers;
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
  protected void handleMessage(JsonNode message) {
    JsonNode event = message.get(EVENT);
    if (event != null) {
      if (PRICE.equals(event.textValue())) {
        super.handleMessage(message);
      }
    }
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    String chanId;
    if (message.has(INSTRUMENT)) {
      chanId = message.get(INSTRUMENT).asText();
    } else {
      throw new IOException("Can't find INSTRUMENT value");
    }
    return chanId;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    List<Integer> levels = this.levels.getOrDefault(channelName, Lists.newArrayList(1));
    return objectMapper.writeValueAsString(
        new B2C2WebSocketSubscribeMessage(channelName, levels, null));
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    B2C2WebSocketUnsubscribeMessage message = new B2C2WebSocketUnsubscribeMessage(channelName);
    return objectMapper.writeValueAsString(message);
  }

  public String channelName(CurrencyPair currencyPair) {
    return currencyPair.base.toString() + currencyPair.counter.toString() + ".SPOT";
  }
}
