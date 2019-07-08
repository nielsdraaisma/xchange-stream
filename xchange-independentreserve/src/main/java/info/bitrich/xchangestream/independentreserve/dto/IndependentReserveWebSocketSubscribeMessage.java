package info.bitrich.xchangestream.independentreserve.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IndependentReserveWebSocketSubscribeMessage {

  @JsonProperty("Event")
  public final String event = "Subscribe";

  @JsonProperty("Data")
  public final List<String> data;

  public IndependentReserveWebSocketSubscribeMessage(List<String> data) {
    this.data = data;
  }
}
