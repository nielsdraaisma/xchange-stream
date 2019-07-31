package info.bitrich.xchangestream.acx.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import info.bitrich.xchangestream.acx.AcxStreamingAdapters;
import info.bitrich.xchangestream.acx.dto.AcxWebSocketOrderbookMessage;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.Pair;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static info.bitrich.xchangestream.acx.service.AcxStreamingService.CHANNEL_ORDERBOOK;

public class AcxStreamingMarketDataService implements StreamingMarketDataService {

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private final AcxStreamingService service;
  private final MarketDataService marketDataService;

  private final Map<CurrencyPair, Map<Long, LimitOrder>> bids = Maps.newConcurrentMap();
  private final Map<CurrencyPair, Map<Long, LimitOrder>> asks = Maps.newConcurrentMap();

  public AcxStreamingMarketDataService(
      MarketDataService marketDataService, AcxStreamingService service) {
    this.service = service;
    this.marketDataService = marketDataService;
  }

  private OrderBook handleOrderbookMessage(AcxWebSocketOrderbookMessage message)
      throws IOException {
    LimitOrder limitOrder = AcxStreamingAdapters.adaptOrderbookMessageToLimitOrder(message);
    CurrencyPair currencyPair = limitOrder.getCurrencyPair();
    Map<Long, LimitOrder> destinationMap;
    if (!bids.containsKey(currencyPair)) {
      OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
      final Map<Long, LimitOrder> bidOrders = Maps.newConcurrentMap();
      orderBook
          .getBids()
          .forEach(
              bid -> bidOrders.put(Long.valueOf(bid.getId()), bid));
      bids.put(currencyPair, bidOrders);
      final Map<Long, LimitOrder> askOrders = Maps.newConcurrentMap();
      orderBook
          .getAsks()
          .forEach(
              ask -> askOrders.put(Long.valueOf(ask.getId()), ask));
      asks.put(currencyPair, askOrders);
    }
    if (limitOrder.getType() == Order.OrderType.BID) {
      destinationMap = bids.get(limitOrder.getCurrencyPair());
    } else {
      destinationMap = asks.get(limitOrder.getCurrencyPair());
    }
    if ("remove".equals(message.orderbook.action)) {
      destinationMap.remove(message.orderbook.order.id);
    } else {
      destinationMap.put(message.orderbook.order.id, limitOrder);
    }
    return new OrderBook(
        limitOrder.getTimestamp(),
        asks.get(limitOrder.getCurrencyPair()).values().stream()
            .sorted(Comparator.comparing(LimitOrder::getLimitPrice))
            .collect(Collectors.toList()),
        bids.get(limitOrder.getCurrencyPair()).values().stream()
            .sorted(Comparator.comparing(LimitOrder::getLimitPrice))
            .collect(Collectors.toList()));
  }

  @Override
  public Observable<OrderBook> getOrderBook(final CurrencyPair currencyPair, Object... args) {
    return service
        .subscribeChannel(CHANNEL_ORDERBOOK)
        .map(e -> mapper.treeToValue(e, AcxWebSocketOrderbookMessage.class))
        .map(
            e ->
                Pair.of(
                    AcxStreamingAdapters.adaptMarketToCurrencyPair(e.orderbook.order.market),
                    handleOrderbookMessage(e)))
        .filter(e -> e.getLeft().equals(currencyPair))
        .map(Pair::getRight);
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
