package info.bitrich.xchangestream.coinjar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.bitrich.xchangestream.coinjar.CoinjarStreamingAdapters;
import info.bitrich.xchangestream.coinjar.CoinjarStreamingService;
import info.bitrich.xchangestream.coinjar.dto.CoinjarWebSocketBookEvent;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class CoinjarStreamingMarketDataService implements StreamingMarketDataService {

  private static final Logger logger =
      LoggerFactory.getLogger(CoinjarStreamingMarketDataService.class);

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private final CoinjarStreamingService service;

  private final Map<CurrencyPair, SortedMap<BigDecimal, LimitOrder>> bids = Maps.newConcurrentMap();
  private final Map<CurrencyPair, SortedMap<BigDecimal, LimitOrder>> asks = Maps.newConcurrentMap();

  public CoinjarStreamingMarketDataService(CoinjarStreamingService service) {
    this.service = service;
  }

  private static void updateOrderbook(Map<BigDecimal, LimitOrder> book, List<LimitOrder> orders) {
    orders.forEach(
        order -> {
          if (order.getOriginalAmount().compareTo(BigDecimal.ZERO) > 0) {
            book.put(order.getLimitPrice(), order);
          } else {
            book.remove(order.getLimitPrice());
          }
        });
  }

  private OrderBook handleOrderbookEvent(CoinjarWebSocketBookEvent event) {
    final CurrencyPair pairFromEvent =
        CoinjarStreamingAdapters.adaptTopicToCurrencyPair(event.topic);
    switch (event.event) {
      case CoinjarWebSocketBookEvent.UPDATE:
      case CoinjarWebSocketBookEvent.INIT:
        updateOrderbook(
            bids.get(pairFromEvent),
            CoinjarStreamingAdapters.toLimitOrders(
                event.payload.bids, pairFromEvent, Order.OrderType.BID));
        updateOrderbook(
            asks.get(pairFromEvent),
            CoinjarStreamingAdapters.toLimitOrders(
                event.payload.asks, pairFromEvent, Order.OrderType.ASK));
        break;
    }
    return new OrderBook(
        null,
        Lists.newArrayList(asks.get(pairFromEvent).values()),
        Lists.newArrayList(bids.get(pairFromEvent).values()));
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String channelName = CoinjarStreamingAdapters.adaptCurrencyPairToBookTopic(currencyPair);
    this.asks.put(currencyPair, Maps.newTreeMap(BigDecimal::compareTo));
    this.bids.put(currencyPair, Maps.newTreeMap((o1, o2) -> Math.negateExact(o1.compareTo(o2))));
    return service
        .subscribeChannel(channelName)
        .doOnError(
            throwable -> {
              logger.warn(
                  "encoutered error while subscribing to channel " + channelName, throwable);
            })
        .map(
            node -> {
              CoinjarWebSocketBookEvent orderEvent =
                  mapper.treeToValue(node, CoinjarWebSocketBookEvent.class);
              return this.handleOrderbookEvent(orderEvent);
            })
        .filter(orderbook -> !orderbook.getBids().isEmpty() && !orderbook.getAsks().isEmpty());
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
