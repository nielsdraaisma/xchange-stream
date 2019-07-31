package info.bitrich.xchangestream.b2c2;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.b2c2.B2C2Exchange;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;
import java.util.Map;

public class B2C2StreamingExchange extends B2C2Exchange implements StreamingExchange {

  public static final String LEVELS = "levels";
  private static final String API_URI = "wss://socket.b2c2.net/quotes";
  private static final String SANDBOX_API_URI = "wss://sandboxsocket.b2c2.net/quotes";
  private B2C2StreamingService streamingService;
  private B2C2StreamingMarketDataService streamingMarketDataService;

  @Override
  protected void initServices() {
    super.initServices();
    this.streamingService = createStreamingService();
    this.streamingMarketDataService = new B2C2StreamingMarketDataService(streamingService);
  }

  private B2C2StreamingService createStreamingService() {
    String apiUrl;
    if (exchangeSpecification.getHost() != null
        && exchangeSpecification.getHost().indexOf("sandbox") > 0) {
      apiUrl = SANDBOX_API_URI;
    } else {
      apiUrl = API_URI;
    }

    Map<CurrencyPair, List<Integer>> levels =
        (Map<CurrencyPair, List<Integer>>)
            exchangeSpecification.getExchangeSpecificParametersItem(LEVELS);

    B2C2StreamingService streamingService =
        new B2C2StreamingService(apiUrl, exchangeSpecification.getApiKey(), levels);
    applyStreamingSpecification(getExchangeSpecification(), streamingService);
    return streamingService;
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    return streamingService.connect();
  }

  @Override
  public Completable disconnect() {
    return streamingService.disconnect();
  }

  @Override
  public boolean isAlive() {
    return streamingService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification spec = super.getDefaultExchangeSpecification();
    spec.setShouldLoadRemoteMetaData(false);
    return spec;
  }

  @Override
  public B2C2StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
