package com.jonkimbel.busboybackend;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link HttpUtils}.
 */
@RunWith(JUnit4.class)
public class HttpUtilsTest {
  @Test
  public void parseQueryString() throws Exception {
    Map<String, String> result =
        HttpUtils.parseQueryString("key=a8pz=8&stop=ID");

    assertThat(result).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_leadingQuestionMark() throws Exception {
    Map<String, String> result =
        HttpUtils.parseQueryString("?key=a8pz=8&stop=ID");

    assertThat(result).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_returnsEmptyForNullInput() throws Exception {
    Map<String, String> result = HttpUtils.parseQueryString(null);

    assertThat(result).isEmpty();
  }
}
