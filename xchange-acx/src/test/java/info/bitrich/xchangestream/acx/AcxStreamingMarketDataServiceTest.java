package info.bitrich.xchangestream.acx;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class AcxStreamingMarketDataServiceTest {
  private static final Logger logger =
      LoggerFactory.getLogger(AcxStreamingMarketDataServiceTest.class);

  @Test
  public void runTest() {
    final int messageToReceive = 2;

    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getResourceAsStream("secret.keys"));
    } catch (IOException e) {
      logger.warn("No ACX secret.keys found, skipping test");
      return;
    }

    String apiKey = properties.getProperty("api-key");
    String apiSecret = properties.getProperty("api-secret");

    ExchangeSpecification defaultExchangeSpecification =
        new ExchangeSpecification(AcxStreamingExchange.class);
    defaultExchangeSpecification.setApiKey(apiKey);
    defaultExchangeSpecification.setSecretKey(apiSecret);

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
                  logger.info("Bids: {}", orderBook.getAsks().size());
                  logger.info("Asks: {}", orderBook.getBids().size());
                });

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    exchange.disconnect().subscribe(() -> logger.info("Disconnected from the Exchange"));
  }
}
