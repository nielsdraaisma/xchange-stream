package info.bitrich.xchangestream.acx.service;

import org.knowm.xchange.utils.Assert;
import org.knowm.xchange.utils.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.knowm.xchange.service.BaseParamsDigest.HMAC_SHA_256;

public class AcxStreamingDigest {

  private final Charset utf8 = Charset.forName("UTF-8");
  private final Mac mac;
  private final String apiKey;

  public AcxStreamingDigest(String apiKey, String apiSecret) {
    Assert.notNull(apiKey, "Null apiKey");
    this.apiKey = apiKey;
    try {
      mac = Mac.getInstance(HMAC_SHA_256);
      mac.init(new SecretKeySpec(apiSecret.getBytes(utf8), mac.getAlgorithm()));
    } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  public String answerChallenge(String challenge) {
    String payload = apiKey + challenge;
    return DigestUtils.bytesToHex(mac.doFinal(payload.getBytes())).toLowerCase();
  }
}
