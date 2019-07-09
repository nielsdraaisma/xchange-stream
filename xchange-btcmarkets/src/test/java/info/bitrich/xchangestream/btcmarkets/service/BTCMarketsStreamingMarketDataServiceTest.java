package info.bitrich.xchangestream.btcmarkets.service;

import info.bitrich.xchangestream.btcmarkets.BTCMarketsStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BTCMarketsStreamingMarketDataServiceTest {
  private static final Logger logger =
      LoggerFactory.getLogger(BTCMarketsStreamingMarketDataServiceTest.class);

  @Test
  public void runTest() {
    final int messageToReceive = 2;

    ExchangeSpecification defaultExchangeSpecification =
        new ExchangeSpecification(BTCMarketsStreamingExchange.class);

    StreamingExchange exchange =
        StreamingExchangeFactory.INSTANCE.createExchange(defaultExchangeSpecification);
    exchange.connect().blockingAwait();

    Disposable btcOrderBookDisposable =
        exchange
            .getStreamingMarketDataService()
            .getOrderBook(CurrencyPair.BTC_AUD)
//            .take(messageToReceive)
            .forEach(
                orderBook -> {
                  logger.info("First btc ask: {}", orderBook.getAsks().get(0));
                  logger.info("First btc bid: {}", orderBook.getBids().get(0));
                });

    Disposable ethOrderBookDisposable =
        exchange
            .getStreamingMarketDataService()
            .getOrderBook(CurrencyPair.ETH_AUD)
//            .take(messageToReceive)
            .forEach(
                orderBook -> {
                  logger.info("First eth ask: {}", orderBook.getAsks().get(0));
                  logger.info("First eth bid: {}", orderBook.getBids().get(0));
                });

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

//    btcOrderBookDisposable.dispose();
//    ethOrderBookDisposable.dispose();
    //    tradesDisposable.dispose();
    exchange.disconnect().subscribe(() -> logger.info("Disconnected from the Exchange"));
  }
}
