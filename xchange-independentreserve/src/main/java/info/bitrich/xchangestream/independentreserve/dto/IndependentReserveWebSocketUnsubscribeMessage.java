package info.bitrich.xchangestream.independentreserve.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IndependentReserveWebSocketUnsubscribeMessage {

  @JsonProperty("Event")
  public final String event = "Unsubscribe";

  @JsonProperty("Data")
  public final List<String> data;

  public IndependentReserveWebSocketUnsubscribeMessage(List<String> data) {
    this.data = data;
  }
}
