package info.bitrich.xchangestream.b2c2;

import info.bitrich.xchangestream.b2c2.dto.B2C2WebSocketPriceEvent;
import org.knowm.xchange.b2c2.B2C2Adapters;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.util.List;
import java.util.stream.Collectors;

public class B2C2StreamingAdapters {

  public static final OrderBook adaptPriceEventToOrderbook(B2C2WebSocketPriceEvent event) {
    List<LimitOrder> bids =
        event.levels.sell.stream()
            .filter(level -> level.price != null)
            .map(
                level ->
                    new LimitOrder(
                        Order.OrderType.BID,
                        level.quantity,
                        B2C2Adapters.adaptInstrumentToCurrencyPair(event.instrument),
                        null,
                        event.timestamp,
                        level.price))
            .collect(Collectors.toList());
    List<LimitOrder> asks =
        event.levels.buy.stream()
            .filter(level -> level.price != null)
            .map(
                level ->
                    new LimitOrder(
                        Order.OrderType.ASK,
                        level.quantity,
                        B2C2Adapters.adaptInstrumentToCurrencyPair(event.instrument),
                        null,
                        event.timestamp,
                        level.price))
            .collect(Collectors.toList());
    return new OrderBook(event.timestamp, asks, bids);
  }
}
