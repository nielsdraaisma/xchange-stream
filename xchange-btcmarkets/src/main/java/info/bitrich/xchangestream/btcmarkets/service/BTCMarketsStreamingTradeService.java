package info.bitrich.xchangestream.btcmarkets.service;

import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

public class BTCMarketsStreamingTradeService implements StreamingTradeService {

  private final BTCMarketsStreamingService service;

  public BTCMarketsStreamingTradeService(BTCMarketsStreamingService service) {
    this.service = service;
  }

  @Override
  public Observable<Order> getOrderChanges(CurrencyPair currencyPair, Object... args) {
    return null;
  }
}
