package info.bitrich.xchangestream.independentreserve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.independentreserve.dto.IndependentReserveWebSocketOrderEvent;
import info.bitrich.xchangestream.independentreserve.dto.IndependentReserveWebSocketSubscribtionEvent;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class IndependentReserveStreamingMarketDataService implements StreamingMarketDataService {

  private static final Logger logger =
      LoggerFactory.getLogger(IndependentReserveStreamingMarketDataService.class);

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private final IndependentReserveStreamingService service;
  private final MarketDataService marketDataService;

  private final Map<CurrencyPair, Map<String, LimitOrder>> bids = Maps.newHashMap();
  private final Map<CurrencyPair, Map<String, LimitOrder>> asks = Maps.newHashMap();

  public IndependentReserveStreamingMarketDataService(
      MarketDataService marketDataService, IndependentReserveStreamingService service) {
    this.service = service;
    this.marketDataService = marketDataService;
    service
        .subscribeConnectionSuccess()
        .forEach(
            x ->
                service
                    .subscribeChannel("Subscriptions")
                    .forEach(
                        node -> {
                          IndependentReserveWebSocketSubscribtionEvent subscribtionEvent =
                              mapper.treeToValue(
                                  node, IndependentReserveWebSocketSubscribtionEvent.class);
                          handleSubscriptionEvent(subscribtionEvent);
                        }));
  }

  private OrderBook handleOrderbookEvent(
      CurrencyPair currencyPair, IndependentReserveWebSocketOrderEvent event) {
    final Order.OrderType orderType;
    if (event.data.orderType.equals("LimitBid")) {
      orderType = Order.OrderType.BID;
    } else {
      orderType = Order.OrderType.ASK;
    }
    final CurrencyPair pairFromEvent =
        IndependentReserveStreamingAdapters.adaptChannelToCurrencyPair(event.channel);
    final Map<String, LimitOrder> orderMap;
    if (orderType == Order.OrderType.BID) {
      orderMap = bids.get(pairFromEvent);
    } else {
      orderMap = asks.get(pairFromEvent);
    }
    LimitOrder order;
    switch (event.event) {
      case IndependentReserveWebSocketOrderEvent.NEW_ORDER:
        order =
            new LimitOrder(
                orderType,
                event.data.volume,
                currencyPair,
                event.data.orderGuid,
                null,
                event.data.price);

        orderMap.put(event.data.orderGuid, order);
        break;
      case IndependentReserveWebSocketOrderEvent.ORDER_CANCELED:
        orderMap.remove(event.data.orderGuid);
        break;
      case IndependentReserveWebSocketOrderEvent.ORDER_CHANGED:
        // Fully filled orders are treated as removal
        if (event.data.volume.compareTo(BigDecimal.ZERO) == 0) {
          orderMap.remove(event.data.orderGuid);
          break;
        }
        order = orderMap.get(event.data.orderGuid);
        if (order != null) {
          order =
              new LimitOrder(
                  order.getType(),
                  event.data.volume,
                  currencyPair,
                  event.data.orderGuid,
                  null,
                  order.getLimitPrice());
          orderMap.put(event.data.orderGuid, order);
        }
        break;
    }
    return new OrderBook(
        null,
        Lists.newArrayList(asks.get(pairFromEvent).values()),
        Lists.newArrayList(bids.get(pairFromEvent).values()));
  }

  private void handleSubscriptionEvent(IndependentReserveWebSocketSubscribtionEvent event) {
    event.data.stream()
        .map(IndependentReserveStreamingAdapters::adaptChannelToCurrencyPair)
        .forEach(
            currencyPair -> {
              try {
                if (!bids.containsKey(currencyPair)) {
                  bids.put(currencyPair, Maps.newHashMap());
                }
                if (!asks.containsKey(currencyPair)) {
                  asks.put(currencyPair, Maps.newHashMap());
                }

                Map<String, LimitOrder> bids = this.bids.get(currencyPair);
                Map<String, LimitOrder> asks = this.asks.get(currencyPair);
                logger.info("Loading {} orderbook after subscribing to stream", currencyPair);
                bids.clear();
                asks.clear();
                OrderBook orderBook = this.marketDataService.getOrderBook(currencyPair);
                orderBook
                    .getBids()
                    .forEach(
                        bid -> {
                          if (bid.getOriginalAmount().compareTo(BigDecimal.ZERO) > 0) {
                            bids.put(bid.getId(), bid);
                          }
                        });
                orderBook
                    .getAsks()
                    .forEach(
                        bid -> {
                          if (bid.getOriginalAmount().compareTo(BigDecimal.ZERO) > 0) {
                            asks.put(bid.getId(), bid);
                          }
                        });
              } catch (IOException e) {
                logger.warn("Caught exception while loading {} orderbook", currencyPair, e);
              }
            });
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String channelName =
        "orderbook-"
            + currencyPair.base.toString().toLowerCase()
            + "-"
            + currencyPair.counter.toString().toLowerCase();
    return service
        .subscribeChannel(channelName)
        .map(
            node -> {
              IndependentReserveWebSocketOrderEvent orderEvent =
                  mapper.treeToValue(node, IndependentReserveWebSocketOrderEvent.class);
              return this.handleOrderbookEvent(currencyPair, orderEvent);
            });
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
