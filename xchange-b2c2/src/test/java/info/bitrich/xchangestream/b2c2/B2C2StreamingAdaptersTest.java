package info.bitrich.xchangestream.b2c2;

import com.google.common.collect.Lists;
import info.bitrich.xchangestream.b2c2.dto.B2C2WebSocketPriceEvent;
import org.junit.Test;
import org.knowm.xchange.dto.marketdata.OrderBook;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class B2C2StreamingAdaptersTest {

  @Test
  public void testAdaptPriceEvent() {
    B2C2WebSocketPriceEvent.Levels levels =
        new B2C2WebSocketPriceEvent.Levels(
            Lists.newArrayList(
                new B2C2WebSocketPriceEvent.Levels.Level(
                    new BigDecimal("1"), new BigDecimal("500"))),
            Lists.newArrayList(
                new B2C2WebSocketPriceEvent.Levels.Level(
                    new BigDecimal("2"), new BigDecimal("550"))));

    B2C2WebSocketPriceEvent event =
        new B2C2WebSocketPriceEvent(
            levels, true, "BTCUSD.SPOT", Date.from(Instant.ofEpochMilli(1554350656890L)));

    OrderBook orderbook = B2C2StreamingAdapters.adaptPriceEventToOrderbook(event);
    assertThat(orderbook.getAsks().size()).isEqualTo(1);
    assertThat(orderbook.getAsks().get(0).getLimitPrice()).isEqualTo(new BigDecimal("500"));
    assertThat(orderbook.getAsks().get(0).getOriginalAmount()).isEqualTo(new BigDecimal("1"));
    assertThat(orderbook.getBids().size()).isEqualTo(1);
    assertThat(orderbook.getBids().get(0).getLimitPrice()).isEqualTo(new BigDecimal("550"));
    assertThat(orderbook.getBids().get(0).getOriginalAmount()).isEqualTo(new BigDecimal("2"));
  }
}
