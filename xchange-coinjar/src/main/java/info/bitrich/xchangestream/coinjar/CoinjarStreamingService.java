package info.bitrich.xchangestream.coinjar;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.coinjar.dto.CoinjarHeartbeat;
import info.bitrich.xchangestream.coinjar.dto.CoinjarWebSocketSubscribeMessage;
import info.bitrich.xchangestream.coinjar.dto.CoinjarWebSocketUnsubscribeMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CoinjarStreamingService extends JsonNettyStreamingService {

  private static final Logger LOG = LoggerFactory.getLogger(CoinjarStreamingService.class);

  private String apiKey;

  public CoinjarStreamingService(String apiUrl, String apiKey) {
    super(apiUrl);
    this.apiKey = apiKey;
    this.subscribeConnectionSuccess()
        .forEach(
            (l) -> {
              Observable.interval(30, TimeUnit.SECONDS)
                  .forEach(
                      (ll) -> {
                        if (this.isSocketOpen()) {
                          this.sendObjectMessage(CoinjarHeartbeat.INSTANCE);
                        }
                      });
            });
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) {
    return message.get("topic").asText();
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    return objectMapper.writeValueAsString(
        new CoinjarWebSocketSubscribeMessage(channelName, apiKey));
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    CoinjarWebSocketUnsubscribeMessage message = new CoinjarWebSocketUnsubscribeMessage();
    return objectMapper.writeValueAsString(message);
  }
}
