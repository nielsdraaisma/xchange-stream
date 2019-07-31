package info.bitrich.xchangestream.b2c2;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.b2c2.dto.B2C2WebSocketPriceEvent;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;

public class B2C2StreamingMarketDataService implements StreamingMarketDataService {

  private final B2C2StreamingService service;

  public B2C2StreamingMarketDataService(B2C2StreamingService service) {
    this.service = service;
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String channelName = service.channelName(currencyPair);
    final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

    return service
        .subscribeChannel(channelName)
        .map(node -> mapper.treeToValue(node, B2C2WebSocketPriceEvent.class))
        .map(B2C2StreamingAdapters::adaptPriceEventToOrderbook);
  }

  @Override
  public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
    throw new NotAvailableFromExchangeException();
  }

  @Override
  public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
    throw new NotAvailableFromExchangeException();
  }
}
