package info.bitrich.xchangestream.coinjar.service;

import info.bitrich.xchangestream.coinjar.CoinjarStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinjarStreamingMarketDataServiceTest {

  private static final Logger logger =
      LoggerFactory.getLogger(CoinjarStreamingMarketDataServiceTest.class);

  @Test
  public void runTest() {
    final int messageToReceive = 10;

    ExchangeSpecification defaultExchangeSpecification =
        new ExchangeSpecification(CoinjarStreamingExchange.class);

    StreamingExchange exchange =
        StreamingExchangeFactory.INSTANCE.createExchange(defaultExchangeSpecification);
    exchange.connect().blockingAwait();

    Disposable btcOrderBookDisposable =
        exchange
            .getStreamingMarketDataService()
            .getOrderBook(CurrencyPair.BTC_AUD)
            .take(messageToReceive)
            .forEach(orderBook -> logger.info("Got orderbook {}", orderBook));

    Disposable ethOrderBookDisposable =
        exchange
            .getStreamingMarketDataService()
            .getOrderBook(CurrencyPair.ETH_AUD)
            .take(messageToReceive)
            .forEach(orderBook -> logger.info("Got orderbook {}", orderBook));

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ethOrderBookDisposable.dispose();
    btcOrderBookDisposable.dispose();
  }
}
