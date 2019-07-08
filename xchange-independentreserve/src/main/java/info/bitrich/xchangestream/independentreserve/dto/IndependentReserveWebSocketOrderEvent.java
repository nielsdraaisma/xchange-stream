package info.bitrich.xchangestream.independentreserve.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndependentReserveWebSocketOrderEvent extends IndependentReserveEvent {
  public static final String NEW_ORDER = "NewOrder";
  public static final String ORDER_CHANGED = "OrderChanged";
  public static final String ORDER_CANCELED = "OrderCanceled";

  public final String channel;

  public final Long nonce;

  public final Data data;

  public final String event;

  public IndependentReserveWebSocketOrderEvent(
      @JsonProperty("Channel") String channel,
      @JsonProperty("Nonce") Long nonce,
      @JsonProperty("Data") Data data,
      @JsonProperty("Event") String event) {
    this.channel = channel;
    this.nonce = nonce;
    this.data = data;
    this.event = event;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Data {
    public final String orderGuid;

    public final String pair;

    public final BigDecimal price;

    public final String orderType;

    public final BigDecimal volume;

    public Data(
        @JsonProperty("OrderGuid") String orderGuid,
        @JsonProperty("Pair") String pair,
        @JsonProperty("Price") BigDecimal price,
        @JsonProperty("OrderType") String orderType,
        @JsonProperty("Volume") BigDecimal volume) {
      this.orderGuid = orderGuid;
      this.pair = pair;
      this.price = price;
      this.orderType = orderType;
      this.volume = volume;
    }
  }
}
