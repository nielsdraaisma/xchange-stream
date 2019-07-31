package info.bitrich.xchangestream.b2c2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class B2C2WebSocketUnsubscribeMessage {

  private static final String EVENT = "event";
  private static final String INSTRUMENT = "instrument";
  private static final String LEVELS = "levels";
  private static final String TAG = "tag";

  @JsonProperty(EVENT)
  public final String event = "unsubscribe";

  @JsonProperty(INSTRUMENT)
  public final String instrument;

  @JsonProperty(TAG)
  public final String tag;

  public B2C2WebSocketUnsubscribeMessage(String instrument, String tag) {
    this.instrument = instrument;
    this.tag = tag;
  }

  public B2C2WebSocketUnsubscribeMessage(String instrument) {
    this.instrument = instrument;
    this.tag = null;
  }
}
