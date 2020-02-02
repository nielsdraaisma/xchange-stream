package info.bitrich.xchangestream.independentreserve;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;

import io.reactivex.disposables.Disposable;

import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.*;

public class IndependentReserveManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(IndependentReserveManualExample.class);

    public static void main(String[] args) throws InterruptedException {

        ExchangeSpecification spec = StreamingExchangeFactory.INSTANCE.createExchange(
                IndependentReserveStreamingExchange.class.getName()).getDefaultExchangeSpecification();
        IndependentReserveStreamingExchange exchange = (IndependentReserveStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        exchange.connect().blockingAwait();

        Disposable orderbooks = orderbooks(exchange, new CurrencyPair(Currency.XBT, Currency.AUD));
        while (!orderbooks.isDisposed()) {
            sleep(5000);

        }

        exchange.disconnect().blockingAwait();
    }

    private static Disposable orderbooks(StreamingExchange exchange, CurrencyPair pair) {
        return exchange.getStreamingMarketDataService()
                .getOrderBook(pair)
                .subscribe(orderBook -> {
                    LOG.info(
                            "Order Book ({}): askDepth={} ask={} askSize={} bidDepth={}. bid={}, bidSize={}",
                            pair,
                            orderBook.getAsks().size(),
                            orderBook.getAsks().get(0).getLimitPrice(),
                            orderBook.getAsks().get(0).getRemainingAmount(),
                            orderBook.getBids().size(),
                            orderBook.getBids().get(0).getLimitPrice(),
                            orderBook.getBids().get(0).getRemainingAmount()
                    );
                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));
    }
}
