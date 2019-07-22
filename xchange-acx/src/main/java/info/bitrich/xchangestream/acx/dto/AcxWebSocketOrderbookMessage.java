package info.bitrich.xchangestream.acx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcxWebSocketOrderbookMessage {

  public final Orderbook orderbook;

  public AcxWebSocketOrderbookMessage(@JsonProperty("orderbook") Orderbook orderbook) {
    this.orderbook = orderbook;
  }

  public static class Orderbook {
    public final String action;

    public final Order order;

    public Orderbook(@JsonProperty("action") String action, @JsonProperty("order") Order order) {
      this.action = action;
      this.order = order;
    }

    public static class Order {
      public Long id;
      public Long timestamp;
      public String type;
      public String volume;
      public String price;
      public String market;
      public String orderType;

      public Order(
          @JsonProperty("id") Long id,
          @JsonProperty("timestamp") Long timestamp,
          @JsonProperty("type") String type,
          @JsonProperty("volume") String volume,
          @JsonProperty("price") String price,
          @JsonProperty("market") String market,
          @JsonProperty("ord_type") String ordType) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.volume = volume;
        this.price = price;
        this.market = market;
        this.orderType = ordType;
      }
    }
  }
}
