package info.bitrich.xchangestream.acx.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AcxWebSocketOrderbookMessageTest {

  private static ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  @Test
  public void testMessage() throws Exception {
    InputStream stream = this.getClass().getResourceAsStream("orderbook.json");
    AcxWebSocketOrderbookMessage message =
        mapper.readValue(stream, AcxWebSocketOrderbookMessage.class);
    assertThat(message.orderbook).isNotNull();
    assertThat(message.orderbook.action).isEqualTo("add");
    assertThat(message.orderbook.order.id).isEqualTo(3252);
    assertThat(message.orderbook.order.timestamp).isEqualTo(1402898864);
    assertThat(message.orderbook.order.type).isEqualTo("ask");
    assertThat(message.orderbook.order.volume).isEqualTo("1.0");
    assertThat(message.orderbook.order.price).isEqualTo("3500.0");
    assertThat(message.orderbook.order.market).isEqualTo("btcaud");
    assertThat(message.orderbook.order.orderType).isEqualTo("limit");
  }
}
