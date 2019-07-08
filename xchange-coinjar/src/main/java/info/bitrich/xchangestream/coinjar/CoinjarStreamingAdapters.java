package info.bitrich.xchangestream.coinjar;

import info.bitrich.xchangestream.coinjar.dto.CoinjarWebSocketBookEvent;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CoinjarStreamingAdapters {

  public static final CurrencyPair adaptTopicToCurrencyPair(String topic) {
    if (topic.startsWith("book")) {
      topic = topic.substring(5);
      Currency base = new Currency(topic.substring(0, 3));
      Currency counter = new Currency(topic.substring(3, 6));
      return new CurrencyPair(base, counter);
    } else throw new IllegalArgumentException("Cannot determine topic from topic name " + topic);
  }

  public static final String adaptCurrencyPairToBookTopic(CurrencyPair pair) {
    return "book:" + pair.base.toString() + pair.counter.toString();
  }

  public static final LimitOrder toLimitOrder(
      CoinjarWebSocketBookEvent.Payload.Order order,
      CurrencyPair currencyPair,
      Order.OrderType orderType) {
    return new LimitOrder(
        orderType,
        new BigDecimal(order.volume),
        currencyPair,
        null,
        null,
        new BigDecimal(order.price));
  }

  public static final List<LimitOrder> toLimitOrders(
      List<CoinjarWebSocketBookEvent.Payload.Order> orders,
      CurrencyPair currencyPair,
      Order.OrderType orderType) {
    return orders.stream()
        .map(o -> toLimitOrder(o, currencyPair, orderType))
        .collect(Collectors.toList());
  }
}
