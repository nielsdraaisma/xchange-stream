package info.bitrich.xchangestream.independentreserve.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndependentReserveWebSocketSubscribtionEvent extends IndependentReserveEvent {
  public static final String SUBSCRIPTIONS = "Subscriptions";

  public final List<String> data;

  public final String event;

  public IndependentReserveWebSocketSubscribtionEvent(
      @JsonProperty("Data") List<String> data, @JsonProperty("Event") String event) {
    this.data = data;
    this.event = event;
  }
}
