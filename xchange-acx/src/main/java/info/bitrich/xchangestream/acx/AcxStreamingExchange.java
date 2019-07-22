package info.bitrich.xchangestream.acx;

import info.bitrich.xchangestream.acx.service.AcxStreamingMarketDataService;
import info.bitrich.xchangestream.acx.service.AcxStreamingService;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.acx.AcxExchange;

public class AcxStreamingExchange extends AcxExchange implements StreamingExchange {

  private AcxStreamingService streamingService;
  private AcxStreamingMarketDataService streamingMarketDataService;

  @Override
  protected void initServices() {
    super.initServices();

    this.streamingService = createStreamingService();
    this.streamingMarketDataService =
        new AcxStreamingMarketDataService(this.getMarketDataService(), streamingService);
  }

  private AcxStreamingService createStreamingService() {
    return new AcxStreamingService(
        exchangeSpecification.getApiKey(), exchangeSpecification.getSecretKey());
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
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
