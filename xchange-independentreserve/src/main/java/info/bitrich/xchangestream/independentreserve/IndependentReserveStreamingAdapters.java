package info.bitrich.xchangestream.independentreserve;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

public class IndependentReserveStreamingAdapters {

  public static final CurrencyPair adaptChannelToCurrencyPair(String channel) {
    String[] parts = channel.split("-");
    Currency base = new Currency(parts[1]);
    Currency counter = new Currency(parts[2]);
    return new CurrencyPair(base, counter);
  }

}
