package com.jonkimbel.busboybackend;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

/**
 * Unit tests for {@link HttpUtils}.
 */
@RunWith(JUnit4.class)
public class HttpUtilsTest {
  private HttpUtils httpUtils;

  @Before
  public void setUp() {
    httpUtils = new HttpUtils();
  }

  @Test
  public void parseQueryString() throws Exception {
    Map<String, String> result =
        httpUtils.parseQueryString("key=a8pz=8&stop=ID");

    assertThat(result).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_leadingQuestionMark() throws Exception {
    Map<String, String> result =
        httpUtils.parseQueryString("?key=a8pz=8&stop=ID");

    assertThat(result).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_returnsEmptyForNullInput() throws Exception {
    Map<String, String> result = httpUtils.parseQueryString(null);

    assertThat(result).isEmpty();
  }
}
