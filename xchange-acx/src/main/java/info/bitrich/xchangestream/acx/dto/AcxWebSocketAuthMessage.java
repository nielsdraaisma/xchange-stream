package info.bitrich.xchangestream.acx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcxWebSocketAuthMessage {

  @JsonProperty("auth")
  public final Auth auth;

  public AcxWebSocketAuthMessage(String accessKey, String answer) {
    this.auth = new Auth(accessKey, answer);
  }

  public class Auth {
    @JsonProperty("access_key")
    public final String accessKey;

    @JsonProperty("answer")
    public final String answer;

    public Auth(String accessKey, String answer) {
      this.accessKey = accessKey;
      this.answer = answer;
    }
  }
}
