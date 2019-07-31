package info.bitrich.xchangestream.b2c2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import org.knowm.xchange.utils.jackson.MillisecTimestampDeserializer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class B2C2WebSocketPriceEvent {

  public final Levels levels;
  public final boolean success;
  public final String instrument;
  public final Date timestamp;

  public B2C2WebSocketPriceEvent(
      @JsonProperty("levels") Levels levels,
      @JsonProperty("success") boolean success,
      @JsonProperty("instrument") String instrument,
      @JsonProperty("timestamp") @JsonDeserialize(using = MillisecTimestampDeserializer.class)
              Date timestamp) {
    this.success = success;
    this.instrument = instrument;
    this.timestamp = timestamp;
    if (levels != null) {
      this.levels = levels;
    } else {
      this.levels = new Levels(Lists.newArrayList(), Lists.newArrayList());
    }
  }

  public static class Levels {
    public final List<Level> buy;
    public final List<Level> sell;

    public Levels(@JsonProperty("buy") List<Level> buy, @JsonProperty("sell") List<Level> sell) {
      if (buy != null) {
        this.buy = buy;
      } else {
        this.buy = Lists.newArrayList();
      }
      if (sell != null) {
        this.sell = sell;
      } else {
        this.sell = Lists.newArrayList();
      }
    }

    public static class Level {
      public final BigDecimal quantity;
      public final BigDecimal price;

      public Level(
              @JsonProperty("quantity") BigDecimal quantity, @JsonProperty("price") BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
      }
    }
  }
}
