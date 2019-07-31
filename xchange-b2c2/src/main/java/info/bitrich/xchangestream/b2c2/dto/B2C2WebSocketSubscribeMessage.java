package info.bitrich.xchangestream.b2c2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

public class B2C2WebSocketSubscribeMessage {

  private static final String EVENT = "event";
  private static final String INSTRUMENT = "instrument";
  private static final String LEVELS = "levels";
  private static final String TAG = "tag";

  @JsonProperty(EVENT)
  public final String event = "subscribe";

  @JsonProperty(INSTRUMENT)
  public final String instrument;

  @JsonProperty(LEVELS)
  public final List<Integer> levels;

  @JsonProperty(TAG)
  public final String tag;

  public B2C2WebSocketSubscribeMessage(String instrument, List<Integer> levels, String tag) {
    this.instrument = instrument;
    this.levels = levels;
    this.tag = tag;
  }

  public B2C2WebSocketSubscribeMessage(String instrument) {
    this.instrument = instrument;
    this.levels = Lists.newArrayList(1);
    this.tag = null;
  }

}
