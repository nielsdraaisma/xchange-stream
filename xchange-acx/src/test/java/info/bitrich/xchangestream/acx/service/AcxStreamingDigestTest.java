package info.bitrich.xchangestream.acx.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AcxStreamingDigestTest {
  @Test
  public void testAnswerChallenge() {
    AcxStreamingDigest digest = new AcxStreamingDigest("abc", "ghi");
    String answer = digest.answerChallenge("def");
    assertThat(answer)
        .isEqualTo("52ca0e5beab532532c62155e78d81c7dc8ad6d6f744cf3797668cf52dd2f9a41");
  }
}
