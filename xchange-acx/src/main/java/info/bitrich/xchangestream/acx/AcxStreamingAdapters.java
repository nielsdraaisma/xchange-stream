package info.bitrich.xchangestream.acx;

import info.bitrich.xchangestream.acx.dto.AcxWebSocketOrderbookMessage;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.utils.DateUtils;

import java.math.BigDecimal;

public class AcxStreamingAdapters {
  public static String adaptCurrencyPairToMarketId(CurrencyPair currencyPair) {
    return currencyPair.base.toString() + "-" + currencyPair.counter.toString();
  }

  public static CurrencyPair adaptMarketToCurrencyPair(String marketId) {
    int audIndex = marketId.indexOf("aud");
    return new CurrencyPair(
        marketId.substring(0, audIndex), marketId.substring(audIndex, audIndex + 3));
  }

  public static LimitOrder adaptOrderbookMessageToLimitOrder(AcxWebSocketOrderbookMessage message) {
    return new LimitOrder.Builder(
            Order.OrderType.valueOf(message.orderbook.order.type.toUpperCase()),
            adaptMarketToCurrencyPair(message.orderbook.order.market))
        .id(message.orderbook.order.id.toString())
        .timestamp(DateUtils.fromUnixTime(message.orderbook.order.timestamp))
        .originalAmount(new BigDecimal(message.orderbook.order.volume))
        .limitPrice(new BigDecimal(message.orderbook.order.price))
        .build();

    //      BTCMarketsWebSocketOrderbookMessage message) throws InvalidFormatException {
    //    CurrencyPair currencyPair = adaptMarketIdToCurrencyPair(message.marketId);
    //    BiFunction<List<String>, Order.OrderType, LimitOrder> toLimitOrder =
    //        (strings, ot) ->
    //            new LimitOrder.Builder(ot, currencyPair)
    //                .originalAmount(new BigDecimal(strings.get(1)))
    //                .limitPrice(new BigDecimal(strings.get(0)))
    //                .build();
    //
    //    return new OrderBook(
    //        DateUtils.fromISODateString(message.timestamp),
    //        message.asks.stream()
    //            .map((o) -> toLimitOrder.apply(o, Order.OrderType.ASK))
    //            .collect(Collectors.toList()),
    //        message.bids.stream()
    //            .map((o) -> toLimitOrder.apply(o, Order.OrderType.BID))
    //            .collect(Collectors.toList()));
  }
}
