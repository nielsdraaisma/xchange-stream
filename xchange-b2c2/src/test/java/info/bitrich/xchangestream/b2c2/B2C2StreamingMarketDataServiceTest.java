package info.bitrich.xchangestream.b2c2;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class B2C2StreamingMarketDataServiceTest {
  @Test
  public void test() throws IOException {
    JsonNode node =
        StreamingObjectMapperHelper.getObjectMapper()
            .readTree(
                "{ \"event\": \"price\", \"success\": true, \"instrument\": \"ETHUSD.SPOT\", \"levels\": { \"buy\": [ { \"price\": \"160.1\", \"quantity\": \"1\" } ], \"sell\": [ { \"price\": \"159.92\", \"quantity\": \"1\" } ] }, \"timestamp\": 1554356491250 }");
    B2C2StreamingService streamingService = Mockito.mock(B2C2StreamingService.class);
    Mockito.when(streamingService.subscribeChannel(ArgumentMatchers.matches("ETHUSD.SPOT")))
        .thenReturn(Observable.just(node));
    Mockito.when(streamingService.channelName(CurrencyPair.ETH_USD)).thenReturn("ETHUSD.SPOT");
    B2C2StreamingMarketDataService service = new B2C2StreamingMarketDataService(streamingService);
    Observable<OrderBook> orderBookObserverable = service.getOrderBook(CurrencyPair.ETH_USD);
    OrderBook orderBook = orderBookObserverable.blockingFirst();
    assertThat(orderBook.getBids().size()).isEqualTo(1);
    assertThat(orderBook.getAsks().size()).isEqualTo(1);
  }
}
