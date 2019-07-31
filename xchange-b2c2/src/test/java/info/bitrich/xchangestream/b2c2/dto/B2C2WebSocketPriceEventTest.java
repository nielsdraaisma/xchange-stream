package info.bitrich.xchangestream.b2c2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class B2C2WebSocketPriceEventTest {

  @Test
  public void testUnmarshalling() throws IOException {

    String json =
        "{ \"event\": \"price\", \"success\": true, \"instrument\": \"ETHUSD.SPOT\", \"levels\": { \"buy\": [ { \"price\": \"156.76\", \"quantity\": \"1\" } ], \"sell\": [ { \"price\": \"156.36\", \"quantity\": \"1\" } ] }, \"timestamp\": 1554350656890 }";
    ObjectMapper mapper = new ObjectMapper();
    B2C2WebSocketPriceEvent event = mapper.readValue(json, B2C2WebSocketPriceEvent.class);
    assertThat(event.timestamp).isEqualTo(Date.from(Instant.ofEpochMilli(1554350656890L)));
    assertThat(event.instrument).isEqualTo("ETHUSD.SPOT");
    assertThat(event.levels.buy.size()).isEqualTo(1);
    assertThat(event.levels.buy.get(0).price).isEqualTo(new BigDecimal("156.76"));
    assertThat(event.levels.buy.get(0).quantity).isEqualTo(new BigDecimal("1"));

    assertThat(event.levels.sell.size()).isEqualTo(1);
    assertThat(event.levels.sell.get(0).price).isEqualTo(new BigDecimal("156.36"));
    assertThat(event.levels.sell.get(0).quantity).isEqualTo(new BigDecimal("1"));
  }
}
