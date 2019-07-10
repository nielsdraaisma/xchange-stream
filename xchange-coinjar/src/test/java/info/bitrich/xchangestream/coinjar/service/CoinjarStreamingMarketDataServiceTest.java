package info.bitrich.xchangestream.coinjar.service;

import info.bitrich.xchangestream.coinjar.CoinjarStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
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
    StreamingMarketDataService streamingMarketDataService =
        exchange.getStreamingMarketDataService();

    Disposable btcOrderBookDisposable =
        streamingMarketDataService
            .getOrderBook(CurrencyPair.BTC_AUD)
            //            .take(messageToReceive)
            .forEach(orderBook -> logger.info("Got BTCAUD orderbook"));

    Disposable ethOrderBookDisposable =
        streamingMarketDataService
            .getOrderBook(CurrencyPair.ETH_AUD)
            //            .take(messageToReceive)
            .forEach(orderBook -> logger.info("Got ETHAUD orderbook"));

    try {
      Thread.sleep(120000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ethOrderBookDisposable.dispose();
    btcOrderBookDisposable.dispose();
  }
}
